package com.example.bank.config;

import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfiguration {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerFactoryWebServerFactoryCustomizer() {
        return (server) -> {
            server.addConnectorCustomizers((connector) -> {
                ProtocolHandler protocolHandler = connector.getProtocolHandler();
                if (protocolHandler instanceof AbstractHttp11Protocol) {
                    ((AbstractHttp11Protocol) protocolHandler).setKeepAliveTimeout(60000);
                    ((AbstractHttp11Protocol) protocolHandler).setMaxKeepAliveRequests(600);
                }
            });
        };
    }
}
