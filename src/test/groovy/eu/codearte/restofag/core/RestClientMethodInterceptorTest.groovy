package eu.codearte.restofag.core

import eu.codearte.restofag.core.sample.RestClientWithMethods
import eu.codearte.restofag.core.sample.RestMethodsConfig
import eu.codearte.restofag.core.sample.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.withCreatedEntity
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

/**
 * @author Jakub Kubrynski
 */
@ContextConfiguration(classes = RestMethodsConfig)
class RestClientMethodInterceptorTest extends Specification {

	@Autowired
	RestClientWithMethods restClient

	@Autowired
	RestTemplate restTemplate

	def "should invoke get method"() {
		given:
			MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate)

			mockServer.expect(requestTo("/users/42")).andExpect(method(HttpMethod.GET))
					.andRespond(withSuccess("{ \"id\" : \"42\", \"name\" : \"John\"}", MediaType.APPLICATION_JSON))

		when:
			User user = restClient.getWithSingleParameter(42)

		then:
			mockServer.verify()
			user.id == 42
			user.name == "John"
	}

	def "should invoke get method with two parameters"() {
		given:
			MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate)

			mockServer.expect(requestTo("/users/42/staff/123")).andExpect(method(HttpMethod.GET))
					.andRespond(withSuccess("{ \"id\" : \"42\", \"name\" : \"John\"}", MediaType.APPLICATION_JSON))

		when:
			User user = restClient.getWithTwoParameters(123, 42)

		then:
			mockServer.verify()
			user.id == 42
			user.name == "John"
	}

	def "should invoke post method"() {
		given:
			MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate)

			mockServer.expect(requestTo("/users")).andExpect(method(HttpMethod.POST))
					.andExpect(MockRestRequestMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andRespond(withSuccess())

		when:
			restClient.postToUsers(new User(name: "test"))

		then:
			mockServer.verify()
	}

	def "should invoke post method with path parameter"() {
		given:
			MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate)

			mockServer.expect(requestTo("/users/135/staff")).andExpect(method(HttpMethod.POST))
					.andExpect(MockRestRequestMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andRespond(withCreatedEntity(new URI("http://localhost/users/42")))

		when:
			restClient.postToUsersStaff(135, new User(name: "test"))

		then:
			mockServer.verify()
	}
}
