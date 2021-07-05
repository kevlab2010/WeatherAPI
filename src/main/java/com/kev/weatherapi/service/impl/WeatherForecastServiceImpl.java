package com.kev.weatherapi.service.impl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.kev.weatherapi.exception.ServiceNotAvailableException;
import com.kev.weatherapi.exception.ZipCodeNotFoundException;
import com.kev.weatherapi.model.GeoLocation;
import com.kev.weatherapi.model.Record;
import com.kev.weatherapi.model.WeatherForecastDto;
import com.kev.weatherapi.model.response.OpenWeatherMapAPIResponse;
import com.kev.weatherapi.service.WeatherForecastService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Service
public class WeatherForecastServiceImpl implements WeatherForecastService{
	
	@Autowired
	private Environment env;
	
	private GeoClient geoClient;	
	
	private OpenWeatherClient openWeatherClient;
	
	@Autowired
	public WeatherForecastServiceImpl(GeoClient geoClient, OpenWeatherClient openWeatherClient){
		this.geoClient = geoClient;
		this.openWeatherClient = openWeatherClient;
	}
	
	@Override
	public WeatherForecastDto getHourlyForecastByZipcode(WeatherForecastDto dto) {

		// Call GeoAIP to populate the coordinates for a given zipcode
		dto = geoClient.populateCoordinates(dto);

		// Call OpenWeatherMap API to get hourly weather forecast 
		dto = openWeatherClient.populateWeatherForecast(dto);
		
		dto = constrainRecordToOneDay(dto);
		dto = populateMinMaxTemp(dto);
		dto = populateDateTimeStr(dto);
		 
		return dto;
	}
	
	private WeatherForecastDto populateMinMaxTemp(WeatherForecastDto dto) {				
		List<Record> tempList = new ArrayList<>();
		dto.getHourlyForecast().forEach( r -> tempList.add(r));		
		tempList.sort( (Record r1, Record r2) -> Float.compare(r1.getTemp(), r2.getTemp()) );
		dto.setMinTemp(tempList.get(0));
		dto.setMaxTemp(tempList.get(tempList.size() - 1));		
		return dto;
	}

	private WeatherForecastDto constrainRecordToOneDay(WeatherForecastDto dto){		
		 if(!dto.getHourlyForecast().isEmpty()) {
			 ZoneId zoneId = ZoneId.of(dto.getTimezone());
			 LocalDate fromDate = LocalDate.now(zoneId);
			 LocalDate toDate = LocalDate.now(zoneId).plusDays(2);
			 dto.getHourlyForecast().removeIf( record -> {
				 LocalDate date = Instant.ofEpochSecond(record.getDt()).atZone(zoneId).toLocalDate();
				 return !(date.isAfter(fromDate) && date.isBefore(toDate));
			 });
		 }		 
		return dto;
	}
	
	private WeatherForecastDto populateDateTimeStr(WeatherForecastDto dto) {			
		 if(!dto.getHourlyForecast().isEmpty()) {			 
			 dto.getHourlyForecast().forEach( record ->  record.setDt_txt( createDateTimeStr(record.getDt(), dto.getTimezone())) ); 
		 }		
		return dto;
	}
	
	private String createDateTimeStr(long epoch, String timezone ) {	
		LocalDateTime datetime = Instant.ofEpochSecond(epoch).atZone(ZoneId.of(timezone)).toLocalDateTime();		
		String datetimeStr = datetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		return datetimeStr;
	}
	
}
