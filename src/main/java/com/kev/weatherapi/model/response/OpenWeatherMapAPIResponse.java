package com.kev.weatherapi.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kev.weatherapi.model.Record;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter @ToString
public class OpenWeatherMapAPIResponse {
	
	@JsonProperty(value = "lat")
	private Double latitude;
	
	@JsonProperty(value = "lon")
	private Double longitude ;

	private String timezone;
	private Long timezone_offset;
	
	@JsonProperty(value = "hourly")
	private List<Record> hourlyForecast;
	
}
