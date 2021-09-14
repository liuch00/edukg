package com.edukgapp.controller;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

// RestTemplate 负责发送网络请求，这是一个配置类，整个项目中用到该类时都会按照此处的配置自动注入
@Configuration
public class RestTemplateConfig {
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.setConnectTimeout(Duration.ofMillis(5000L))
				.setReadTimeout(Duration.ofMillis(30000L))
				.build();
	}
}
