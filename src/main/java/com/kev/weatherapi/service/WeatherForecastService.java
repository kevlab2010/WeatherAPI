package com.kev.weatherapi.service;

import com.kev.weatherapi.model.WeatherForecastDto;


public interface WeatherForecastService {	
	WeatherForecastDto getHourlyForecastByZipcode(WeatherForecastDto dto);
}
