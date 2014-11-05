package eu.codearte.restofag

import eu.codearte.restofag.annotation.EnableRestofag
import eu.codearte.restofag.endpoint.EndpointProvider
import eu.codearte.restofag.endpoint.StubEndpointProvider
import eu.codearte.restofag.sample.RestClientInterface
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spock.lang.Specification

/**
 * @author Jakub Kubrynski
 */
class RestofagBeanFactoryPostProcessorTest extends Specification {

	@Configuration
	@EnableRestofag
	static class SampleRawConfiguration {
		@Bean
		EndpointProvider endpointProvider() {
			new StubEndpointProvider()
		}
	}

	def "should contain RestClientInterface with default @EnableRestofag"() {
		given:
			def context = new AnnotationConfigApplicationContext(SampleRawConfiguration)
		when:
			def bean = context.getBean(RestClientInterface)
		then:
			bean != null
			bean instanceof RestClientInterface
	}

	@Configuration
	@EnableRestofag(basePackages = "eu.codearte.restofag.sample")
	static class SampleBasePackagesConfiguration {
		@Bean
		EndpointProvider endpointProvider() {
			new StubEndpointProvider()
		}
	}

	def "should contain RestClientInterface with @EnableRestofag containing basePackages"() {
		given:
			def context = new AnnotationConfigApplicationContext(SampleBasePackagesConfiguration)
		when:
			def bean = context.getBean(RestClientInterface)
		then:
			bean != null
			bean instanceof RestClientInterface
	}

}
