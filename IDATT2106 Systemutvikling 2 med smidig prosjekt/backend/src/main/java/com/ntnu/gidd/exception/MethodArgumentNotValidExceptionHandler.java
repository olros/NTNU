package com.ntnu.gidd.exception;

import com.ntnu.gidd.util.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MethodArgumentNotValidExceptionHandler {
		@ExceptionHandler({MethodArgumentNotValidException.class})
		public ResponseEntity<Object> handleValid(MethodArgumentNotValidException ex) {
			return new ResponseEntity<Object>( new Response(ex.getAllErrors().get(0).getDefaultMessage()), HttpStatus.NOT_ACCEPTABLE);
		}
		
}
