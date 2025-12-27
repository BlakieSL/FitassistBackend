package source.code.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/virtual-threads")
public class VirtualThreadsController {

	@GetMapping("/thread-info")
	public Map<String, Object> threadInfo() {
		Thread t = Thread.currentThread();
		return Map.of("id", t.threadId(), "isVirtual", t.isVirtual(), "name", t.getName());
	}

}
