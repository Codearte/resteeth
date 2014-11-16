package eu.codearte.resteeth.util;

import eu.codearte.resteeth.handlers.RestInvocationHandler;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Jakub Kubrynski
 */
public final class SpringUtils {

	private SpringUtils() {
	}

	public static Collection<RestInvocationHandler> getBeansOfType(Class<RestInvocationHandler> restInvocationHandlerClass,
																   ConfigurableListableBeanFactory beanFactory) {
		LinkedList<RestInvocationHandler> restInvocationHandlers = new LinkedList<>();
		for (String beanName : beanFactory.getBeanDefinitionNames()) {
			RootBeanDefinition beanDefinition = (RootBeanDefinition) beanFactory.getMergedBeanDefinition(beanName);

			if (restInvocationHandlerClass.isAssignableFrom(beanDefinition.getTargetType())) {
				restInvocationHandlers.add((RestInvocationHandler) beanFactory.getBean(beanName));
			}
		}
		return restInvocationHandlers;
	}
}
