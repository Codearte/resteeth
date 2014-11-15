package eu.codearte.resteeth.endpoint;

import java.net.URL;

/**
 * @author Jakub Kubrynski
 */
public class Endpoints {

	private Endpoints() {
	}

	public static EndpointProvider fixedEndpoint(URL endpointUrl) {
		return new FixedEndpoint(endpointUrl);
	}

	public static EndpointProvider roundRobinEndpoint(URL... endpointUrls) {
		return new RoundRobinEndpoint(endpointUrls);
	}

}
