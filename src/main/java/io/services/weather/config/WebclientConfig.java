package io.services.weather.config;


import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebclientConfig {

    @Bean
    public WebClient webClient() {
        HttpClient client = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS));
                }).compress(true);

        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(client)).build();
    }
}

