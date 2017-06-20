package com.gilbertojrequena.reactive.service;

import com.gilbertojrequena.reactive.conf.ClientConfiguration;
import com.gilbertojrequena.reactive.model.Foo;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

public class FooService {

    private final WebClient client;

    public FooService(ClientConfiguration configuration) {
        this.client = WebClient.create(configuration.getUrl());
    }

    public Flux<Foo> getFoos() {
        return client.get()
                .uri("/foos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Foo.class));
    }
}
