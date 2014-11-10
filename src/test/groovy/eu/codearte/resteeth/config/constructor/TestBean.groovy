package eu.codearte.resteeth.config.constructor

import eu.codearte.resteeth.annotation.RestClient
import eu.codearte.resteeth.sample.RestClientInterface
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Jakub Kubrynski
 */
@Component
class TestBean {

	final RestClientInterface restClientInterface

	@Autowired
	TestBean(@RestClient RestClientInterface restClientInterface) {
		this.restClientInterface = restClientInterface
	}

}
