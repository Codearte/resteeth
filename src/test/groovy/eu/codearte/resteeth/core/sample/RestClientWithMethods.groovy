package eu.codearte.resteeth.core.sample

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * @author Jakub Kubrynski
 */
interface RestClientWithMethods {

	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	User getWithSingleParameter(@PathVariable("id") Integer id);

	@RequestMapping(value = "/users/{id}/staff/{orderId}", method = RequestMethod.GET)
	User getWithTwoParameters(@PathVariable("orderId") Integer orderId, @PathVariable("id") Integer id);

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	void postToUsers(@RequestBody User user);

	@RequestMapping(value = "/users/{id}/staff", method = RequestMethod.POST)
	void postToUsersStaff(@PathVariable("id") Long userId, @RequestBody User user);

	@RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
	void putToUsers(@PathVariable("id") Long userId, @RequestBody User user);

	@RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
	void deleteUser(@PathVariable("id") Integer id);

	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	ResponseEntity<User> getResponseEntity(@PathVariable("id") Integer id);

}
