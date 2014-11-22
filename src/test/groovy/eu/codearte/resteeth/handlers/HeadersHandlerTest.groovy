package eu.codearte.resteeth.handlers

import eu.codearte.resteeth.annotation.RestClient
import eu.codearte.resteeth.core.sample.RestClientHeaders
import eu.codearte.resteeth.core.sample.RestMethodsConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

/**
 * @author Jakub Kubrynski
 */
@ContextConfiguration(classes = RestMethodsConfig)
class HeadersHandlerTest extends Specification {

	@RestClient
	RestClientHeaders restClient

	@Autowired
	RestTemplate restTemplate

	MockRestServiceServer mockServer

	void setup() {
		mockServer = MockRestServiceServer.createServer(restTemplate)
	}

	def "should add dynamic header"() {
		given:
			mockServer.expect(header("testHeaderName", "testHeaderValue"))
					.andRespond(withSuccess("{}", MediaType.APPLICATION_JSON))

		when:
			restClient.getUserDynamicHeader("testHeaderValue", 42)

		then:
			mockServer.verify()
	}

	def "should add static header"() {
		given:
			mockServer.expect(header("testHeaderName", "testHeaderValue"))
					.andRespond(withSuccess("{}", MediaType.APPLICATION_JSON))

		when:
			restClient.getUserStaticHeader(42)

		then:
			mockServer.verify()
	}

	def "should add static headers"() {
		given:
			mockServer.expect(header("testHeaderName1", "testHeaderValue1"))
					.andExpect(header("testHeaderName2", "testHeaderValue2"))
					.andRespond(withSuccess("{}", MediaType.APPLICATION_JSON))

		when:
			restClient.getUserStaticHeaders(42)

		then:
			mockServer.verify()
	}

}
