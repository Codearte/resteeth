package eu.codearte.resteeth.handlers;

import eu.codearte.resteeth.annotation.StaticHeader;
import eu.codearte.resteeth.annotation.StaticHeaders;
import eu.codearte.resteeth.core.RestInvocation;
import org.springframework.web.bind.annotation.RequestHeader;

import java.lang.annotation.Annotation;

/**
 * @author Jakub Kubrynski
 */
public class HeadersHandler implements RestInvocationHandler {

	@Override
	public Object proceed(RestInvocation invocation) {
		addDynamicHeaders(invocation);
		addStaticHeaders(invocation);
		return invocation.proceed();
	}

	private void addDynamicHeaders(RestInvocation invocation) {
		Annotation[][] parametersAnnotations = invocation.getMethod().getParameterAnnotations();
		for (int i = 0; i < parametersAnnotations.length; i++) {
			Annotation[] parameterAnnotation = parametersAnnotations[i];
			for (Annotation annotation : parameterAnnotation) {
				if (RequestHeader.class.isAssignableFrom(annotation.annotationType())) {
					RequestHeader requestHeader = (RequestHeader) annotation;
					invocation.getDynamicHeaders().add(requestHeader.value(), String.valueOf(invocation.getArguments()[i]));
				}
			}
		}
	}

	private void addStaticHeaders(RestInvocation invocation) {
		//FIXME should read annotation from metadata - then also interface could be annotated
		StaticHeader staticHeader = invocation.getMethod().getAnnotation(StaticHeader.class);
		if (staticHeader != null) {
			invocation.getMetadata().getHttpHeaders().add(staticHeader.name(), staticHeader.value());
		}
		StaticHeaders staticHeaders = invocation.getMethod().getAnnotation(StaticHeaders.class);
		if (staticHeaders != null) {
			for (StaticHeader header : staticHeaders.value()) {
				invocation.getMetadata().getHttpHeaders().add(header.name(), header.value());
			}
		}
	}

	@Override
	public int getOrder() {
		return 90;
	}
}
