package eu.codearte.restofag.config;

import eu.codearte.restofag.annotation.RestClient;
import eu.codearte.restofag.core.RestClientBean;
import eu.codearte.restofag.core.RestClientMethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.Ordered;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * @author Jakub Kubrynski
 */
public class RestofagBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered, BeanClassLoaderAware {

	private final ClassPathScanningCandidateComponentProvider candidateComponentProvider;
	private final String[] basePackages;
	private ClassLoader classLoader;

	public RestofagBeanFactoryPostProcessor(String[] basePackagesParam) {
		Assert.notEmpty(basePackagesParam);
		basePackages = basePackagesParam;
		candidateComponentProvider = new RestClientComponentProvider(false);
		candidateComponentProvider.addIncludeFilter(new AnnotationTypeFilter(RestClient.class));
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
		for (String basePackages : basePackages) {
			Set<BeanDefinition> eu = candidateComponentProvider.findCandidateComponents(basePackages);
			for (BeanDefinition beanDefinition : eu) {
				Class<?> beanClass = getBeanClass(beanDefinition);
				if (beanNotDefinedExplicitly(configurableListableBeanFactory, beanClass)) {
					Object bean = createBean(beanClass);
					configurableListableBeanFactory.registerSingleton(beanDefinition.getBeanClassName(), bean);
				}
			}
		}

	}

	private boolean beanNotDefinedExplicitly(ConfigurableListableBeanFactory configurableListableBeanFactory, Class<?> beanClass) {
		String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(configurableListableBeanFactory, beanClass, true, true);
		return beanNames == null || beanNames.length == 0;
	}

	private Object createBean(Class<?> beanClass) {
		ProxyFactory result = new ProxyFactory();

		result.addInterface(beanClass);
		result.setTarget(new RestClientBean());
		result.addAdvice(new RestClientMethodInterceptor());

		return result.getProxy();
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
