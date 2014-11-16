package eu.codearte.resteeth.config

import eu.codearte.resteeth.annotation.RestClient
import eu.codearte.resteeth.config.attributes.RestClientWithEndpoints
import eu.codearte.resteeth.config.qualifier.RestInterfaceWithQualifier
import eu.codearte.resteeth.config.sample.RestInterfaceWithCustomQualifier
import eu.codearte.resteeth.config.sample.SampleEndpoint
import eu.codearte.resteeth.endpoint.EndpointProvider
import eu.codearte.resteeth.endpoint.StubEndpointProvider
import org.springframework.beans.factory.BeanCreationException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spock.lang.Specification

/**
 * @author Jakub Kubrynski
 */
class EndpointProviderResolverTest extends Specification {

	@Configuration
	@EnableResteeth
	static class SampleConfigurationWithoutProperEndpointProvider {

		@RestClient
		private RestInterfaceWithCustomQualifier customQualifier

		@Bean
		EndpointProvider endpointProvider() {
			new StubEndpointProvider()
		}
	}

	def "should throw exception when no proper EndpointProvider is found"() {
		when:
			def context = new AnnotationConfigApplicationContext(SampleConfigurationWithoutProperEndpointProvider)
			context.getBean(RestInterfaceWithCustomQualifier.class)
		then:
			def exception = thrown(BeanCreationException)
			exception.message.contains("Cannot find proper for eu.codearte.resteeth.config.sample.RestInterfaceWithCustomQualifier")
	}

	@Configuration
	@EnableResteeth
	static class SampleCustomQualifierConfiguration {

		@RestClient
		RestInterfaceWithCustomQualifier restInterfaceWithCustomQualifier

		@Bean
		@SampleEndpoint
		EndpointProvider endpointProvidera() {
			new StubEndpointProvider()
		}

		@Bean
		@Qualifier("test2")
		EndpointProvider endpointProvider2() {
			new StubEndpointProvider()
		}
	}

	def "should find proper EndpointProvided using @SampleEndpoint annotation"() {
		when:
			new AnnotationConfigApplicationContext(SampleCustomQualifierConfiguration)
		then:
			// check if proper endpoint is injected
			noExceptionThrown()
	}

	@Configuration
	@EnableResteeth
	static class SampleQualifierConfiguration {

		@RestClient
		RestInterfaceWithQualifier restInterfaceWithQualifier

		@Bean
		@Qualifier("test")
		EndpointProvider endpointProvider() {
			new StubEndpointProvider()
		}

		@Bean
		@Qualifier("test2")
		EndpointProvider endpointProvider2() {
			new StubEndpointProvider()
		}
	}

	def "should find proper EndpointProvided using @Qualifier annotation"() {
		when:
			new AnnotationConfigApplicationContext(SampleQualifierConfiguration)
		then:
			// check if proper endpoint is injected
			noExceptionThrown()
	}

	@Configuration
	@EnableResteeth
	static class SampleEndpointsAttributeConfiguration {

		@RestClient(endpoints = ["http://test"])
		RestClientWithEndpoints restClientWithFixedEndpoints

		@RestClient(endpoints = ["http://test", "http://test2"])
		RestClientWithEndpoints restClientWithRoundRobinEndpoint
	}

	def "should create fixed EndpointProvided from RestClient.endpoints() attribute"() {
		when:
			new AnnotationConfigApplicationContext(SampleEndpointsAttributeConfiguration)
		then:
			// check if proper endpoint is injected
			noExceptionThrown()
	}

	def "should create round robin EndpointProvided from RestClient.endpoints() attribute"() {
		when:
			new AnnotationConfigApplicationContext(SampleEndpointsAttributeConfiguration)
		then:
			// check if proper endpoint is injected
			noExceptionThrown()
	}
}
