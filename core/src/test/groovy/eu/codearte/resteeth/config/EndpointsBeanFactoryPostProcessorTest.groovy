package eu.codearte.resteeth.config

import eu.codearte.resteeth.endpoint.EndpointProvider
import eu.codearte.resteeth.endpoint.StubEndpointProvider
import eu.codearte.resteeth.sample.RestClientInterface
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * @author Jakub Kubrynski
 */
class EndpointsBeanFactoryPostProcessorTest extends Specification {

	@Configuration
	@EnableResteeth
	static class SampleRawConfiguration {
		@Bean
		EndpointProvider endpointProvider() {
			new StubEndpointProvider()
		}
	}

	@Ignore("How to exclude beans from full component scan?")
	def "should contain RestClientInterface with default @EnableResteeth"() {
		given:
			def context = new AnnotationConfigApplicationContext(SampleRawConfiguration)
		when:
			def bean = context.getBean(RestClientInterface)
		then:
			bean != null
			bean instanceof RestClientInterface
	}

	@Configuration
	@EnableResteeth(basePackages = "eu.codearte.resteeth.sample")
	static class SampleBasePackagesConfiguration {
		@Bean
		EndpointProvider endpointProvider() {
			new StubEndpointProvider()
		}
	}

	def "should contain RestClientInterface with @EnableResteeth containing basePackages"() {
		given:
			def context = new AnnotationConfigApplicationContext(SampleBasePackagesConfiguration)
		when:
			def bean = context.getBean(RestClientInterface)
		then:
			bean != null
			bean instanceof RestClientInterface
	}

}
