package eu.codearte.resteeth.config.boot

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * @author Mariusz Smykula
 */
@RestController
class EchoServer {

	@RequestMapping(value = "/echo/{message}", method = RequestMethod.GET)
	String echo(@PathVariable("message") String message) {
		message
	}

}
