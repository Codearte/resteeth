package eu.codearte.resteeth.config;

import eu.codearte.resteeth.annotation.RestClient;
import eu.codearte.resteeth.endpoint.EndpointProvider;
import eu.codearte.resteeth.endpoint.Endpoints;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.MethodMetadata;

import java.lang.annotation.Annotation;

/**
 * @author Jakub Kubrynski
 */
class BeanResolver {

	boolean beanNotDefinedExplicitly(ConfigurableListableBeanFactory configurableListableBeanFactory, Class<?> beanClass) {
		String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(configurableListableBeanFactory, beanClass, true, true);
		return beanNames == null || beanNames.length == 0;
	}

	EndpointProvider findEndpointProvider(Class<?> beanClass, ConfigurableListableBeanFactory beanFactory, RestClient restClient) {
		if (restClient.endpoints().length == 1) {
			return Endpoints.fixedEndpoint(restClient.endpoints()[0]);
		} else if (restClient.endpoints().length > 1) {
			return Endpoints.roundRobinEndpoint(restClient.endpoints());
		}

		Qualifier qualifier = AnnotationUtils.findAnnotation(beanClass, Qualifier.class);

		if (qualifier == null) {
			// without qualifier
			return BeanFactoryUtils.beanOfTypeIncludingAncestors(beanFactory, EndpointProvider.class);
		}

		Annotation qualifierAnnotation = qualifier;

		for (Annotation annotation : beanClass.getAnnotations()) {
			if (qualifier != annotation && annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
				qualifierAnnotation = annotation;
			}
		}

		String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, EndpointProvider.class, true, true);

		for (String beanName : beanNames) {

			if (checkQualifier(beanFactory.getBeanDefinition(beanName), qualifierAnnotation)) {
				return (EndpointProvider) beanFactory.getBean(beanName);
			}
		}

		throw new NoSuchBeanDefinitionException(EndpointProvider.class, "Cannot find proper for " + beanClass.getCanonicalName());
	}

	private boolean checkQualifier(BeanDefinition endpointBeanDefinition, Annotation qualifierAnnotation) {
		if (endpointBeanDefinition instanceof AnnotatedBeanDefinition) {
			AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) endpointBeanDefinition;
			String qualifierCanonicalName = qualifierAnnotation.annotationType().getCanonicalName();

			MethodMetadata factoryMethodMetadata = annotatedBeanDefinition.getFactoryMethodMetadata();

			if (factoryMethodMetadata.isAnnotated(qualifierCanonicalName)) {
				if (qualifierAnnotation instanceof Qualifier) {
					Object value1 = factoryMethodMetadata.getAnnotationAttributes(qualifierCanonicalName).get("value");
					Object value2 = ((Qualifier) qualifierAnnotation).value();
					if (value1 == null || value2 == null) {
						throw new IllegalArgumentException("No value found on Qualifier annotation");
					}
					if (value1.equals(value2)) {
						return true;
					}
				}
				return true;
			}
		}
		return false;
	}

}
