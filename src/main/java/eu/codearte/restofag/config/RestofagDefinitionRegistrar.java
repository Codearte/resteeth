package eu.codearte.restofag.config;

import eu.codearte.restofag.annotation.EnableRestofag;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Jakub Kubrynski
 */
public class RestofagDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

	private static final String ANNOTATION_NAME = EnableRestofag.class.getCanonicalName();
	private static final String BASE_PACKAGES_ATTRIBUTE = "basePackages";
	private static final String BEAN_FACTORY_POST_PROCESSOR_NAME = "restofagBeanFactoryPostProcessor";

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes annotationAttributes = new AnnotationAttributes(importingClassMetadata.getAnnotationAttributes(ANNOTATION_NAME));

		String[] basePackages = annotationAttributes.getStringArray(BASE_PACKAGES_ATTRIBUTE);

		ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
		constructorArgumentValues.addGenericArgumentValue(basePackages);
		registry.registerBeanDefinition(BEAN_FACTORY_POST_PROCESSOR_NAME, new RootBeanDefinition(RestofagBeanFactoryPostProcessor.class,
				constructorArgumentValues, new MutablePropertyValues()));
	}
}
