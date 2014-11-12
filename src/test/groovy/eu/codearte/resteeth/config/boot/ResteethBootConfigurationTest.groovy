package eu.codearte.resteeth.config.boot

import eu.codearte.resteeth.annotation.RestClient
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Specification

/**
 * @author Jakub Kubrynski
 */

@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
@IntegrationTest
class ResteethBootConfigurationTest extends Specification {

	@Configuration
	@EnableAutoConfiguration
	@ComponentScan("eu.codearte.resteeth.config.boot")
	static class Application {
	}

	interface EchoClient {
		@RequestMapping(value = "/echo/{message}", method = RequestMethod.GET)
		String echo(@PathVariable("message") String message);
	}

	@RestClient(endpoints = "http://localhost:8080")
	EchoClient echoClient

	def "should send and receive response from EchoServer"() {
		when:
			def echo = echoClient.echo("foo")
		then:
			echo == "foo"
	}
}
