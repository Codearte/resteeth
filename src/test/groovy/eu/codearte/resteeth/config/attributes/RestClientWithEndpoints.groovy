package eu.codearte.resteeth.config.attributes

import eu.codearte.resteeth.annotation.RestClient

/**
 * @author Jakub Kubrynski
 */
@RestClient(endpoints = ["http://test", "http://test2"])
interface RestClientWithEndpoints {
}
