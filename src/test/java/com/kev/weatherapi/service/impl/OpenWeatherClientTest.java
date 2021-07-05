package com.kev.weatherapi.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kev.weatherapi.exception.ServiceNotAvailableException;
import com.kev.weatherapi.exception.ZipCodeNotFoundException;
import com.kev.weatherapi.model.Record;
import com.kev.weatherapi.model.WeatherForecastDto;
import com.kev.weatherapi.model.response.OpenWeatherMapAPIResponse;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@ExtendWith(MockitoExtension.class)
public class OpenWeatherClientTest {

	private static MockWebServer mockWebServer;

	  @Mock 
	  private Environment env;
	 
	@InjectMocks
	private static OpenWeatherClient openWeatherClient;


	@BeforeAll
	static void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start(9090);
		openWeatherClient = new OpenWeatherClient(WebClient.builder(), mockWebServer.url("/data/2.5/onecall").toString());
	}

	@AfterAll
	static void tearDown() throws IOException {
		mockWebServer.shutdown();
	}
	
	@Test
	public void populateWeatherForecast() throws JsonProcessingException {	
		  WeatherForecastDto dto = new WeatherForecastDto();
		  dto.setZip("85259");
		  dto.setName("Scottsdale");
		  dto.setLatitude(33.5879);
		  dto.setLongitude(-111.8404);
		  dto.setCountry("US");
		   
		  OpenWeatherMapAPIResponse mockResponse = new OpenWeatherMapAPIResponse();	
		  mockResponse.setLatitude(33.5879);
		  mockResponse.setLongitude(-111.8404);
		  List<Record> records =  new ArrayList<Record>();
		  records.add( new Record(1625504400L, 91.47f, 90.23f, "2021-07-05 10:00:00"));
		  records.add( new Record(1625508000L, 95.28f, 94.12f, "2021-07-05 11:00:00"));		  
		  mockResponse.setHourlyForecast(records);			   
		  mockWebServer.enqueue(new MockResponse()
				  	.setResponseCode(200)
				  	.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
					.setBody(new ObjectMapper().writeValueAsString(mockResponse))	
				);
		  WeatherForecastDto actual = openWeatherClient.populateWeatherForecast(dto);
		  assertEquals("Scottsdale", actual.getName());
		  assertEquals(2, actual.getHourlyForecast().size());
		  assertEquals(1625508000L, actual.getHourlyForecast().get(1).getDt().longValue());		 	  		
	}
	
	 @Test
	  public void populateWeatherForecast_NotFoundException() throws JsonProcessingException {
		  String errMsg = "Zipcode not found, please try another zipcode";
		  when(env.getProperty("error.zipcode.notfound.message")).thenReturn(errMsg);
		  when(env.getProperty("org.openweathermap.api.appid")).thenReturn("");
		  WeatherForecastDto dto = new WeatherForecastDto();
		  dto.setZip("85259");
		  dto.setName("Scottsdale");
		  dto.setLatitude(33.5879);
		  dto.setLongitude(-111.8404);
		  dto.setCountry("US");
		  
		  mockWebServer.enqueue(new MockResponse()
				  	.setResponseCode(404)
				  	.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)									
				);		  
		  Exception ex = assertThrows(ZipCodeNotFoundException.class, () ->  openWeatherClient.populateWeatherForecast(dto));	  
		  assertTrue(ex.getMessage().equals(errMsg));
	  }
	  
	  @Test
	  public void populateWeatherForecast_ServiceNotAvailableException() throws JsonProcessingException {
		  String errMsg = "Service is currently not available. PLease try another time";
		  when(env.getProperty("error.service.notavailable.message")).thenReturn(errMsg);
		  when(env.getProperty("org.openweathermap.api.appid")).thenReturn("");
		  WeatherForecastDto dto = new WeatherForecastDto();
		  dto.setZip("85259");
		  dto.setName("Scottsdale");
		  dto.setLatitude(33.5879);
		  dto.setLongitude(-111.8404);
		  dto.setCountry("US");
		  
		  mockWebServer.enqueue(new MockResponse()
				  	.setResponseCode(500)
				  	.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)									
				);		  
		  Exception ex = assertThrows(ServiceNotAvailableException.class, () ->  openWeatherClient.populateWeatherForecast(dto));	
		  assertTrue(ex.getMessage().equals(errMsg));
	  }
	

}
