package edu.uah.itsc.xively.ingestor.exceptions;

public class FeedParserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public FeedParserException(String message){
		super("Feed Parser Exception: " + message);
	}

}
