package com.gilbertojrequena.reactive.service;

import com.gilbertojrequena.reactive.conf.ClientConfiguration;
import com.gilbertojrequena.reactive.model.Foo;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;
import static java.lang.String.format;

public class FooServiceTest {

    private static final String HOST = "localhost";
    private static final String FOOS_URL = "/foos";
    private static final String CONTENT_TYPE = "Content-type";
    private WireMockServer wireMockServer;
    private WireMock wireMock;
    private FooService fooService;

    @Before
    public void setUp() throws Exception {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        wireMock = new WireMock(HOST, wireMockServer.port());
        System.out.println("port = " + wireMockServer.port());
        fooService = new FooService(ClientConfiguration.builder().url(format("http://%s:%d", HOST, wireMockServer.port())).build());
    }

    @Test
    public void createFluxFromEmptyArrayJsonResponse() throws Exception {
        ResponseDefinitionBuilder responseBuilder = aResponse()
                .withStatus(200)
                .withBody("[]")
                .withHeader(CONTENT_TYPE, APPLICATION_JSON);

        wireMock.register(get(FOOS_URL).willReturn(responseBuilder));

        Flux<Foo> foosFlux = fooService.getFoos();

        StepVerifier.create(foosFlux)
                .verifyComplete();

        wireMock.verifyThat(getRequestedFor(urlEqualTo(FOOS_URL)));
    }

    @Test
    public void createFluxFromOnFooObject() throws Exception {
        ResponseDefinitionBuilder responseBuilder = aResponse()
                .withStatus(200)
                .withBody("[{\"value\": \"reactive\"}]")
                .withHeader(CONTENT_TYPE, APPLICATION_JSON);

        wireMock.register(get(FOOS_URL).willReturn(responseBuilder));

        Flux<Foo> foosFlux = fooService.getFoos();

        StepVerifier.create(foosFlux)
                .expectNextMatches(foo -> "reactive".equals(foo.getValue()))
                .verifyComplete();

        wireMock.verifyThat(getRequestedFor(urlEqualTo(FOOS_URL)));
    }

    @Test
    public void createFluxFromMultipleFooObjects() throws Exception {
        ResponseDefinitionBuilder responseBuilder = aResponse()
                .withStatus(200)
                .withBody("[{\"value\": \"reactive\"}, {\"value\": \"web\"}]")
                .withHeader(CONTENT_TYPE, APPLICATION_JSON);

        wireMock.register(get(FOOS_URL).willReturn(responseBuilder));

        Flux<Foo> foosFlux = fooService.getFoos();

        StepVerifier.create(foosFlux)
                .expectNextMatches(foo -> "reactive".equals(foo.getValue()))
                .expectNextMatches(foo -> "web".equals(foo.getValue()))
                .verifyComplete();

        wireMock.verifyThat(getRequestedFor(urlEqualTo(FOOS_URL)));
    }

}