package com.kev.weatherapi.restcontroller;

import java.util.Map;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kev.weatherapi.exception.InValidQueryParameterException;
import com.kev.weatherapi.model.WeatherForecastDto;
import com.kev.weatherapi.model.response.WeatherForecastResponse;
import com.kev.weatherapi.service.ValidationService;
import com.kev.weatherapi.service.WeatherForecastService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/forecast")
@Validated
@Slf4j
public class WeatherForecastRestController {

	@Autowired
	private WeatherForecastService weatherForecastService;
	
	@Autowired
	private ValidationService validationService;

	@GetMapping(path = "/hourly")
	public ResponseEntity<WeatherForecastDto> getHourlyForecastByZipcode(	
			@RequestParam(name = "zip", required = true, defaultValue = "85259") String zip ){
		
		validationService.validateQueryParam("zip", zip);		
		
		WeatherForecastDto dto = new WeatherForecastDto();
		dto.setZip(zip);
		
		dto = weatherForecastService.getHourlyForecastByZipcode(dto);
		
		WeatherForecastResponse response = new WeatherForecastResponse();
		BeanUtils.copyProperties(dto, response);
		
		return new ResponseEntity(response, HttpStatus.OK);	
	}
}
