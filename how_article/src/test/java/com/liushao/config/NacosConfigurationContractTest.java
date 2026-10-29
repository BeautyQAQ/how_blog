package com.liushao.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.naming.NamingService;
import com.liushao.client.SearchClient;
import com.liushao.client.UserClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises the actual Spring binding and Alibaba property enrichment paths.
 * It never creates a Nacos client, starts a Spring application or opens a socket.
 */
class NacosConfigurationContractTest {

    private static final Map<String, String> APPLICATIONS = new LinkedHashMap<>();

    static {
        APPLICATIONS.put("how_article", "how-article");
        APPLICATIONS.put("how_user", "how-user");
        APPLICATIONS.put("how_search", "how-search");
        APPLICATIONS.put("how_sms", "how-sms");
        APPLICATIONS.put("how_gateway_web", "how-gateway-web");
    }

    @Test
    void bootstrapDefaultsKeepBothNacosClientsInThePublicNamespace() throws Exception {
        for (Map.Entry<String, String> application : APPLICATIONS.entrySet()) {
            ConfigurableEnvironment environment = environment(application.getKey(), null, credentials());
            assertEquals(application.getValue(), environment.getProperty("spring.application.name"));
            assertEquals("dev", environment.getProperty("spring.profiles.active"));
            assertEquals("yaml", environment.getProperty("spring.cloud.nacos.config.file-extension"));
            assertNacosProperties(environment, "localhost:8848", "", "DEFAULT_GROUP");
        }
    }

    @Test
    void bootstrapRequiresExplicitNacosCredentials() throws Exception {
        for (String module : APPLICATIONS.keySet()) {
            ConfigurableEnvironment environment = environment(module, null, Collections.emptyMap());
            assertThrows(IllegalArgumentException.class,
                    () -> environment.getRequiredProperty("spring.cloud.nacos.config.username"), module);
            assertThrows(IllegalArgumentException.class,
                    () -> environment.getRequiredProperty("spring.cloud.nacos.discovery.password"), module);
        }
    }

