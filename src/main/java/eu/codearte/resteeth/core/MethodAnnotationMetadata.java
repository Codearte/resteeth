package eu.codearte.resteeth.core;

/**
 * @author Jakub Kubrynski
 */
public class MethodAnnotationMetadata {

	private final ResteethAnnotationMetadata resteethAnnotationMetadata;

	MethodAnnotationMetadata(ResteethAnnotationMetadata resteethAnnotationMetadata) {
		this.resteethAnnotationMetadata = resteethAnnotationMetadata;
	}

	public ResteethAnnotationMetadata getResteethAnnotationMetadata() {
		return resteethAnnotationMetadata;
	}
}
