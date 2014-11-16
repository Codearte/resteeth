package eu.codearte.resteeth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.annotation.AnnotationAttributes;

import java.lang.invoke.MethodHandles;

/**
 * @author Jakub Kubrynski
 */
class ResteethBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private final AnnotationAttributes enableResteethAttributes;

	public ResteethBeanFactoryPostProcessor(AnnotationAttributes enableResteethAttributes) {
		this.enableResteethAttributes = enableResteethAttributes;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		LOG.info("Resteeth is being registered in Spring BeanFactory...");
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
		ResteethAutowireCandidateResolverDelegate resteethAutowireCandidateResolverDelegate = new ResteethAutowireCandidateResolverDelegate(
				defaultListableBeanFactory.getAutowireCandidateResolver(), enableResteethAttributes);
		defaultListableBeanFactory.setAutowireCandidateResolver(resteethAutowireCandidateResolverDelegate);
	}
}
