package com.fitassist.backend.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response,
			@NotNull Object handler) {
		request.setAttribute("startTime", System.currentTimeMillis());
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, @NotNull Object handler,
			Exception ex) {
		long duration = System.currentTimeMillis() - (long) request.getAttribute("startTime");
		int userId = AuthorizationUtil.getUserId();

		log.atInfo()
			.addKeyValue("userId", userId)
			.addKeyValue("status", response.getStatus())
			.addKeyValue("method", request.getMethod())
			.addKeyValue("uri", request.getRequestURI())
			.addKeyValue("durationMs", duration)
			.log("Request completed");
	}

}
