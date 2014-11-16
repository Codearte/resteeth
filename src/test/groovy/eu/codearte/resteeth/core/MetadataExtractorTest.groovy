package eu.codearte.resteeth.core

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Specification

/**
 * @author Jakub Kubrynski
 */
class MetadataExtractorTest extends Specification {

	private MetadataExtractor extractor

	static interface SampleRestClient {

		@RequestMapping(value = "/somethings/{id}")
		void withoutRequestMethod(@PathVariable("id") Long id);

		@RequestMapping(method = RequestMethod.GET)
		void withoutRequestUrl();

		@RequestMapping(value = "/somethings/{id}", method = RequestMethod.GET)
		void withoutContentTypes();

		@RequestMapping(value = "/somethings", method = RequestMethod.GET,
				consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_XML_VALUE])
		void withAllData();
	}

	@RequestMapping(value = "/somethings", method = RequestMethod.GET,
			consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_XML_VALUE])
	static interface SampleRestClientWithMapping {

		@RequestMapping(value = "/{id}")
		void withoutRequestMethod(@PathVariable("id") Long id);

		@RequestMapping(method = RequestMethod.GET)
		void withoutRequestUrl();

		@RequestMapping(method = RequestMethod.GET)
		void withoutContentTypes();
	}

	void setup() {
		extractor = new MetadataExtractor()
	}

	def "should extract request information from method mapping"() {
		given:
			def method = SampleRestClient.class.getDeclaredMethod("withAllData")
		when:
			def metadata = extractor.extractMethodMetadata(method, null, null)
		then:
			metadata.methodUrl == "/somethings"
			metadata.requestMethod == HttpMethod.GET
			metadata.httpHeaders.getContentType() == MediaType.APPLICATION_JSON
			metadata.httpHeaders.getAccept() == [MediaType.APPLICATION_XML]
	}

	def "should check if request method is required"() {
		given:
			def method = SampleRestClient.class.getDeclaredMethod("withoutRequestMethod", Long.class)
		when:
			extractor.extractMethodMetadata(method, null, null)
		then:
			def e = thrown(IncorrectRequestMapping.class)
			e.message.contains("No requestMethods specified")
	}

	def "should inherit request method from controller"() {
		given:
			def method = SampleRestClientWithMapping.class.getDeclaredMethod("withoutRequestMethod", Long.class)
			def controllerRequestMapping = SampleRestClientWithMapping.getAnnotation(RequestMapping)
		when:
			def metadata = extractor.extractMethodMetadata(method, controllerRequestMapping, null)
		then:
			metadata.requestMethod == HttpMethod.GET
	}

	def "should check if request url is required"() {
		given:
			def method = SampleRestClient.class.getDeclaredMethod("withoutRequestUrl")
		when:
			extractor.extractMethodMetadata(method, null, null)
		then:
			def e = thrown(IncorrectRequestMapping.class)
			e.message.contains("No request url found")
	}

	def "should inherit request url from controller"() {
		given:
			def method = SampleRestClientWithMapping.class.getDeclaredMethod("withoutRequestUrl")
			def controllerRequestMapping = SampleRestClientWithMapping.getAnnotation(RequestMapping)
		when:
			def metadata = extractor.extractMethodMetadata(method, controllerRequestMapping, null)
		then:
			metadata.methodUrl == "/somethings"
	}

	def "should merge request url from controller"() {
		given:
			def method = SampleRestClientWithMapping.class.getDeclaredMethod("withoutRequestMethod", Long.class)
			def controllerRequestMapping = SampleRestClientWithMapping.getAnnotation(RequestMapping)
		when:
			def metadata = extractor.extractMethodMetadata(method, controllerRequestMapping, null)
		then:
			metadata.methodUrl == "/somethings/{id}"
	}

	def "should work without content types"() {
		given:
			def method = SampleRestClient.class.getDeclaredMethod("withoutContentTypes")
		when:
			def metadata = extractor.extractMethodMetadata(method, null, null)
		then:
			!metadata.httpHeaders.containsKey("ContentType")
			!metadata.httpHeaders.containsKey("Accept")
	}

	def "should inherit content types from controller"() {
		given:
			def method = SampleRestClientWithMapping.class.getDeclaredMethod("withoutContentTypes")
			def controllerRequestMapping = SampleRestClientWithMapping.getAnnotation(RequestMapping)
		when:
			def metadata = extractor.extractMethodMetadata(method, controllerRequestMapping, null)
		then:
			metadata.httpHeaders.getContentType() == MediaType.APPLICATION_JSON
			metadata.httpHeaders.getAccept() == [MediaType.APPLICATION_XML]
	}
}
