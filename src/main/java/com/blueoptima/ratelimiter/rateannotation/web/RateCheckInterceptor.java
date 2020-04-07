package com.blueoptima.ratelimiter.rateannotation.web;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.blueoptima.ratelimiter.rateannotation.Limiter;
import com.blueoptima.ratelimiter.rateannotation.RedisProperties;
import com.blueoptima.ratelimiter.rateannotation.UserBasedConfiguration;
import com.blueoptima.ratelimiter.rateannotation.core.RateCheckCallableTask;
import com.blueoptima.ratelimiter.rateannotation.dynamic.DynamicLimiter;
import com.blueoptima.ratelimiter.rateannotation.dynamic.LimiterConfig;
import com.blueoptima.ratelimiter.rateannotation.dynamic.ReddisProcessor;
import com.blueoptima.ratelimiter.rateannotation.event.RateExceedingEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Configuration
public class RateCheckInterceptor implements HandlerInterceptor, ApplicationContextAware {

	private final RedisProperties redisLimiterProperties;
	private final RateCheckCallableTask rateCheckTaskRunner;
	private final ReddisProcessor redisLimiterConfigProcessor;
	private ApplicationContext applicationContext;

	@Autowired
	private UserBasedConfiguration userBasedConfiguration;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}
		boolean isSuccess = true;
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();
		if (method.isAnnotationPresent(Limiter.class)) {
			isSuccess = handleStatic(method, request, response);
		} else if (method.isAnnotationPresent(DynamicLimiter.class)) {
			isSuccess = handleDynamic(method, request, response);
		}
		return isSuccess;
	}

	private boolean handleStatic(Method method, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Limiter rateLimiterAnnotation = method.getAnnotation(Limiter.class);

		TimeUnit timeUnit = rateLimiterAnnotation.timeUnit();
		String path = rateLimiterAnnotation.path();
		if ("".equals(path)) {
			path = request.getRequestURI();
		}

		String baseExp = rateLimiterAnnotation.base();
		String baseVal = "";
		if (!"".equals(baseExp)) {
			baseVal = eval(baseExp, request);
		}

		int permits = userBasedConfiguration.getUserBasedPermits().get(baseVal) != null
				? Integer.valueOf(userBasedConfiguration.getUserBasedPermits().get(baseVal))
				: rateLimiterAnnotation.permits();

		String rateLimiterKey = redisLimiterProperties.getRedisKeyPrefix() + ":" + path + ":" + baseVal;
		boolean isSuccess = rateCheckTaskRunner.checkRun(rateLimiterKey, timeUnit, permits);

		if (!isSuccess) {
			rateExceeded(method, response, baseExp, baseVal, path, permits, timeUnit.name());
		}
		return isSuccess;
	}

	private boolean handleDynamic(Method method, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isSuccess = true;
		String limiterConfigKey = method.getDeclaringClass().getSimpleName() + ":" + method.getName();
		LimiterConfig limiterConfig = redisLimiterConfigProcessor.get(limiterConfigKey);
		if (limiterConfig != null) {
			String baseExp = limiterConfig.getBaseExp();
			String baseVal = "";
			if (!"".equals(baseExp)) {
				baseVal = eval(baseExp, request);
			}
			String path = limiterConfig.getPath();
			if ("".equals(path)) {
				path = request.getRequestURI();
			}
			int permits = limiterConfig.getPermits();
			String timeUnit = limiterConfig.getTimeUnit();
			String rateLimiterKey = redisLimiterProperties.getRedisKeyPrefix() + ":" + path + ":" + baseVal;
			isSuccess = rateCheckTaskRunner.checkRun(rateLimiterKey, TimeUnit.valueOf(timeUnit), permits);
			if (!isSuccess) {
				rateExceeded(method, response, baseExp, baseVal, path, permits, timeUnit);
			}
		}
		return isSuccess;
	}

	private void rateExceeded(Method method, HttpServletResponse response, String baseExp, String baseVal, String path,
			int permits, String timeUnit) throws Exception {
		buildDenyResponse(response);
		RateExceedingEvent rateExceedingEvent = new RateExceedingEvent();
		rateExceedingEvent.setControllerName(method.getDeclaringClass().getSimpleName());
		rateExceedingEvent.setMethodName(method.getName());
		rateExceedingEvent.setBaseExp(baseExp);
		rateExceedingEvent.setBaseValue(baseVal);
		rateExceedingEvent.setPath(path);
		rateExceedingEvent.setPermits(permits);
		rateExceedingEvent.setTimeUnit(timeUnit);
		applicationContext.publishEvent(rateExceedingEvent);
	}

	private String eval(String baseExp, HttpServletRequest request) {
		StandardEvaluationContext context = new StandardEvaluationContext();
		ExpressionParser expressionParser = new SpelExpressionParser();
		mountCookies(request, context);
		mountHeaders(request, context);
		Expression expression = expressionParser.parseExpression(baseExp);
		String baseVal = expression.getValue(context, String.class);
		if (baseVal == null) {
			baseVal = "";
		}
		return baseVal;
	}

	private void mountCookies(HttpServletRequest request, StandardEvaluationContext context) {
		HashMap<String, String> cookieMap = new HashMap<>();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				cookieMap.put(cookie.getName(), cookie.getValue());
			}
		}
		context.setVariable("Cookies", cookieMap);
	}

	private void mountHeaders(HttpServletRequest request, StandardEvaluationContext context) {
		HashMap<String, String> headerMap = new HashMap();
		Enumeration<String> headerNames = request.getHeaderNames();
		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();
				headerMap.put(headerName, request.getHeader(headerName));
			}
		}
		context.setVariable("Headers", headerMap);
	}

	private void buildDenyResponse(HttpServletResponse response) throws Exception {
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.getWriter().print("Access denied because of exceeding access rate");
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
	}

}
