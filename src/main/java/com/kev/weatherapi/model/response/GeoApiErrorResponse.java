package com.kev.weatherapi.model.response;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class GeoApiErrorResponse {
	private String cod;
	private String message;
}
