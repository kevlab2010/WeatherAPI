package com.kev.weatherapi.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kev.weatherapi.WeatherApiApplication;
import com.kev.weatherapi.exception.ServiceNotAvailableException;
import com.kev.weatherapi.exception.ZipCodeNotFoundException;
import com.kev.weatherapi.model.WeatherForecastDto;
import com.kev.weatherapi.service.WeatherForecastService;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@ExtendWith(MockitoExtension.class)
public class GeoClientTest {

	private static MockWebServer mockWebServer;

	  @Mock 
	  private Environment env;	 
	
	@InjectMocks
	private static GeoClient geoClient;

	@BeforeAll
	static void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start(9090);
		geoClient = new GeoClient(WebClient.builder(), mockWebServer.url("/geo/1.0/zip").toString());
	}

	@AfterAll
	static void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	  @Test 
	  public void populateCoordinatesTest() throws JsonProcessingException { 
		  WeatherForecastDto dto = new WeatherForecastDto();
		  dto.setZip("85259");
		  
		  WeatherForecastDto mockResponseDto = new WeatherForecastDto();
		  mockResponseDto.setZip("85259");
		  mockResponseDto.setName("Scottsdale");
		  mockResponseDto.setLatitude(33.5879);
		  mockResponseDto.setLongitude(-111.8404);
		  mockResponseDto.setCountry("US");
	
		  mockWebServer.enqueue(new MockResponse()
				  	.setResponseCode(200)
				  	.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
					.setBody(new ObjectMapper().writeValueAsString(mockResponseDto))					
				);
		  WeatherForecastDto actual = geoClient.populateCoordinates(dto);		  
		  double expectedLat = 33.5879;
		  double expectedLon = -111.8404;
		  assertEquals(expectedLat, actual.getLatitude().doubleValue(), 0.00001);
		  assertEquals(expectedLon, actual.getLongitude().doubleValue(), 0.00001);
		    
	  }
	  
	  @Test
	  public void populateCoordinates_NotFoundException() throws JsonProcessingException {
		  String errMsg = "Zipcode not found, please try another zipcode";
		  when(env.getProperty("error.zipcode.notfound.message")).thenReturn(errMsg);
		  when(env.getProperty("org.openweathermap.api.appid")).thenReturn("");
		  WeatherForecastDto dto = new WeatherForecastDto();
		  dto.setZip("85259");
		  
		  mockWebServer.enqueue(new MockResponse()
				  	.setResponseCode(404)
				  	.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)									
				);		  
		  Exception ex = assertThrows(ZipCodeNotFoundException.class, () ->  geoClient.populateCoordinates(dto));		  
		  assertTrue(ex.getMessage().equals(errMsg));
	  }
	  
	  @Test
	  public void populateCoordinates_ServiceNotAvailableException() throws JsonProcessingException {
		  String errMsg = "Service is currently not available. PLease try another time";
		  when(env.getProperty("error.service.notavailable.message")).thenReturn(errMsg);
		  when(env.getProperty("org.openweathermap.api.appid")).thenReturn("");
		  WeatherForecastDto dto = new WeatherForecastDto();
		  dto.setZip("85259");
		  
		  mockWebServer.enqueue(new MockResponse()
				  	.setResponseCode(500)
				  	.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)									
				);		  
		  Exception ex = assertThrows(ServiceNotAvailableException.class, () ->  geoClient.populateCoordinates(dto));
		  assertTrue(ex.getMessage().equals(errMsg));
	  }

}
