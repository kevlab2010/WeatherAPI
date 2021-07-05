package com.kev.weatherapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.kev.weatherapi.exception.InValidQueryParameterException;
import com.kev.weatherapi.service.ValidationService;

import lombok.extern.slf4j.Slf4j;

@Service
@PropertySource("classpath:messages.properties")
public class ValidationServiceImpl implements ValidationService {
	
	@Autowired
	Environment env;
	
	public void validateQueryParam(String name, Object value) {	
		if("zip".equalsIgnoreCase(name)){			
			if(!((String)value).matches("[0-9]{5}")) throw new InValidQueryParameterException(env.getProperty("error.validate.queryparam.zipcode"));		
		}		
	}
}
