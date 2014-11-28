package eu.codearte.resteeth.config;

import eu.codearte.resteeth.annotation.RestClient;
import eu.codearte.resteeth.core.BeanProxyCreator;
import eu.codearte.resteeth.handlers.RestInvocationHandler;
import eu.codearte.resteeth.util.SpringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Jakub Kubrynski
 */
class ResteethAutowireCandidateResolverDelegate implements AutowireCandidateResolver, BeanFactoryAware {

	private static final String RESTEETH_REST_TEMPLATE_BEAN_NAME = "resteethRestTemplate";
	private BeanResolver beanResolver = new BeanResolver();
	private ConfigurableListableBeanFactory beanFactory;
	private BeanProxyCreator beanProxyCreator;

	private final AutowireCandidateResolver autowireCandidateResolver;
	private final AnnotationAttributes enableResteethAttributes;

	private boolean initialized = false;

	public ResteethAutowireCandidateResolverDelegate(AutowireCandidateResolver autowireCandidateResolver,
	                                                 AnnotationAttributes enableResteethAttributes) {
		this.autowireCandidateResolver = autowireCandidateResolver;
		this.enableResteethAttributes = enableResteethAttributes;
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
			ensueBeanProxyCreatorInitialized();
			return beanProxyCreator.createProxyBean(descriptor.getDependencyType(),
					beanResolver.findEndpointProvider(descriptor.getDependencyType(), beanFactory, restClientAnnotation),
					enableResteethAttributes, Arrays.asList(descriptor.getAnnotations()));
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

	private synchronized void ensueBeanProxyCreatorInitialized() {
		if (!initialized) {
			initialized = true;
			RestTemplate restTemplate = provideRestTemplate(this.beanFactory);
			final Collection<RestInvocationHandler> handlers = SpringUtils.getBeansOfType(RestInvocationHandler.class, this.beanFactory);
			beanProxyCreator = new BeanProxyCreator(restTemplate, handlers);
		}
	}

	private RestTemplate provideRestTemplate(ConfigurableListableBeanFactory configurableListableBeanFactory) {
		if (beanResolver.beanNotDefinedExplicitly(configurableListableBeanFactory, RestTemplate.class)) {
			ArrayList<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
			messageConverters.add(new StringHttpMessageConverter());
			messageConverters.add(new MappingJackson2HttpMessageConverter());
			configurableListableBeanFactory.registerSingleton(RESTEETH_REST_TEMPLATE_BEAN_NAME, new RestTemplate(messageConverters));
		}
		return configurableListableBeanFactory.getBean(RestTemplate.class);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}
}
