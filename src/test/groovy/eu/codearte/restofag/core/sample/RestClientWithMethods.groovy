package eu.codearte.restofag.core.sample

import eu.codearte.restofag.annotation.RestClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * @author Jakub Kubrynski
 */
@RestClient
interface RestClientWithMethods {

	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	User getWithSingleParameter(@PathVariable("id") Integer id);

	@RequestMapping(value = "/users/{id}/staff/{orderId}", method = RequestMethod.GET)
	User getWithTwoParameters(@PathVariable("orderId") Integer orderId, @PathVariable("id") Integer id);

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	void postToUsers(@RequestBody User user);

	@RequestMapping(value = "/users/{id}/staff", method = RequestMethod.POST)
	void postToUsersStaff(@PathVariable("id") Long userId, @RequestBody User user);
}
