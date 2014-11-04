package eu.codearte.restofag;

import eu.codearte.restofag.endpoint.EndpointProvider;
import eu.codearte.restofag.endpoint.FixedEndpoint;
import eu.codearte.restofag.endpoint.RoundRobinEndpoint;

/**
 * @author Jakub Kubrynski
 */
public class Restofag {

	public static EndpointProvider fixedEndpoint(String endpointUrl) {
		return new FixedEndpoint(endpointUrl);
	}

	public static EndpointProvider roundRobinEndpoint(String... endpointUrls) {
		return new RoundRobinEndpoint(endpointUrls);
	}

}
