package eu.codearte.restofag.endpoint

import spock.lang.Specification

/**
 * @author Jakub Kubrynski
 */
class FixedEndpointTest extends Specification {

	private static final String ENDPOINT_URL = "http://localhost"

	def "should return fixed url"() {
		given:
			EndpointProvider sut = new FixedEndpoint(ENDPOINT_URL)
		when:
			def endpoint1 = sut.getEndpoint()
			def endpoint2 = sut.getEndpoint()
		then:
			endpoint1 == ENDPOINT_URL
			endpoint2 == ENDPOINT_URL
	}
}
