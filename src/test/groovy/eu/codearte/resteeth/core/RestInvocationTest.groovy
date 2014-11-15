package eu.codearte.resteeth.core

import eu.codearte.resteeth.handlers.RestInvocationHandler
import spock.lang.Specification

/**
 * @author Tomasz Nurkiewicz
 */
class RestInvocationTest extends Specification {

	public static final int SOME_RESULT = 17

	def 'should call first and only handler in stack'() {
		given:
			def handlerMock = Mock(RestInvocationHandler)
			def invocation = new RestInvocation(null, null, Mock(MethodMetadata), [handlerMock])

		when:
			invocation.proceed()

		then:
			1 * handlerMock.proceed(_)
	}

	def 'should call all handlers in order'() {
		given:
			def firstHandler = [proceed: {
				return it.proceed()
			}] as RestInvocationHandler
			def secondHandler = Mock(RestInvocationHandler)
			def invocation = new RestInvocation(null, null, Mock(MethodMetadata), [firstHandler, secondHandler])

		when:
			def result = invocation.proceed()

		then:
			1 * secondHandler.proceed(_) >> SOME_RESULT
			result == SOME_RESULT
	}

	def 'should not call second handler if first handled request already'() {
		given:
			def firstHandler = [proceed: {
				return SOME_RESULT
			}] as RestInvocationHandler
			def secondHandler = Mock(RestInvocationHandler)
			def invocation = new RestInvocation(null, null, Mock(MethodMetadata), [firstHandler, secondHandler])

		when:
			def result = invocation.proceed()

		then:
			0 * secondHandler.proceed(_)
			result == SOME_RESULT
	}
}

