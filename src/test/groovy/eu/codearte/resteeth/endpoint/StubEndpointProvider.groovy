package eu.codearte.resteeth.endpoint

/**
 * @author Jakub Kubrynski
 */
class StubEndpointProvider implements EndpointProvider {

	@Override
	URL getEndpoint() {
		new URL("http://localhost")
	}
}
