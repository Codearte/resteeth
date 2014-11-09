package eu.codearte.resteeth.config;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

/**
 * @author Jakub Kubrynski
 */
class RestClientComponentProvider extends ClassPathScanningCandidateComponentProvider {

	RestClientComponentProvider(boolean useDefaultFilters) {
		super(useDefaultFilters);
	}

	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		// only interfaces are allowed
		return beanDefinition.getMetadata().isInterface();
	}
}
