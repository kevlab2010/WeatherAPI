package com.kev.weatherapi.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.kev.weatherapi.model.Record;
import com.kev.weatherapi.model.WeatherForecastDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class WeatherForecastServiceTest {
	
	@Mock
	private Environment env;
	
	@Mock
	private GeoClient geoClient;
	
	@Mock
	private OpenWeatherClient openWeatherClient;
	
	@InjectMocks
	private WeatherForecastServiceImpl weatherForecastService;
	
	@Test
	public void getHourlyForecastByZipcode() {
		
		 List<Record> expects =  new ArrayList<Record>();
		 expects.add( new Record(1625504400L, 91.47f, 90.23f, "2021-07-05 10:00:00"));
		 expects.add( new Record(1625508000L, 95.28f, 94.12f, "2021-07-05 11:00:00"));
		 expects.add( new Record(1625511600L, 102.53f, 100.42f, "2021-07-05 12:00:00"));	
	
		 WeatherForecastDto dto = new WeatherForecastDto();
		  dto.setZip("85259");
		  dto.setName("Scottsdale");
		  dto.setLatitude(33.5879);
		  dto.setLongitude(-111.8404);
		  dto.setCountry("US");
		  dto.setTimezone("America/Phoenix");
		  dto.setTimezone_offset(-25200L);
		  List<Record> records =  new ArrayList<Record>();
		  records.add( new Record(LocalDateTime.now().plusHours(2).atZone(ZoneId.of(dto.getTimezone())).toEpochSecond(), 80.77f, 80.43f, ""));
		  records.add( new Record(LocalDateTime.now().plusHours(3).atZone(ZoneId.of(dto.getTimezone())).toEpochSecond(), 93.34f, 90.23f, ""));
		  records.add( new Record(LocalDateTime.now().plusDays(1).plusHours(1).atZone(ZoneId.of(dto.getTimezone())).toEpochSecond(), 91.47f, 90.20f, ""));
		  records.add( new Record(LocalDateTime.now().plusDays(1).plusHours(2).atZone(ZoneId.of(dto.getTimezone())).toEpochSecond(), 90.82f, 91.73f, ""));
		  records.add( new Record(LocalDateTime.now().plusDays(1).plusHours(3).atZone(ZoneId.of(dto.getTimezone())).toEpochSecond(), 101.12f, 97.73f, ""));
		  records.add( new Record(LocalDateTime.now().plusDays(2).plusHours(1).atZone(ZoneId.of(dto.getTimezone())).toEpochSecond(), 101.12f, 98.28f, ""));
		  records.add( new Record(LocalDateTime.now().plusDays(2).plusHours(2).atZone(ZoneId.of(dto.getTimezone())).toEpochSecond(), 120.12f, 80.85f, ""));
		  
		  dto.setHourlyForecast(records);
		
		when(openWeatherClient.populateWeatherForecast(any())).thenReturn(null);
		when(openWeatherClient.populateWeatherForecast(any())).thenReturn(dto);
		
		WeatherForecastDto actual = weatherForecastService.getHourlyForecastByZipcode(dto);
		
		assertEquals(90.82f, actual.getMinTemp().getTemp());
		assertEquals(101.12f, actual.getMaxTemp().getTemp());
	}
	
	
}
