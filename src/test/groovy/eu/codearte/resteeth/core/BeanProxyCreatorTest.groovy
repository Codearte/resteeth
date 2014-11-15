package eu.codearte.resteeth.core

import eu.codearte.resteeth.annotation.RestClient
import eu.codearte.resteeth.config.EnableResteeth
import eu.codearte.resteeth.core.sample.RestClientWithMethods
import eu.codearte.resteeth.core.sample.User
import eu.codearte.resteeth.handlers.LoggingHandler
import eu.codearte.resteeth.handlers.ProfilingHandler
import eu.codearte.resteeth.handlers.RestInvocationHandler
import eu.codearte.resteeth.handlers.UserAgentHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * @author Tomasz Nurkiewicz
 */
@ContextConfiguration(classes = CustomHandlersConfiguration)
class BeanProxyCreatorTest extends Specification {

	public static final int CACHED_ID = 1
	public static final String FIXED_RESPONSE = "Fixed"
	public static final String CACHED_RESPONSE = "Cached"

	@RestClient(endpoints = "http://localhost")
	private RestClientWithMethods client

	def 'should stop at custom handler and do not invoke remaining handlers'() {
		given:
			def idThatIsCached = CACHED_ID

		when:
			User user = client.getWithSingleParameter(idThatIsCached)

		then:
			user.id == CACHED_ID
			user.name == CACHED_RESPONSE
	}

	def 'should proceed after first handler and call second in order'() {
		given:
			def idThatIsNotCached = 2

		when:
			User user = client.getWithSingleParameter(idThatIsNotCached)

		then:
			user.id == 2
			user.name == FIXED_RESPONSE
	}

}

@Configuration
@EnableResteeth
class CustomHandlersConfiguration {

	@Bean
	RestInvocationHandler cachingHandler() {
		return new RestInvocationHandler() {
			@Override
			Object proceed(RestInvocation invocation) {
				def id = invocation.arguments[0]
				if (id == BeanProxyCreatorTest.CACHED_ID) {
					return new User(id: id, name: BeanProxyCreatorTest.CACHED_RESPONSE)
				} else {
					return invocation.proceed()
				}
			}

			@Override
			int getOrder() {
				return LOWEST_PRECEDENCE - 10
			}
		}
	}

	@Bean
	RestInvocationHandler fixedHandler() {
		return new RestInvocationHandler() {

			@Override
			Object proceed(RestInvocation invocation) {
				return new User(id: invocation.arguments[0], name: BeanProxyCreatorTest.FIXED_RESPONSE)
			}

			@Override
			int getOrder() {
				return LOWEST_PRECEDENCE - 5
			}
		}
	}

	@Bean
	RestInvocationHandler loggingHandler() {
		return new LoggingHandler()
	}

	@Bean
	RestInvocationHandler profilingHandler() {
		return new ProfilingHandler()
	}

	@Bean
	RestInvocationHandler userAgentHandler() {
		return new UserAgentHandler()
	}

}
