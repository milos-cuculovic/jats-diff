package main.diff_L1_L2.test.exceptions;

public class XMLnotEqualException extends Exception {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	public XMLnotEqualException(String assertError) {
		super(assertError);
	}

}
