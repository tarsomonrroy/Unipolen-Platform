package com.unipolen.webserver.model;

import com.unipolen.webserver.App;
import com.unipolen.webserver.model.defaults.DefaultsService;
import org.reactivestreams.Publisher;
import org.springframework.ui.ModelMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class ModelMapBuilder {

	private final HashMap<String, Publisher<Object>> futureModelMap;
	private ModelMapBuilder() {
		futureModelMap = new HashMap<>();
	}

	public static ModelMapBuilder create() {return new ModelMapBuilder();}

	public ModelMapBuilder withDefaults() {
		return include(App.CONTEXT.getBean(DefaultsService.class).getMappings(), "defaults");
	}

	@SuppressWarnings("unchecked")
	public <T> ModelMapBuilder include(Publisher<T> objectPublisher, Class<T> objClass) {
		futureModelMap.put(objClass.getSimpleName().toLowerCase(Locale.ROOT), (Publisher<Object>) objectPublisher);
		return this;
	}

	@SuppressWarnings("unchecked")
	public ModelMapBuilder include(Publisher objectPublisher, String name) {
		futureModelMap.put(name, objectPublisher);
		return this;
	}

	public Mono<ModelMap> build() {
		return Mono.create(sink -> {
			if (futureModelMap.isEmpty()) sink.success();

			ModelMap modelMap = new ModelMap();

			Flux<Map.Entry<String, Publisher<Object>>> entryFlux = Flux.fromIterable(futureModelMap.entrySet());
			AtomicInteger cnt = new AtomicInteger(futureModelMap.size());

			entryFlux.subscribe(
					entry -> {
						Publisher<Object> objectPublisher = entry.getValue();
						assert objectPublisher instanceof Flux<Object> || objectPublisher instanceof Mono<Object>;

						if (objectPublisher instanceof Flux)
							objectPublisher = Mono.from(  ((Flux<Object>)objectPublisher).collectList()  );

						((Mono<Object>)objectPublisher)
							.switchIfEmpty(Mono.just(Void.class))
							.subscribe(obj -> {
										if (obj != Void.class) modelMap.put(entry.getKey(), obj);
										if (cnt.decrementAndGet() == 0) sink.success(modelMap);
									}
							);
					}
			);
		});
	}
}
