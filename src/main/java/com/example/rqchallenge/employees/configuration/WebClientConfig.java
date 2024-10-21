package com.example.rqchallenge.employees.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    /**
     * started the connection with the web with the given api
     * @return the webClient.builer()
     */
    @Value("$dummy.rest.api")
    private String api;
    @Bean
    public WebClient.Builder webClientBuilder() {
        // Configure the HttpClient
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000) // Set connection timeout to 15 seconds
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(15, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(15, TimeUnit.SECONDS)));

        // Create a ClientHttpConnector using the configured HttpClient
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        // Build the WebClient with the connector and return webclient
        return WebClient.builder()
                .clientConnector(connector)
                .defaultHeader("Content-Type", "application/json")
                .baseUrl(api);
    }

}
