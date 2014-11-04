package eu.codearte.restofag.config;

import eu.codearte.restofag.annotation.RestClient;
import eu.codearte.restofag.core.RestClientBean;
import eu.codearte.restofag.core.RestClientMethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.Ordered;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * @author Jakub Kubrynski
 */
@Component
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
				Object bean = createBean(beanDefinition);
				configurableListableBeanFactory.registerSingleton(beanDefinition.getBeanClassName(), bean);
			}
		}

	}

	private Object createBean(BeanDefinition beanDefinition) {
		ProxyFactory result = new ProxyFactory();

		try {
			result.addInterface(classLoader.loadClass(beanDefinition.getBeanClassName()));
		} catch (ClassNotFoundException e) {
			throw new NoClassDefFoundError("No class found: " + e.getMessage());
		}
		result.setTarget(new RestClientBean());
		result.addAdvice(new RestClientMethodInterceptor());

		return result.getProxy();
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
