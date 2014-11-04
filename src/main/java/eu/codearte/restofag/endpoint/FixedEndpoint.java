package eu.codearte.restofag.endpoint;

/**
 * @author Jakub Kubrynski
 */
public class FixedEndpoint implements EndpointProvider {

	private final String endpointUrl;

	public FixedEndpoint(String endpointUrl) {
		this.endpointUrl = endpointUrl;
	}

	@Override
	public String getEndpoint() {
		return endpointUrl;
	}
}
