package eu.codearte.resteeth.endpoint;

/**
 * @author Jakub Kubrynski
 */
public class Endpoints {

	private Endpoints() {
	}

	public static EndpointProvider fixedEndpoint(String endpointUrl) {
		return new FixedEndpoint(endpointUrl);
	}

	public static EndpointProvider roundRobinEndpoint(String... endpointUrls) {
		return new RoundRobinEndpoint(endpointUrls);
	}

}
