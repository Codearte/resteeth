package eu.codearte.resteeth.config;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Jakub Kubrynski
 */
class ResteethDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

	private static final String BEAN_FACTORY_POST_PROCESSOR_NAME = "resteethBeanFactoryPostProcessor";

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		registry.registerBeanDefinition(BEAN_FACTORY_POST_PROCESSOR_NAME, new RootBeanDefinition(ResteethBeanFactoryPostProcessor.class,
				null, new MutablePropertyValues()));
	}
}
