package com.kev.weatherapi.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode
public class Record{
	
	private Long dt;
	private float temp;
	private float feels_like;
	
	// "dt_txt":"2019-10-05 03:00:00"
	private String dt_txt;
	
	
	
}