    @Test
    void remoteDocumentsPreserveTheSameAuthenticatedNacosConnection() throws Exception {
        Map<String, Object> values = serviceEnvironment();
        for (String directory : Arrays.asList("nacos-public", "nacos-dev")) {
            for (Map.Entry<String, String> application : APPLICATIONS.entrySet()) {
                Path yaml = repositoryRoot().resolve("doc").resolve(directory)
                        .resolve(application.getValue() + "-dev.yaml");
                ConfigurableEnvironment environment = environment(application.getKey(), yaml, values);
                assertEquals(application.getValue(), environment.getProperty("spring.application.name"));
                assertNacosProperties(environment, "127.0.0.1:18848", "test-namespace-id", "test-group");
                assertAllPlaceholdersResolve(environment);
                if (Arrays.asList("how_article", "how_user").contains(application.getKey())) {
                    assertTrue(environment.getRequiredProperty("spring.datasource.url")
                            .startsWith("jdbc:mysql://127.0.0.1:13306/how-blog?"));
                    assertEquals("test-blog-user", environment.getProperty("spring.datasource.username"));
                    assertEquals("test-blog-password", environment.getProperty("spring.datasource.password"));
                    assertEquals("none", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
                }
                if (Arrays.asList("how_article", "how_user", "how_gateway_web")
                        .contains(application.getKey())) {
                    assertEquals(values.get("JWT_SECRET"), environment.getProperty("jwt.config.key"));
                }
            }
        }
    }

    @Test
    void upgradedClientRetainsTheFactoryContractUsedByTheAlibabaStarters() throws Exception {
        Properties coordinates = new Properties();
        try (InputStream stream = NacosFactory.class.getResourceAsStream(
                "/META-INF/maven/com.alibaba.nacos/nacos-client/pom.properties")) {
            assertNotNull(stream, "The selected nacos-client must expose its Maven coordinates");
            coordinates.load(stream);
        }
        assertTrue(coordinates.getProperty("version").startsWith("2."),
                "Use the pinned 2.x client rather than the Alibaba BOM's legacy 1.x client");
        assertEquals(ConfigService.class,
                NacosFactory.class.getMethod("createConfigService", Properties.class).getReturnType());
        assertEquals(NamingService.class,
                NacosFactory.class.getMethod("createNamingService", Properties.class).getReturnType());
        ClassLoader loader = NacosFactory.class.getClassLoader();
        assertNotNull(Class.forName("com.alibaba.nacos.client.config.NacosConfigService", false, loader)
                .getConstructor(Properties.class));
        assertNotNull(Class.forName("com.alibaba.nacos.client.naming.NacosNamingService", false, loader)
                .getConstructor(Properties.class));
    }

    @Test
    void feignAndGatewayContinueResolvingTheRegisteredServiceNames() throws Exception {
        assertFeignService(UserClient.class, "how-user");
        assertFeignService(SearchClient.class, "how-search");
        for (String directory : Arrays.asList("nacos-public", "nacos-dev")) {
            Path yaml = repositoryRoot().resolve("doc").resolve(directory)
                    .resolve("how-gateway-web-dev.yaml");
            ConfigurableEnvironment environment = environment("how_gateway_web", yaml, serviceEnvironment());
            Set<String> targets = new HashSet<>();
            for (int i = 0; i < 3; i++) {
                targets.add(environment.getRequiredProperty("spring.cloud.gateway.routes[" + i + "].uri"));
            }
            assertEquals(new HashSet<>(Arrays.asList("lb://how-article", "lb://how-user", "lb://how-search")),
                    targets);
        }
    }

    private void assertNacosProperties(ConfigurableEnvironment environment, String server,
                                       String namespace, String group) {
        NacosConfigProperties config = Binder.get(environment)
                .bind("spring.cloud.nacos.config", Bindable.of(NacosConfigProperties.class)).get();
        config.setEnvironment(environment);
        Properties configClient = config.assembleConfigServiceProperties();

        NacosDiscoveryProperties discovery = Binder.get(environment)
                .bind("spring.cloud.nacos.discovery", Bindable.of(NacosDiscoveryProperties.class)).get();
        ReflectionTestUtils.setField(discovery, "environment", environment);
        // Production init() calls this to fill optional fields (logName, endpoint, accessKey)
        // before assembling Properties. Invoke this step without init()'s host detection.
        discovery.overrideFromEnv(environment);
        // This private method assembles Properties only; namingServiceInstance would start a client.
        Properties discoveryClient = ReflectionTestUtils.invokeMethod(discovery, "getNacosProperties");

        assertEquals(group, config.getGroup());
        assertEquals(group, discovery.getGroup());
        for (Properties properties : Arrays.asList(configClient, discoveryClient)) {
            assertEquals(server, properties.getProperty(PropertyKeyConst.SERVER_ADDR));
            assertEquals(namespace, properties.getProperty(PropertyKeyConst.NAMESPACE));
            // An empty SCA contextPath overrides the client's /nacos default and breaks login URLs.
            assertEquals("/nacos", properties.getProperty("contextPath"));
            // SCA 2.2.0 forwards these through its enrichment path despite lacking Java fields.
            assertEquals("test-nacos-user", properties.getProperty("username"));
            assertEquals("test-nacos-password", properties.getProperty("password"));
        }
    }

    private void assertFeignService(Class<?> client, String service) {
        FeignClient annotation = client.getAnnotation(FeignClient.class);
        assertNotNull(annotation);
        assertEquals(service, annotation.value());
        assertEquals("", annotation.url());
    }

    private void assertAllPlaceholdersResolve(ConfigurableEnvironment environment) {
        for (PropertySource<?> source : environment.getPropertySources()) {
            if (source instanceof EnumerablePropertySource) {
                for (String name : ((EnumerablePropertySource<?>) source).getPropertyNames()) {
                    Object value = source.getProperty(name);
                    if (value instanceof String) {
                        environment.resolveRequiredPlaceholders((String) value);
                    }
                }
            }
        }
    }

    private ConfigurableEnvironment environment(String module, Path yaml, Map<String, Object> values)
            throws IOException {
        StandardEnvironment environment = new StandardEnvironment();
        // Test credentials and defaults must be independent of the developer's actual environment.
        environment.getPropertySources().remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
        environment.getPropertySources().remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
        environment.getPropertySources().addLast(new MapPropertySource("test-environment", values));
        if (yaml != null) {
            for (PropertySource<?> source : new YamlPropertySourceLoader()
                    .load("remote-config", new FileSystemResource(yaml.toFile()))) {
                environment.getPropertySources().addLast(source);
            }
        }
        Properties bootstrap = new Properties();
        try (Reader reader = Files.newBufferedReader(repositoryRoot().resolve(module)
                .resolve("src/main/resources/bootstrap.properties"), StandardCharsets.UTF_8)) {
            bootstrap.load(reader);
        }
        environment.getPropertySources().addLast(new PropertiesPropertySource("bootstrap", bootstrap));
        return environment;
    }

    private Map<String, Object> credentials() {
        Map<String, Object> values = new HashMap<>();
        values.put("NACOS_USERNAME", "test-nacos-user");
        values.put("NACOS_PASSWORD", "test-nacos-password");
        return values;
    }

    private Map<String, Object> serviceEnvironment() {
        Map<String, Object> values = credentials();
        values.put("NACOS_SERVER_ADDR", "127.0.0.1:18848");
        values.put("NACOS_NAMESPACE", "test-namespace-id");
        values.put("NACOS_GROUP", "test-group");
        values.put("MYSQL_HOST", "127.0.0.1");
        values.put("MYSQL_PORT", "13306");
        values.put("MYSQL_DATABASE", "how-blog");
        values.put("MYSQL_USERNAME", "test-blog-user");
        values.put("MYSQL_PASSWORD", "test-blog-password");
        values.put("REDIS_HOST", "127.0.0.1");
        values.put("REDIS_PASSWORD", "test-redis-password");
        values.put("RABBITMQ_HOST", "127.0.0.1");
        values.put("RABBITMQ_USERNAME", "test-rabbit-user");
        values.put("RABBITMQ_PASSWORD", "test-rabbit-password");
        values.put("ELASTICSEARCH_CLUSTER_NODES", "127.0.0.1:19300");
        values.put("JWT_SECRET", "test-jwt-secret-shared-between-all-services");
        values.put("ALIYUN_SMS_TEMPLATE_CODE", "test-template-code");
        values.put("ALIYUN_SMS_SIGN_NAME", "test-sign");
        values.put("ALIYUN_SMS_ACCESS_KEY_ID", "test-access-key");
        values.put("ALIYUN_SMS_ACCESS_KEY_SECRET", "test-access-secret");
        return values;
    }

    private Path repositoryRoot() {
        for (Path path = Paths.get("").toAbsolutePath(); path != null; path = path.getParent()) {
            if (Files.isDirectory(path.resolve("doc/nacos-public"))
                    && Files.isDirectory(path.resolve("how_article"))) {
                return path;
            }
            Path nested = path.resolve("how_blog");
            if (Files.isDirectory(nested.resolve("doc/nacos-public"))
                    && Files.isDirectory(nested.resolve("how_article"))) {
                return nested;
            }
        }
        throw new IllegalStateException("Cannot locate the how_blog repository from the test working directory");
    }
}
