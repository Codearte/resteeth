package eu.codearte.resteeth.config

import eu.codearte.resteeth.config.attributes.RestClientWithEndpoint
import eu.codearte.resteeth.config.attributes.RestClientWithEndpoints
import eu.codearte.resteeth.config.qualifier.RestInterfaceWithQualifier
import eu.codearte.resteeth.config.sample.RestInterfaceWithCustomQualifier
import eu.codearte.resteeth.config.sample.SampleEndpoint
import eu.codearte.resteeth.endpoint.EndpointProvider
import eu.codearte.resteeth.endpoint.StubEndpointProvider
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spock.lang.Specification

/**
 * @author Jakub Kubrynski
 */
class ResteethBeanFactoryPostProcessorTest extends Specification {

	@Configuration
	@EnableResteeth(basePackages = "eu.codearte.resteeth.config.sample")
	static class SampleConfigurationWithoutProperEndpointProvider {
		@Bean
		EndpointProvider endpointProvider() {
			new StubEndpointProvider()
		}
	}

	def "should throw exception when no proper EndpointProvider is found"() {
		when:
			new AnnotationConfigApplicationContext(SampleConfigurationWithoutProperEndpointProvider)
		then:
			def exception = thrown(NoSuchBeanDefinitionException)
			exception.message.contains("Cannot find proper for eu.codearte.resteeth.config.sample.RestInterface")
	}

	@Configuration
	@EnableResteeth(basePackages = "eu.codearte.resteeth.config.sample")
	static class SampleCustomQualifierConfiguration {
		@Bean
		@SampleEndpoint
		EndpointProvider endpointProvider() {
			new StubEndpointProvider()
		}

		@Bean
		@Qualifier("test2")
		EndpointProvider endpointProvider2() {
			new StubEndpointProvider()
		}
	}

	def "should find proper EndpointProvided using @SampleEndpoint annotation"() {
		given:
			def context = new AnnotationConfigApplicationContext(SampleCustomQualifierConfiguration)
		when:
			def bean = context.getBean(RestInterfaceWithCustomQualifier.class)
		then:
			// check if proper endpoint is injected
			noExceptionThrown()
			bean != null
	}

	@Configuration
	@EnableResteeth(basePackages = "eu.codearte.resteeth.config.qualifier")
	static class SampleQualifierConfiguration {
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
		given:
			def context = new AnnotationConfigApplicationContext(SampleQualifierConfiguration)
		when:
			def bean = context.getBean(RestInterfaceWithQualifier.class)
		then:
			// check if proper endpoint is injected
			noExceptionThrown()
			bean != null
	}

	@Configuration
	@EnableResteeth(basePackages = "eu.codearte.resteeth.config.attributes")
	static class SampleEndpointsAttributeConfiguration {

	}

	def "should create fixed EndpointProvided from RestClient.endpoints() attribute"() {
		given:
			def context = new AnnotationConfigApplicationContext(SampleEndpointsAttributeConfiguration)
		when:
			def bean = context.getBean(RestClientWithEndpoint.class)
		then:
			// check if proper endpoint is injected
			noExceptionThrown()
			bean != null
	}

	def "should create round robin EndpointProvided from RestClient.endpoints() attribute"() {
		given:
			def context = new AnnotationConfigApplicationContext(SampleEndpointsAttributeConfiguration)
		when:
			def bean = context.getBean(RestClientWithEndpoints.class)
		then:
			// check if proper endpoint is injected
			noExceptionThrown()
			bean != null
	}
}
