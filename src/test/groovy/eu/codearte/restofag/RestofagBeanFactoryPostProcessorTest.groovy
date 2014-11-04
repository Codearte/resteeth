package eu.codearte.restofag

import eu.codearte.restofag.annotation.EnableRestofag
import eu.codearte.restofag.sample.RestClientInterface
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Specification

/**
 * @author Jakub Kubrynski
 */
class RestofagBeanFactoryPostProcessorTest extends Specification {

	@EnableRestofag
	static class SampleRawConfiguration {
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

	@EnableRestofag(basePackages = "eu.codearte.restofag.sample")
	static class SampleBasePackagesConfiguration {
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
