package eu.codearte.resteeth.config

import eu.codearte.resteeth.TestObjectWrapper
import eu.codearte.resteeth.annotation.RestClient
import eu.codearte.resteeth.config.constructor.TestBean
import eu.codearte.resteeth.endpoint.EndpointProvider
import eu.codearte.resteeth.endpoint.StubEndpointProvider
import eu.codearte.resteeth.sample.RestClientInterface
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import spock.lang.Specification

/**
 * @author Jakub Kubrynski
 */
class ResteethBeanFactoryPostProcessorTest extends Specification {

	@Configuration
	@EnableResteeth
	static class SampleConfigurationInject {

		@RestClient
		private RestClientInterface restClientInterface

		@Bean
		TestObjectWrapper objectWrapper() {
			new TestObjectWrapper(restClientInterface)
		}

		@Bean
		EndpointProvider endpointProvider() {
			new StubEndpointProvider()
		}
	}

	def "should inject RestClientInterface into field"() {
		given:
			def context = new AnnotationConfigApplicationContext(SampleConfigurationInject)
		when:
			def bean = context.getBean(TestObjectWrapper)
		then:
			bean != null
			bean.target instanceof RestClientInterface
	}

	@Configuration
	@EnableResteeth
	static class SampleMethodInject {

		@Bean
		TestObjectWrapper objectWrapper(@RestClient RestClientInterface restClientInterface) {
			new TestObjectWrapper(restClientInterface)
		}

		@Bean
		EndpointProvider endpointProvider() {
			new StubEndpointProvider()
		}
	}

	def "should inject RestClientInterface into method parameter"() {
		given:
			def context = new AnnotationConfigApplicationContext(SampleMethodInject)
		when:
			def bean = context.getBean(TestObjectWrapper)
		then:
			bean != null
			bean.target instanceof RestClientInterface
	}

	@Configuration
	@EnableResteeth
	@ComponentScan("eu.codearte.resteeth.config.constructor")
	static class ConstructorInjectionConfiguration {

		@Bean
		EndpointProvider endpointProvider() {
			new StubEndpointProvider()
		}
	}

	def "should inject RestClientInterface into constructor"() {
		given:
			def context = new AnnotationConfigApplicationContext(ConstructorInjectionConfiguration)
		when:
			def bean = context.getBean(TestBean)
		then:
			bean != null
			bean.restClientInterface instanceof RestClientInterface
	}

}
