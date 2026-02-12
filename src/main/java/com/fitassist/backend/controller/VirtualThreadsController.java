package com.fitassist.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/virtual-threads")
public class VirtualThreadsController {

	@GetMapping("/thread-info")
	public Map<String, Object> threadInfo() {
		Thread thread = Thread.currentThread();
		return Map.of("id", thread.threadId(), "isVirtual", thread.isVirtual(), "name", thread.getName());
	}

}
