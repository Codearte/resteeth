package eu.codearte.resteeth.endpoint;

import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jakub Kubrynski
 */
class RoundRobinEndpoint implements EndpointProvider {

	private final URL[] endpointUrls;
	private final AtomicInteger counter = new AtomicInteger();

	RoundRobinEndpoint(URL[] endpointUrls) {
		this.endpointUrls = endpointUrls;
	}

	@Override
	public URL getEndpoint() {
		return endpointUrls[Math.abs(counter.getAndIncrement()) % endpointUrls.length];
	}
}
