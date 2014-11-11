package eu.codearte.resteeth.config;

import eu.codearte.resteeth.annotation.RestClient;
import eu.codearte.resteeth.core.BeanProxyCreator;
import eu.codearte.resteeth.endpoint.EndpointProvider;
import eu.codearte.resteeth.endpoint.Endpoints;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.MethodMetadata;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.Annotation;
import java.util.ArrayList;

/**
 * @author Jakub Kubrynski
 */
class AutowireCandidateResolverDelegate implements AutowireCandidateResolver, BeanFactoryAware {

	private static final String RESTEETH_REST_TEMPLATE_BEAN_NAME = "resteethRestTemplate";
	private ConfigurableListableBeanFactory beanFactory;
	private BeanProxyCreator beanProxyCreator;

	private final AutowireCandidateResolver autowireCandidateResolver;

	public AutowireCandidateResolverDelegate(AutowireCandidateResolver autowireCandidateResolver) {
		this.autowireCandidateResolver = autowireCandidateResolver;
	}

	@Override
	public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
		return autowireCandidateResolver.isAutowireCandidate(bdHolder, descriptor);
	}

	@Override
	public Object getSuggestedValue(DependencyDescriptor descriptor) {
		return autowireCandidateResolver.getSuggestedValue(descriptor);
	}

	@Override
	public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, String beanName) {
		RestClient restClientAnnotation = getRestClientAnnotation(descriptor.getAnnotations());
		if (restClientAnnotation != null) {
			EndpointProvider endpointProvider = findEndpointProvider(descriptor.getDependencyType(), beanFactory, restClientAnnotation);
			return beanProxyCreator.createProxyBean(descriptor.getDependencyType(), endpointProvider);
		}
		return autowireCandidateResolver.getLazyResolutionProxyIfNecessary(descriptor, beanName);
	}

	private RestClient getRestClientAnnotation(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (RestClient.class.equals(annotation.annotationType())) {
				return (RestClient) annotation;
			}
		}
		return null;
	}

	private boolean beanNotDefinedExplicitly(ConfigurableListableBeanFactory configurableListableBeanFactory, Class<?> beanClass) {
		String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(configurableListableBeanFactory, beanClass, true, true);
		return beanNames == null || beanNames.length == 0;
	}

	private EndpointProvider findEndpointProvider(Class<?> beanClass, ConfigurableListableBeanFactory beanFactory,
																								RestClient restClient) {
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

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
		RestTemplate restTemplate = provideRestTemplate(this.beanFactory);
		beanProxyCreator = new BeanProxyCreator(restTemplate);
	}

	private RestTemplate provideRestTemplate(ConfigurableListableBeanFactory configurableListableBeanFactory) {
		if (beanNotDefinedExplicitly(configurableListableBeanFactory, RestTemplate.class)) {
			ArrayList<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
			messageConverters.add(new MappingJackson2HttpMessageConverter());
			configurableListableBeanFactory.registerSingleton(RESTEETH_REST_TEMPLATE_BEAN_NAME, new RestTemplate(messageConverters));
		}
		return configurableListableBeanFactory.getBean(RestTemplate.class);
	}
}
