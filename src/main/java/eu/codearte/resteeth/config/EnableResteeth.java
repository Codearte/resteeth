package eu.codearte.resteeth.config;

import eu.codearte.resteeth.annotation.LogScope;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jakub Kubrynski
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ResteethDefinitionRegistrar.class, ResteethConfiguration.class})
public @interface EnableResteeth {

	LogScope loggingScope() default LogScope.INVOCATION_ONLY;

}