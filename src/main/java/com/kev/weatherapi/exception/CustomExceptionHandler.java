package com.kev.weatherapi.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@PropertySource("classpath:messages.properties")
@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Autowired
	private Environment env;

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleAllException(Exception ex, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), "500", ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	} 
	
	@ExceptionHandler(InValidQueryParameterException.class)
	public ResponseEntity<Object> handleAllException(InValidQueryParameterException ex, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), "400", ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	} 
	
	@ExceptionHandler(ZipCodeNotFoundException.class)
	public ResponseEntity<Object> handleZipCodeNotFoundException(ZipCodeNotFoundException ex, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), "404", ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND);
	} 
	
	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<Object> handleHttpClientErrorException(HttpClientErrorException ex, WebRequest request) {
		log.error(ex.toString());
		if(ex.getStatusCode() == HttpStatus.NOT_FOUND) {
			ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), env.getProperty("error.zipcode.notfound.code"), env.getProperty("error.zipcode.notfound.message") , request.getDescription(false));
			return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND);
			
		}
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), "400", ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	} 
	
	@ExceptionHandler(HttpServerErrorException .class)
	public ResponseEntity<Object> handleHttpClientErrorException(HttpServerErrorException  ex, WebRequest request) {
		log.error(ex.toString());
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), env.getProperty("error.service.notavailable.code"), env.getProperty("error.service.notavailable.message"),
				request.getDescription(false));
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	} 

}
