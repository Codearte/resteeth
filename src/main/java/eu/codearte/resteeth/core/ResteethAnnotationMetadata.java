package eu.codearte.resteeth.core;

import eu.codearte.resteeth.annotation.LogScope;
import org.springframework.core.annotation.AnnotationAttributes;

import java.lang.annotation.Annotation;

/**
 * @author Jakub Kubrynski
 */
public class ResteethAnnotationMetadata {

	private final LogScope logScope;

	ResteethAnnotationMetadata(LogScope logScope) {
		this.logScope = logScope;
	}

	public LogScope getLoggingScope() {
		return logScope;
	}
}
