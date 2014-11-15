package eu.codearte.resteeth.endpoint;

import java.net.URL;

/**
 * @author Jakub Kubrynski
 */
class FixedEndpoint implements EndpointProvider {

	private final URL endpointUrl;

	FixedEndpoint(URL endpointUrl) {
		this.endpointUrl = endpointUrl;
	}

	@Override
	public URL getEndpoint() {
		return endpointUrl;
	}

	@Override
	public String toString() {
		return endpointUrl;
	}
}
