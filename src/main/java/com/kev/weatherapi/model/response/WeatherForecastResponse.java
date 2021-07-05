package com.kev.weatherapi.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.kev.weatherapi.model.Record;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class WeatherForecastResponse {

	private String zip;
	private String name;
	private String country;

	@JsonProperty(value = "latitude")
	private Double latitude;
	
	@JsonProperty(value = "longitude")
	private Double longitude ;
	
	private String timezone;
	
	@JsonProperty(value = "min_temp_hour")
	private Record minTemp;
	
	@JsonProperty(value = "max_temp_hour")
	private Record maxTemp;

	@JsonProperty(value = "hourly")
	private List<Record> hourlyForecast;

}


