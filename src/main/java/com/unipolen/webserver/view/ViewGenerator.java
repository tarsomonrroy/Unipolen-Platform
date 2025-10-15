package com.unipolen.webserver.view;

import com.unipolen.webserver.model.ModelMapBuilder;
import io.netty.buffer.PooledByteBufAllocator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.ISpringWebFluxTemplateEngine;
import org.thymeleaf.spring6.SpringWebFluxTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.web.IWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

/**
 * Communicates with Thymeleaf reactively to generate views.
 */
public class ViewGenerator {
	private static final ISpringWebFluxTemplateEngine templateEngine;
	private static final ClassLoaderTemplateResolver htmlResolver;
	private final @NonNull Context ctx;
	private final @Nullable Mono<ModelMap> modelMapMono;

	static {
		templateEngine = new SpringWebFluxTemplateEngine();

		htmlResolver = new ClassLoaderTemplateResolver();
		htmlResolver.setPrefix("templates/");
		htmlResolver.setCacheable(false);
		htmlResolver.setSuffix(".html");
		htmlResolver.setTemplateMode("HTML");
		htmlResolver.setForceTemplateMode(true);
		htmlResolver.setCharacterEncoding("utf-8");

		((SpringWebFluxTemplateEngine)templateEngine).addTemplateResolver(htmlResolver);
	}

	public ViewGenerator() {
		ctx = new Context();
		modelMapMono = ModelMapBuilder.create().withDefaults().build();
	}

	public ViewGenerator(@NonNull Context ctx) {
		this.ctx = ctx;
		modelMapMono = ModelMapBuilder.create().withDefaults().build();
	}

	public ViewGenerator(Mono<ModelMap> modelMapMono) {
		ctx = new Context();
		this.modelMapMono = modelMapMono;
	}

	public ViewGenerator(@NonNull Context ctx, Mono<ModelMap> modelMapMono) {
		this.ctx = ctx;
		this.modelMapMono = modelMapMono;
	}

	private Mono<DataBuffer> getFromCtxAlone(@NonNull String path) {
		return Mono.from(templateEngine.processStream(
				path,
				Set.of(),
				ctx,
				new NettyDataBufferFactory(new PooledByteBufAllocator()),
				MediaType.TEXT_HTML,
				StandardCharsets.UTF_8));
	}

	public Mono<DataBuffer> get(@NonNull String path) {
		if (modelMapMono == null) return getFromCtxAlone(path);

		return modelMapMono.flatMap(modelMap -> {
			Flux<Map.Entry<String, Object>> entryFlux = Flux.fromIterable(modelMap.entrySet());
			entryFlux.subscribe(entry -> ctx.setVariable(entry.getKey(), entry.getValue()));
			return entryFlux.then(getFromCtxAlone(path));
		}).switchIfEmpty(getFromCtxAlone(path));
	}

	public String getBlocking(@NonNull String path) {
		return templateEngine.process(path, ctx);
	}

	public String redirect(String path) {
		if (path == null) path = "/";
		return "<html style=\"background-color:black;color:white\"><title>Redirecionando..</title>" +
				"<h1>Redirecionando..</h1>" +
				"<script>window.location.replace('"+path+"')</script></html>";
	}


}
