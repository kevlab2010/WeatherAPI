package com.kev.weatherapi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class WeatherForecastDto {
	
	private String zip;
	private String name;
	
	@JsonProperty(value = "lat")
	private Double latitude;
	
	@JsonProperty(value = "lon")
	private Double longitude ;
	
	private String country;
	private String timezone;
	private Long timezone_offset;
	
	private Record minTemp;
	private Record maxTemp;
	
	@JsonProperty(value = "hourly")
	private List<Record> hourlyForecast;



}
