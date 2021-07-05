package com.kev.weatherapi.service.impl;

import java.time.Duration;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.kev.weatherapi.exception.ServiceNotAvailableException;
import com.kev.weatherapi.exception.ZipCodeNotFoundException;
import com.kev.weatherapi.model.GeoLocation;
import com.kev.weatherapi.model.WeatherForecastDto;

import reactor.netty.http.client.HttpClient;

@Service
public class GeoClient {

	private WebClient.Builder webClientBuilder;
	
	@Autowired
	private Environment env;
	
	private String url;
	
	@Autowired
	public GeoClient(WebClient.Builder webClientBuilder, @Value("${org.openweathermap.api.geo.baseurl}") String url) {
		this.webClientBuilder = webClientBuilder;
		this.url = url;
	}

	public WeatherForecastDto populateCoordinates(WeatherForecastDto dto) {
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		queryParams.add("zip", dto.getZip());
		queryParams.add("appid", env.getProperty("org.openweathermap.api.appid"));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams);

		HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofMillis(2000));

		GeoLocation geoResponse = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
				.build().method(HttpMethod.GET)
				.uri(uriBuilder.toUriString()).headers(httpHeaders -> {
					httpHeaders.addAll(headers);
				}).exchangeToMono(response -> {
					if (response.statusCode() == HttpStatus.OK)
						return response.bodyToMono(GeoLocation.class);
					else if (response.statusCode().equals(HttpStatus.NOT_FOUND))
						throw new ZipCodeNotFoundException(env.getProperty("error.zipcode.notfound.message"));
					else
						throw new ServiceNotAvailableException(env.getProperty("error.service.notavailable.message"));
				}).block();
		
		BeanUtils.copyProperties(geoResponse, dto);
		return dto;
	}

}
