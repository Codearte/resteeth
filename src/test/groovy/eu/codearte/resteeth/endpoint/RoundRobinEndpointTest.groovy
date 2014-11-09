package eu.codearte.resteeth.endpoint

import spock.lang.Specification

/**
 * @author Jakub Kubrynski
 */
class RoundRobinEndpointTest extends Specification {

	private static final String ENDPOINT_1_URL = "http://localhost1"
	private static final String ENDPOINT_2_URL = "http://localhost2"

	def "should return fixed url"() {
		given:
			EndpointProvider sut = new RoundRobinEndpoint(ENDPOINT_1_URL, ENDPOINT_2_URL)
		when:
			def endpoint1 = sut.getEndpoint()
			def endpoint2 = sut.getEndpoint()
			def endpoint3 = sut.getEndpoint()
		then:
			endpoint1 == ENDPOINT_1_URL
			endpoint2 == ENDPOINT_2_URL
			endpoint3 == ENDPOINT_1_URL
	}
}
