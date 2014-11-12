package eu.codearte.resteeth.config.boot;

import eu.codearte.resteeth.annotation.RestClient;
import eu.codearte.resteeth.config.EnableResteeth;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(RestClient.class)
@ConditionalOnExpression("${resteeth.enabled:true}")
@EnableResteeth
public class ResteethBootConfiguration {

}
