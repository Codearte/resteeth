package eu.codearte.resteeth.core.sample

import eu.codearte.resteeth.config.EnableResteeth
import eu.codearte.resteeth.endpoint.EndpointProvider
import eu.codearte.resteeth.endpoint.StubEndpointProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author Jakub Kubrynski
 */
@EnableResteeth(basePackages = "eu.codearte.resteeth.core.sample")
@Configuration
class RestMethodsConfig {

	@Bean
	public EndpointProvider endpointProvider() {
		return new StubEndpointProvider();
	}

}
