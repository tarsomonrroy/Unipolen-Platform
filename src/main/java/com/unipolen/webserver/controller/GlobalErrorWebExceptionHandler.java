package com.unipolen.webserver.controller;

import com.unipolen.webserver.view.ViewGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

import java.util.EnumMap;
import java.util.Map;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends
		AbstractErrorWebExceptionHandler {

	public static final EnumMap<HttpStatus, String> statusToMessage = new EnumMap<>(HttpStatus.class);

	public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
		super(errorAttributes, resources, applicationContext);
		setMessageWriters(configurer.getWriters());
		setMessageReaders(configurer.getReaders());
	}

	static {
		statusToMessage.put(HttpStatus.INTERNAL_SERVER_ERROR, "Erro no Servidor");
		statusToMessage.put(HttpStatus.BAD_REQUEST, "Requisição Inválida");
		statusToMessage.put(HttpStatus.FORBIDDEN, "Acesso Negado");
		statusToMessage.put(HttpStatus.UNAUTHORIZED, "Acesso Restrito");
		statusToMessage.put(HttpStatus.NOT_FOUND, "Página Não Encontrada");
	}

	// constructors

	@Override
	protected RouterFunction<ServerResponse> getRoutingFunction(
			ErrorAttributes errorAttributes) {

		return RouterFunctions.route(
				RequestPredicates.all(), this::renderErrorResponse);
	}

	@Autowired
	Utils utils;

	public Mono<ServerResponse> renderErrorResponse(
			ServerRequest request) {

		Map<String, Object> errorPropertiesMap = getErrorAttributes(request,
				ErrorAttributeOptions.defaults());

		HttpStatus code;

		try {
			code = HttpStatus.valueOf( ((Integer)errorPropertiesMap.get("status")).intValue() );
		} catch (ClassCastException e) {
			code = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		Context ctx = new Context();
		ctx.setVariable("error", code.value());
		ctx.setVariable("message", statusToMessage.getOrDefault(code, "Ocorreu um erro, tente novamente mais tarde."));

		final HttpStatus fCode = code;


		return ServerResponse.status(fCode)
				.contentType(MediaType.TEXT_HTML)
				.body(new ViewGenerator(ctx, null).get("error"), DataBuffer.class);

	}
}