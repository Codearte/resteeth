package eu.codearte.restofag.config

import eu.codearte.restofag.endpoint.EndpointProvider
import eu.codearte.restofag.endpoint.StubEndpointProvider
import org.springframework.beans.factory.NoSuchBeanDefinitionException
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
}
