package eu.codearte.resteeth.core;

/**
 * @author Jakub Kubrynski
 */
class IncorrectRequestMapping extends RuntimeException {

	public IncorrectRequestMapping(String message) {
		super(message);
	}
}
