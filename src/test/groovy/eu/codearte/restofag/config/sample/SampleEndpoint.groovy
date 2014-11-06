package eu.codearte.restofag.config.sample

import org.springframework.beans.factory.annotation.Qualifier

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author Jakub Kubrynski
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface SampleEndpoint {

}
