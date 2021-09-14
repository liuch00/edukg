package com.edukgapp.controller;

import com.edukgapp.controller.neo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

// 与图算法相关的控制器，将请求导向算法端
@RestController
@RequestMapping("/neo")
public class GraphController {

	@Autowired
	private RestTemplate restTemplate;

	// 获取大纲
	@PostMapping("/framework")
	public ResponseEntity<?> getFramework(
			@RequestBody InputOne body
			) {
		String target = "http://127.0.0.1:5000/get-framework";

		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("uri", body.getUri());

		FrameworkResponse response = restTemplate.postForObject(
				target, requestBody, FrameworkResponse.class
		);

		return ResponseEntity.ok(response);
	}

	// 路径搜索
	@PostMapping("/path")
	public ResponseEntity<?> getPath(
			@RequestBody InputTwo body
			) {
		String target = "http://127.0.0.1:5000/get-path";

		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("s", body.getS());
		requestBody.add("t", body.getT());

		PathResponse[][] response = restTemplate.postForObject(
				target, requestBody, PathResponse[][].class
		);

		return ResponseEntity.ok(response);
	}

	// 实体推荐
	@PostMapping("/recommendation")
	public ResponseEntity<?> getRecommendation(
			@RequestBody InputList body
			) {
		String target = "http://127.0.0.1:5000/get-recommendation";

		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("history", body.getHistory());
		requestBody.add("course", body.getCourse());

		RecommendationResponse[] response = restTemplate.postForObject(
				target, requestBody, RecommendationResponse[].class
		);

		return ResponseEntity.ok(response);
	}
}
