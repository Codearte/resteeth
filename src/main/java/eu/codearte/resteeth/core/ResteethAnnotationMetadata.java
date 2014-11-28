package eu.codearte.resteeth.core;

import eu.codearte.resteeth.annotation.LogScope;
import eu.codearte.resteeth.annotation.RestClient;
import org.springframework.core.annotation.AnnotationAttributes;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jakub Kubrynski
 */
public class ResteethAnnotationMetadata {

	private final LogScope logScope;
	private Map<Class, Annotation> annotations;

	ResteethAnnotationMetadata(AnnotationAttributes enableResteethAttributes,
	                                  List<Annotation> restClientAnnotations, List<Annotation> interfaceAnnotations) {
		RestClient restClientAnnotation = null;
		for (Annotation annotation : restClientAnnotations) {
			if (annotation.annotationType() == RestClient.class) {
				restClientAnnotation = (RestClient) annotation;
				break;
			}
		}

		if (restClientAnnotation == null) {
			throw new IllegalStateException("No RestClient annotation found");
		}

		LogScope logScopeVar = enableResteethAttributes.getEnum("loggingScope");

		LogScope[] logScopes = restClientAnnotation.loggingScope();
		if (logScopes.length > 0) {
			logScopeVar = logScopes[0];
		}

		this.logScope = logScopeVar;

		annotations = new HashMap<>();
		for (Annotation annotation : interfaceAnnotations) {
			annotations.put(annotation.annotationType(), annotation);
		}
		for (Annotation annotation : restClientAnnotations) {
			annotations.put(annotation.annotationType(), annotation);
		}
	}

	public LogScope getLoggingScope() {
		return logScope;
	}

	public Map<Class, Annotation> getAnnotations() {
		return annotations;
	}
}
