package eu.codearte.resteeth.autoconfigure;

import eu.codearte.resteeth.annotation.RestClient;
import eu.codearte.resteeth.config.EnableResteeth;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto-configuration} for Resteeth support.
 * Equivalent to enabling {@link eu.codearte.resteeth.config.EnableResteeth} in your configuration.
 * m
 * The configuration will not be activated if {@literal resteeth.enabled=false}.
 *
 * @author Mariusz Smykula
 */
@Configuration
@ConditionalOnClass(RestClient.class)
@ConditionalOnExpression("${resteeth.enabled:true}")
@EnableResteeth
public class ResteethAutoConfiguration {

}
