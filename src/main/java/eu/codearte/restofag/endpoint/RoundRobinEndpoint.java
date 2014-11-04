package eu.codearte.restofag.endpoint;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jakub Kubrynski
 */
public class RoundRobinEndpoint implements EndpointProvider {

	private final String[] endpointUrls;
	private final AtomicInteger counter = new AtomicInteger();

	public RoundRobinEndpoint(String[] endpointUrls) {
		this.endpointUrls = endpointUrls;
	}

	@Override
	public String getEndpoint() {
		return endpointUrls[counter.getAndIncrement() % endpointUrls.length];
	}
}
