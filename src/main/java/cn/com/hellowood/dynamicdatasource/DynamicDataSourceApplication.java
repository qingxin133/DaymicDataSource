package cn.com.hellowood.dynamicdatasource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import cn.com.hellowood.dynamicdatasource.configuration.SpringUtil;

@ComponentScan(basePackages={"cn.com.hellowood.dynamicdatasource"})
@SpringBootApplication
@EnableScheduling
@Import(SpringUtil.class)
public class DynamicDataSourceApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(DynamicDataSourceApplication.class, args);
	}
}
