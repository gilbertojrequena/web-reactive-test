package com.gilbertojrequena.reactive.conf;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientConfiguration {

    private String url;
}
