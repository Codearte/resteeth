package eu.codearte.restofag.config.qualifier

import eu.codearte.restofag.annotation.RestClient
import org.springframework.beans.factory.annotation.Qualifier

/**
 * @author Jakub Kubrynski
 */
@RestClient
@Qualifier("test")
interface RestInterfaceWithQualifier {
}
