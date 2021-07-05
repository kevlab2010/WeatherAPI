package com.kev.weatherapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter @ToString
public class GeoLocation {
	
	private String zip;
	private String name;
	
	@JsonProperty(value = "lat")
	private Double latitude;
	
	@JsonProperty(value = "lon")
	private Double longitude ;
	
	private String country;
	
}
