package eu.codearte.restofag.config;

import eu.codearte.restofag.annotation.RestClient;
import eu.codearte.restofag.core.BeanProxyCreator;
import eu.codearte.restofag.endpoint.EndpointProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author Jakub Kubrynski
 */
class RestofagBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered, BeanClassLoaderAware {

	private final ClassPathScanningCandidateComponentProvider candidateComponentProvider;
	private final String[] basePackages;
	private ClassLoader classLoader;

	RestofagBeanFactoryPostProcessor(String[] basePackagesParam) {
		Assert.notEmpty(basePackagesParam);
		basePackages = basePackagesParam;
		candidateComponentProvider = new RestClientComponentProvider(false);
		candidateComponentProvider.addIncludeFilter(new AnnotationTypeFilter(RestClient.class));
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		RestTemplate restTemplate = provideRestTemplate(beanFactory);
		BeanProxyCreator beanProxyCreator = new BeanProxyCreator(restTemplate);

		for (String basePackage : basePackages) {
			Set<BeanDefinition> eu = candidateComponentProvider.findCandidateComponents(basePackage);
			for (BeanDefinition beanDefinition : eu) {
				Class<?> beanClass = getBeanClass(beanDefinition);
				if (beanNotDefinedExplicitly(beanFactory, beanClass)) {
					EndpointProvider endpointProvider = findEndpointProvider(beanDefinition, beanFactory);
					beanFactory.registerSingleton(beanDefinition.getBeanClassName(),
							beanProxyCreator.createProxyBean(beanClass, endpointProvider));
				}
			}
		}
	}

	private EndpointProvider findEndpointProvider(BeanDefinition beanDefinition, ConfigurableListableBeanFactory beanFactory) {

		Qualifier qualifier = AnnotationUtils.findAnnotation(getBeanClass(beanDefinition), Qualifier.class);

		if (qualifier == null) {
			// without qualifier
			return BeanFactoryUtils.beanOfTypeIncludingAncestors(beanFactory, EndpointProvider.class);
		}

		Annotation qualifierAnnotation = null;

		for (Annotation annotation : getBeanClass(beanDefinition).getAnnotations()) {
			if (qualifier != annotation && annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
				qualifierAnnotation = annotation;
			}
		}

		String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, EndpointProvider.class, true, true);

		for (String beanName : beanNames) {
			AbstractBeanDefinition endpointBeanDefinition = (AbstractBeanDefinition) beanFactory.getBeanDefinition(beanName);

			if (checkQualifier(endpointBeanDefinition, qualifierAnnotation)) {
				return (EndpointProvider) beanFactory.getBean(beanName);
			}
		}

		throw new NoSuchBeanDefinitionException(EndpointProvider.class, "Cannot find proper for " + beanDefinition.getBeanClassName());
	}

	private boolean checkQualifier(AbstractBeanDefinition endpointBeanDefinition, Annotation qualifierAnnotation) {
		return false;
	}

	private RestTemplate provideRestTemplate(ConfigurableListableBeanFactory configurableListableBeanFactory) {
		if (beanNotDefinedExplicitly(configurableListableBeanFactory, RestTemplate.class)) {
			ArrayList<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
			messageConverters.add(new MappingJackson2HttpMessageConverter());
			configurableListableBeanFactory.registerSingleton("restofagRestTemplate", new RestTemplate(messageConverters));
		}
		return configurableListableBeanFactory.getBean(RestTemplate.class);
	}

	private boolean beanNotDefinedExplicitly(ConfigurableListableBeanFactory configurableListableBeanFactory, Class<?> beanClass) {
		String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(configurableListableBeanFactory, beanClass, true, true);
		return beanNames == null || beanNames.length == 0;
	}

	private Class<?> getBeanClass(BeanDefinition beanDefinition) {
		try {
			return classLoader.loadClass(beanDefinition.getBeanClassName());
		} catch (ClassNotFoundException e) {
			throw new NoClassDefFoundError("No class found: " + e.getMessage());
		}
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

}
