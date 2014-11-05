package eu.codearte.restofag.core.sample

import eu.codearte.restofag.annotation.EnableRestofag
import eu.codearte.restofag.endpoint.EndpointProvider
import eu.codearte.restofag.endpoint.StubEndpointProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author Jakub Kubrynski
 */
@EnableRestofag(basePackages = "eu.codearte.restofag.core.sample")
@Configuration
class RestMethodsConfig {

	@Bean
	public EndpointProvider endpointProvider() {
		return new StubEndpointProvider();
	}


}
