package eu.codearte.resteeth.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jakub Kubrynski
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Documented
@Autowired
public @interface RestClient {

	String[] endpoints() default {};

	LogScope[] loggingScope() default {};
}
