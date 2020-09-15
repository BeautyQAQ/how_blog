package com.liushao;
import com.liushao.util.IdWorker;
import com.liushao.util.JwtUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
//@EnableEurekaClient     //Eureka客户端
//@EnableDiscoveryClient  //发现服务
//@EnableFeignClients     //调用服务
public class QaApplication {

	public static void main(String[] args) {
		SpringApplication.run(QaApplication.class, args);
	}

	@Bean
	public IdWorker idWorker(){
		return new IdWorker(1, 1);
	}

	@Bean
	public JwtUtil jwtUtil() {
		return new JwtUtil();
	}

}
