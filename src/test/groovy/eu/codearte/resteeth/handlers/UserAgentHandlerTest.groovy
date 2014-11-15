package eu.codearte.resteeth.handlers

import eu.codearte.resteeth.annotation.RestClient
import eu.codearte.resteeth.config.EnableResteeth
import eu.codearte.resteeth.core.sample.RestClientWithMethods
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@ContextConfiguration(classes = LoggingConfiguration)
class UserAgentHandlerTest extends Specification {

	public static final String USER_AGENT_NAME = "Test client"

	@RestClient(endpoints = "http://localhost")
	private RestClientWithMethods client

	@Autowired
	RestTemplate restTemplate

	MockRestServiceServer mockServer

	void setup() {
		mockServer = MockRestServiceServer.createServer(restTemplate)
	}

	def 'should add User-Agent header to request'() {
		given:
			mockServer.expect(
					requestTo("http://localhost/users/1"))
					.andExpect(header(HttpHeaders.USER_AGENT, USER_AGENT_NAME))
					.andRespond(withSuccess())
		when:
			client.deleteUser(1)

		then:
			mockServer.verify()
	}

}

@Configuration
@EnableResteeth
class LoggingConfiguration {

	@Bean
	UserAgentHandler userAgentHandler() {
		return new UserAgentHandler(UserAgentHandlerTest.USER_AGENT_NAME)
	}

}