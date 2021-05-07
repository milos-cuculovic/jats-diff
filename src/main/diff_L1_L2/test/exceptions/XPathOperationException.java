package main.diff_L1_L2.test.exceptions;

public class XPathOperationException extends Exception {

	// private Exception e;

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	public XPathOperationException(String assertError) {
		super(assertError);
	}

	public XPathOperationException(Exception e) {
		super(e);
	}

}
