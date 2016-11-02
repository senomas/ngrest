package com.senomas.ngrest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.senomas.common.rs.ExceptionResponse;
import com.senomas.common.rs.ResourceNotFoundException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

	@ExceptionHandler({ ResourceNotFoundException.class })
	public ResponseEntity<Object> handleResourceNotFoundException(Exception ex, WebRequest request) {
		log.warn(request.getContextPath() + " " + ex.getMessage(), ex);
		return new ResponseEntity<Object>(new ExceptionResponse(ex), new HttpHeaders(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler({ AccessDeniedException.class })
	public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
		log.warn(request.getContextPath() + " " + ex.getMessage(), ex);
		return new ResponseEntity<Object>(new ExceptionResponse(ex), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleGeneric(Exception ex, WebRequest request) {
		log.warn(request.getContextPath() + " " + ex.getMessage(), ex);
		return new ResponseEntity<Object>(new ExceptionResponse(ex), new HttpHeaders(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
