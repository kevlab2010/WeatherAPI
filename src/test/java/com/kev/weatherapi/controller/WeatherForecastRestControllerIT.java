package com.kev.weatherapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.kev.weatherapi.model.Record;
import com.kev.weatherapi.model.response.WeatherForecastResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WeatherForecastRestControllerIT {
	
	@Autowired
	  private WebTestClient webTestClient;
	
	@Test
	public void contextLoads() {
		WeatherForecastResponse response = this.webTestClient.get()
			.uri( uriBuilder -> uriBuilder
									.path("/forecast/hourly")
									.queryParam("zip", "85259")
									.build())
			.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
			.exchange()
			.returnResult(WeatherForecastResponse.class)
			.getResponseBody()
			.blockLast(Duration.ofMillis(1500));

		assertEquals(33.5879f, response.getLatitude().floatValue());
		assertEquals(-111.8404f, response.getLongitude().floatValue());
		assertEquals(24, response.getHourlyForecast().size());

		List<Record> tempList = new ArrayList<>();
		response.getHourlyForecast().forEach( r -> tempList.add(r));		
		tempList.sort( (Record r1, Record r2) -> Float.compare(r1.getTemp(), r2.getTemp()) );
		
		System.out.println(response.getMinTemp());
		System.out.println(tempList.get(0));
		assertEquals(response.getMinTemp(), tempList.get(0));		
	}
}
