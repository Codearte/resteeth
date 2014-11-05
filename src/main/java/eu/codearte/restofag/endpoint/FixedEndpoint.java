package eu.codearte.restofag.endpoint;

/**
 * @author Jakub Kubrynski
 */
class FixedEndpoint implements EndpointProvider {

	private final String endpointUrl;

	FixedEndpoint(String endpointUrl) {
		this.endpointUrl = endpointUrl;
	}

	@Override
	public String getEndpoint() {
		return endpointUrl;
	}
}
