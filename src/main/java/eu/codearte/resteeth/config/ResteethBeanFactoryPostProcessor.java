package eu.codearte.resteeth.config;

import eu.codearte.resteeth.annotation.RestClient;
import eu.codearte.resteeth.core.BeanProxyCreator;
import eu.codearte.resteeth.endpoint.EndpointProvider;
import eu.codearte.resteeth.endpoint.Endpoints;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.MethodMetadata;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.RestTemplate;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author Jakub Kubrynski
 */
class ResteethBeanFactoryPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

	private static final String RESTEETH_REST_TEMPLATE_BEAN_NAME = "resteethRestTemplate";
	private ConfigurableListableBeanFactory beanFactory;
	private BeanProxyCreator beanProxyCreator;

	@Override
	public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean,
																									String beanName) throws BeansException {

		Class<?> beanClass = bean.getClass();

		// if @Configuration enhanced by CGLib use superclass
		if (ClassUtils.isCglibProxy(bean)) {
			beanClass = beanClass.getSuperclass();
		}

		ArrayList<InjectionMetadata.InjectedElement> injectedElements = buildInjectedElements(beanClass);
		InjectionMetadata injectionMetadata = new InjectionMetadata(beanClass, injectedElements);

		try {
			injectionMetadata.inject(bean, beanName, pvs);
		} catch (Throwable th) {
			throw new BeanCreationException(beanName, "Injection of rest clients failed", th);
		}
		return pvs;
	}

	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		for (Constructor<?> constructor : beanClass.getDeclaredConstructors()) {
			registerFromParameters(constructor.getParameterTypes(), constructor.getParameterAnnotations());
		}

		return null;
	}

	private ArrayList<InjectionMetadata.InjectedElement> buildInjectedElements(Class<?> beanClass) throws BeansException {
		ArrayList<InjectionMetadata.InjectedElement> elements = new ArrayList<>();

		for (Field field : beanClass.getDeclaredFields()) {
			RestClient annotation = field.getAnnotation(RestClient.class);
			if (annotation != null) {
				Class<?> fieldType = field.getType();
				if (beanNotDefinedExplicitly(beanFactory, fieldType)) {
					InjectionMetadata.InjectedElement injectedElement = new FieldInjection(field, annotation);
					elements.add(injectedElement);
				}
			}
		}

		for (Method method : beanClass.getDeclaredMethods()) {
			Annotation[][] parameterAnnotations = method.getParameterAnnotations();
			registerFromParameters(method.getParameterTypes(), parameterAnnotations);

		}
		return elements;
	}

	private void registerFromParameters(Class<?>[] parameterTypes, Annotation[][] parameterAnnotations) {
		for (int i = 0; i < parameterAnnotations.length; i++) {
			for (Annotation annotation : parameterAnnotations[i]) {
				if (annotation.annotationType() == RestClient.class) {
					Class<?> fieldType = parameterTypes[i];
					if (beanNotDefinedExplicitly(beanFactory, fieldType)) {
						EndpointProvider endpointProvider = findEndpointProvider(fieldType, beanFactory, (RestClient) annotation);
						Object proxyBean = beanProxyCreator.createProxyBean(fieldType, endpointProvider);
						// walkaround - we have to register bean in context due to lack of possibility to inject into method parameter
						beanFactory.registerSingleton(fieldType.getName(), proxyBean);
					}
				}
			}
		}
	}

	private class FieldInjection extends InjectionMetadata.InjectedElement {

		private final RestClient annotation;

		private FieldInjection(Field field, RestClient annotation) {
			super(field, null);
			this.annotation = annotation;
		}

		@Override
		protected Object getResourceToInject(Object target, String requestingBeanName) {
			EndpointProvider endpointProvider = findEndpointProvider(this.getResourceType(), beanFactory, annotation);
			return beanProxyCreator.createProxyBean(this.getResourceType(), endpointProvider);
		}
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

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		return true;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
}
