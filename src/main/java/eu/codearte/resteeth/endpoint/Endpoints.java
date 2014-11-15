package eu.codearte.resteeth.endpoint;

import java.net.MalformedURLException;
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
	public static EndpointProvider fixedEndpoint(String endpointUrl) {
		return new FixedEndpoint(toUrl(endpointUrl));
	}

	public static EndpointProvider roundRobinEndpoint(URL... endpointUrls) {
		return new RoundRobinEndpoint(endpointUrls);
	}

	public static EndpointProvider roundRobinEndpoint(String... endpointUrls) {
		return new RoundRobinEndpoint(toUrls(endpointUrls));
	}

	private static URL[] toUrls(String[] endpoints) {
		URL[] urls = new URL[endpoints.length];
		for (int i = 0; i < endpoints.length; i++) {
			urls[i] = toUrl(endpoints[i]);
		}
		return urls;
	}

	private static URL toUrl(String endpoint) {
		try {
			return new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(endpoint + " is not valid URL", e);
		}
	}

}
