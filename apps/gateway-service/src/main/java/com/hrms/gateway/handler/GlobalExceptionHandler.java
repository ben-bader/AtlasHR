package com.hrms.gateway.handler;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Global Exception Handler
 * 
 * Handles exceptions globally and returns consistent JSON error responses.
 */
@Configuration
@Order(-2)
@Slf4j
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		String message = "Internal Server Error";

		// JWT related exceptions
		if (ex instanceof JwtException || (ex.getCause() instanceof JwtException)) {
			status = HttpStatus.UNAUTHORIZED;
			message = "Invalid or expired JWT token";
		}
		// Default error handling
		else {
			message = ex.getMessage() != null ? ex.getMessage() : "An error occurred";
		}

		return sendErrorResponse(exchange, status, message);
	}

	/**
	 * Send JSON error response
	 */
	private Mono<Void> sendErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(status);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		try {
			ObjectMapper mapper = new ObjectMapper();
			ErrorResponse errorResponse = new ErrorResponse(
				status.value(),
				status.getReasonPhrase(),
				message
			);
			byte[] bytes = mapper.writeValueAsBytes(errorResponse);
			
			DataBufferFactory bufferFactory = response.bufferFactory();
			return response.writeWith(Mono.just(bufferFactory.wrap(bytes)));
		} catch (Exception e) {
			return Mono.error(e);
		}
	}

	/**
	 * Error response DTO
	 */
	public static class ErrorResponse {
		public int status;
		public String error;
		public String message;

		public ErrorResponse(int status, String error, String message) {
			this.status = status;
			this.error = error;
			this.message = message;
		}

		public int getStatus() {
			return status;
		}

		public String getError() {
			return error;
		}

		public String getMessage() {
			return message;
		}
	}

}
