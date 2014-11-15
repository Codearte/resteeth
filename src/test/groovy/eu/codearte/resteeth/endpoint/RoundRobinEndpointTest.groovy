package eu.codearte.resteeth.endpoint

import spock.lang.Specification

/**
 * @author Jakub Kubrynski
 */
class RoundRobinEndpointTest extends Specification {

	private static final URL ENDPOINT_1_URL = "http://localhost1".toURL()
	private static final URL ENDPOINT_2_URL = "http://localhost2".toURL()

	def "should return fixed url"() {
		given:
			EndpointProvider sut = Endpoints.roundRobinEndpoint(ENDPOINT_1_URL, ENDPOINT_2_URL)
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
