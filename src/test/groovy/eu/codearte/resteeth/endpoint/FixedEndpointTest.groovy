package eu.codearte.resteeth.endpoint

import spock.lang.Specification

/**
 * @author Jakub Kubrynski
 */
class FixedEndpointTest extends Specification {

	private static final URL ENDPOINT_URL = "http://localhost".toURL()

	def "should return fixed url"() {
		given:
			EndpointProvider sut = Endpoints.fixedEndpoint(ENDPOINT_URL)
		when:
			def endpoint1 = sut.getEndpoint()
			def endpoint2 = sut.getEndpoint()
		then:
			endpoint1 == ENDPOINT_URL
			endpoint2 == ENDPOINT_URL
	}
}
