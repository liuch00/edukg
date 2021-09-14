package com.edukgapp.controller;

import com.edukgapp.database.AppInfo;
import com.edukgapp.database.AppInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

// 测试控制器，生产环境下应在安全组件中将该接口组禁用
@RestController
@RequestMapping("/test")
public class TestController {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AppInfoRepository appInfoRepository;

	// 设置 AppInfo，数据库初始化使用
	@PostMapping("/appinfo")
	public ResponseEntity<String> setAppInfo(@RequestBody AppInfo body) {
		appInfoRepository.save(body);
		return ResponseEntity.ok("Success");
	}

	// 查看 AppInfo
	@GetMapping("/appinfo")
	public ResponseEntity<AppInfo> getAppInfo() {
		AppInfo appInfo = appInfoRepository.findAll().iterator().next();
		return ResponseEntity.ok(appInfo);
	}
}
