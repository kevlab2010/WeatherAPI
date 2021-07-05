package com.kev.weatherapi.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;

import com.kev.weatherapi.exception.InValidQueryParameterException;

@ExtendWith(MockitoExtension.class)
public class ValidationServiceTest {
	
	@Mock
	Environment env;
	
	@InjectMocks
	ValidationServiceImpl validation;	
	
	@ParameterizedTest
	@ValueSource(strings = {"8525a","8525","852590","_1234","12.45"})
	public void validateQueryParam_testThrowException(String value) {
		when(env.getProperty("error.validate.queryparam.zipcode")).thenReturn("Zipcode must be 5 digits");		
		Exception ex = assertThrows(InValidQueryParameterException.class, () -> validation.validateQueryParam("zip", value));		
		String expectedMsg = "Zipcode must be 5 digits";
		String actualMsg = ex.getMessage();	
		assertTrue(actualMsg.equalsIgnoreCase(expectedMsg));	
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"85251","06450","99999","00000"})
	public void validateQueryParam_Pass(String value) {
		assertDoesNotThrow( () -> validation.validateQueryParam("zip", value));		
	}
	
}
