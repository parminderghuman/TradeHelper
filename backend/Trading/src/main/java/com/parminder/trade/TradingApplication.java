package com.parminder.trade;

import javax.activation.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.parminder.trade.utils.CandleUtils;

@SpringBootApplication
@EnableScheduling

public class TradingApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradingApplication.class, args);
		
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	@Autowired Environment env;



	@Bean(name = { "memoryJdbcTemplate" })
	@ConfigurationProperties(prefix = "spring.datasource-memory")
	public H2Jdbc h2Jdbc() {
		   DriverManagerDataSource dataSource = new DriverManagerDataSource();
		    dataSource.setDriverClassName(env.getProperty("spring.datasource-memory.driverClassName"));
		    dataSource.setUrl(env.getProperty("spring.datasource-memory.url"));
		    dataSource.setUsername(env.getProperty("spring.datasource-memory.username"));
		    dataSource.setPassword(env.getProperty("spring.datasource-memory.password"));
		return new H2Jdbc(new JdbcTemplate(dataSource));

	}
}
