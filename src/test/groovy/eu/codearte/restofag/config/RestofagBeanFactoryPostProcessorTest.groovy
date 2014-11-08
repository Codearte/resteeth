package eu.codearte.restofag.config

import eu.codearte.restofag.config.qualifier.RestInterfaceWithQualifier
import eu.codearte.restofag.config.sample.RestInterfaceWithCustomQualifier
import eu.codearte.restofag.config.sample.SampleEndpoint
import eu.codearte.restofag.endpoint.EndpointProvider
import eu.codearte.restofag.endpoint.StubEndpointProvider
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spock.lang.Specification

/**
 * @author Jakub Kubrynski
 */
class RestofagBeanFactoryPostProcessorTest extends Specification {

	@Configuration
	@EnableRestofag(basePackages = "eu.codearte.restofag.config.sample")
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
			exception.message.contains("Cannot find proper for eu.codearte.restofag.config.sample.RestInterface")
	}

	@Configuration
	@EnableRestofag(basePackages = "eu.codearte.restofag.config.sample")
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
	@EnableRestofag(basePackages = "eu.codearte.restofag.config.qualifier")
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
}
