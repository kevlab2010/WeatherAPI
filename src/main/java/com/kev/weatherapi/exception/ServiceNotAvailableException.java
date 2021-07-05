package com.kev.weatherapi.exception;

public class ServiceNotAvailableException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ServiceNotAvailableException(String message) {
		super(message);
	}
	
}
