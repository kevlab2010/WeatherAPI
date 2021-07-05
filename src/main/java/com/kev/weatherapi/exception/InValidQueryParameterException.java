package com.kev.weatherapi.exception;

public class InValidQueryParameterException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public InValidQueryParameterException(String message) {
		super(message);
	}
	
}
