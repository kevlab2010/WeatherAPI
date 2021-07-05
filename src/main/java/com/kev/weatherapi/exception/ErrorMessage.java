package com.kev.weatherapi.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter @AllArgsConstructor
public class ErrorMessage {
	private LocalDateTime timestamp;
	private String errorCode;
	private String message;
	private String path;
}
