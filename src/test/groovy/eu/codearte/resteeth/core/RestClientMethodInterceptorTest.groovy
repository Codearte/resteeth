package eu.codearte.resteeth.core

import eu.codearte.resteeth.annotation.RestClient
import eu.codearte.resteeth.core.sample.RestClientWithMethods
import eu.codearte.resteeth.core.sample.RestMethodsConfig
import eu.codearte.resteeth.core.sample.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
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

	@RestClient
	RestClientWithMethods restClient

	@Autowired
	RestTemplate restTemplate

	MockRestServiceServer mockServer

	void setup() {
		mockServer = MockRestServiceServer.createServer(restTemplate)
	}

	def "should invoke get method"() {
		given:
			mockServer.expect(requestTo("http://localhost/users/42")).andExpect(method(HttpMethod.GET))
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
			mockServer.expect(requestTo("http://localhost/users/42/staff/123")).andExpect(method(HttpMethod.GET))
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
			mockServer.expect(requestTo("http://localhost/users")).andExpect(method(HttpMethod.POST))
					.andExpect(MockRestRequestMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andRespond(withSuccess())

		when:
			restClient.postToUsers(new User(name: "test"))

		then:
			mockServer.verify()
	}

	def "should invoke post method with path parameter"() {
		given:
			mockServer.expect(requestTo("http://localhost/users/135/staff")).andExpect(method(HttpMethod.POST))
					.andExpect(MockRestRequestMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andRespond(withCreatedEntity(new URI("http://localhost/users/42")))

		when:
			restClient.postToUsersStaff(135, new User(name: "test"))

		then:
			mockServer.verify()
	}

	def "should invoke put method"() {
		given:
			mockServer.expect(requestTo("http://localhost/users/44")).andExpect(method(HttpMethod.PUT))
					.andExpect(MockRestRequestMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andRespond(withSuccess())

		when:
			restClient.putToUsers(44, new User(name: "test"))

		then:
			mockServer.verify()
	}

	def "should invoke delete method"() {
		given:
			mockServer.expect(requestTo("http://localhost/users/42")).andExpect(method(HttpMethod.DELETE))
					.andRespond(withSuccess())

		when:
			restClient.deleteUser(42)

		then:
			mockServer.verify()
	}

	def "should not throw when calling toString()"() {
		when:
			def str = restClient.toString()

		then:
			str.startsWith("Proxy to ")
	}

	def "should get ResponseEntity"() {
		given:
			mockServer.expect(requestTo("http://localhost/users/42")).andExpect(method(HttpMethod.GET))
					.andRespond(withSuccess("{ \"id\" : \"42\", \"name\" : \"John\"}", MediaType.APPLICATION_JSON))

		when:
			def entity = restClient.getResponseEntity(42)

		then:
			mockServer.verify()
			entity.statusCode == HttpStatus.OK
			def user = entity.getBody()
			user.id == 42
			user.name == "John"
	}

	def "should invoke get method with request parameter"() {
		given:
			mockServer.expect(requestTo("http://localhost/users/queries?name=John")).andExpect(method(HttpMethod.GET))
					.andRespond(withSuccess("{ \"id\" : \"42\", \"name\" : \"John\"}", MediaType.APPLICATION_JSON))

		when:
			User user = restClient.getWithRequestParameter("John")

		then:
			mockServer.verify()
			user.id == 42
			user.name == "John"
	}

	def "should invoke get method with request parameters pojo"() {
		given:
			mockServer.expect(requestTo("http://localhost/users/queriesPojo?name=John")).andExpect(method(HttpMethod.GET))
					.andRespond(withSuccess("{ \"id\" : \"42\", \"name\" : \"John\"}", MediaType.APPLICATION_JSON))

		when:
			User user = restClient.getWithRequestParametersPojo(new User(name: "John"))

		then:
			mockServer.verify()
			user.id == 42
			user.name == "John"
	}
}
