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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void applicationDefaultsKeepBothNacosClientsInThePublicNamespace() throws Exception {
        for (Map.Entry<String, String> application : APPLICATIONS.entrySet()) {
            ConfigurableEnvironment environment = environment(application.getKey(), null);
            assertEquals(application.getValue(), environment.getProperty("spring.application.name"));
            assertEquals("dev", environment.getProperty("spring.profiles.active"));
            assertEquals("nacos:" + application.getValue() + "-dev.yaml?group=DEFAULT_GROUP&refreshEnabled=true",
                    environment.getProperty("spring.config.import"));
            assertNacosProperties(environment, "192.168.20.10:8848", "", "DEFAULT_GROUP");
        }
    }

    @Test
    void applicationContainsNacosCredentialsWithoutEnvironmentVariables() throws Exception {
        for (String module : APPLICATIONS.keySet()) {
            ConfigurableEnvironment environment = environment(module, null);
            assertEquals("nacos", environment.getRequiredProperty("spring.cloud.nacos.config.username"), module);
            assertEquals("ssyhsup", environment.getRequiredProperty("spring.cloud.nacos.discovery.password"), module);
        }
    }

    @Test
    void remoteDocumentsPreserveTheSameAuthenticatedNacosConnection() throws Exception {
        for (String directory : Arrays.asList("nacos-public", "nacos-dev")) {
            for (Map.Entry<String, String> application : APPLICATIONS.entrySet()) {
                Path yaml = repositoryRoot().resolve("doc").resolve(directory)
                        .resolve(application.getValue() + "-dev.yaml");
                ConfigurableEnvironment environment = environment(application.getKey(), yaml);
                assertEquals(application.getValue(), environment.getProperty("spring.application.name"));
                assertNacosProperties(environment, "192.168.20.10:8848", "", "DEFAULT_GROUP");
                assertAllPlaceholdersResolve(environment);
                if (Arrays.asList("how_article", "how_user").contains(application.getKey())) {
                    assertTrue(environment.getRequiredProperty("spring.datasource.url")
                            .startsWith("jdbc:mysql://192.168.20.5:3306/how-blog?"));
                    assertEquals("root", environment.getProperty("spring.datasource.username"));
                    assertEquals("ssyhsup", environment.getProperty("spring.datasource.password"));
                    assertEquals("none", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
                }
                if (Arrays.asList("how_article", "how_user", "how_search", "how_gateway_web")
                        .contains(application.getKey())) {
                    assertEquals("P1AXH29zhYz/U7v0Ft4jWjUs3myjsVBfd6SnhzoTNO4=",
                            environment.getProperty("jwt.config.key"));
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
        assertTrue(coordinates.getProperty("version").startsWith("3."),
                "Use the 3.x client managed by the Spring Cloud Alibaba BOM");
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
            ConfigurableEnvironment environment = environment("how_gateway_web", yaml);
            Set<String> targets = new HashSet<>();
            for (int i = 0; i < 3; i++) {
                targets.add(environment.getRequiredProperty(
                        "spring.cloud.gateway.server.webflux.routes[" + i + "].uri"));
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
        // Spring injects this field in production; property enrichment also reads it.
        ReflectionTestUtils.setField(discovery, "environment", environment);
        // Production init() calls this to fill optional fields (logName, endpoint, accessKey)
        // before assembling Properties. Invoke this step without init()'s host detection.
        discovery.overrideFromEnv(environment);
        // Assemble Properties only; do not start a naming client.
        Properties discoveryClient = discovery.getNacosProperties();

        assertEquals(group, config.getGroup());
        assertEquals(group, discovery.getGroup());
        for (Properties properties : Arrays.asList(configClient, discoveryClient)) {
            assertEquals(server, properties.getProperty(PropertyKeyConst.SERVER_ADDR));
            assertEquals(namespace, properties.getProperty(PropertyKeyConst.NAMESPACE));
            // An empty SCA contextPath overrides the client's /nacos default and breaks login URLs.
            assertEquals("/nacos", properties.getProperty("contextPath"));
            assertEquals("nacos", properties.getProperty("username"));
            assertEquals("ssyhsup", properties.getProperty("password"));
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

    private ConfigurableEnvironment environment(String module, Path yaml)
            throws IOException {
        StandardEnvironment environment = new StandardEnvironment();
        // Read the checked-in intranet values independently of the developer's process environment.
        environment.getPropertySources().remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
        environment.getPropertySources().remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
        if (yaml != null) {
            for (PropertySource<?> source : new YamlPropertySourceLoader()
                    .load("remote-config", new FileSystemResource(yaml.toFile()))) {
                environment.getPropertySources().addLast(source);
            }
        }
        Properties application = new Properties();
        try (Reader reader = Files.newBufferedReader(repositoryRoot().resolve(module)
                .resolve("src/main/resources/application.properties"), StandardCharsets.UTF_8)) {
            application.load(reader);
        }
        environment.getPropertySources().addLast(new PropertiesPropertySource("application", application));
        return environment;
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
