package eu.codearte.resteeth.config;

import eu.codearte.resteeth.handlers.HeadersHandler;
import eu.codearte.resteeth.handlers.LoggingHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jakub Kubrynski
 */
@Configuration
public class ResteethConfiguration {

	@Bean
	public HeadersHandler headersHandler() {
		return new HeadersHandler();
	}

	@Bean
	public LoggingHandler loggingHandler() {
		return new LoggingHandler();
	}
}
