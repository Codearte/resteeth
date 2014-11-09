package eu.codearte.resteeth.config.qualifier

import eu.codearte.resteeth.annotation.RestClient
import org.springframework.beans.factory.annotation.Qualifier

/**
 * @author Jakub Kubrynski
 */
@RestClient
@Qualifier("test")
interface RestInterfaceWithQualifier {
}
