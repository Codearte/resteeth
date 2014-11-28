package eu.codearte.resteeth.core.sample

import eu.codearte.resteeth.annotation.StaticHeader
import eu.codearte.resteeth.annotation.StaticHeaders
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * @author Jakub Kubrynski
 */
interface RestClientHeaders {

	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	User getUserDynamicHeader(@RequestHeader("testHeaderName") String headerValue, @PathVariable("id") Integer id);

	@StaticHeader(name = "testHeaderName", value = "testHeaderValue")
	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	User getUserStaticHeader(@PathVariable("id") Integer id);

	@StaticHeaders([
			@StaticHeader(name = "testHeaderName1", value = "testHeaderValue1"),
			@StaticHeader(name = "testHeaderName2", value = "testHeaderValue2")
	])
	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	User getUserStaticHeaders(@PathVariable("id") Integer id);
}
