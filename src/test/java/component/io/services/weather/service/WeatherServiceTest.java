package component.io.services.weather.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock;
import com.maciejwalkowiak.wiremock.spring.EnableWireMock;
import com.maciejwalkowiak.wiremock.spring.InjectWireMock;
import io.services.weather.Application;
import io.services.weather.domain.WeatherRequest;
import io.services.weather.domain.WeatherResponse;
import io.services.weather.entity.WeatherData;
import io.services.weather.repository.WeatherDataRepository;
import io.services.weather.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
@EnableWireMock(
        @ConfigureWireMock(name = "random-service")
)
public class SomeRandomTest {

    @MockBean
    private WeatherDataRepository repository;

    @Autowired
    private WeatherService weatherService;


    @InjectWireMock("random-service")
    private WireMockServer wiremock;


    @Test
    void name() {
        String city = "New York";
        String country = "US";

        when(repository.findWeatherDataByCityAndCountry(city, country)).thenReturn(Mono.empty());
        when(repository.save(any())).thenReturn(Mono.just(WeatherData.builder()
                .id(1L)
                .data("{\"name\":\"New York\",\"sys\":{\"country\":\"US\"},\"weather\":[{\"description\":\"Cloudy\"}]}")
                .city(city)
                .country(country)
                .timestamp(LocalDateTime.now().minusSeconds(2))
                .build()));


        wiremock.stubFor(get(urlPathEqualTo("/data/2.5/weather"))
                .withQueryParam("q", equalTo(city + "," + country))
                .withQueryParam("appid", equalTo("your-api-key"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"name\":\"New York\",\"sys\":{\"country\":\"US\"},\"weather\":[{\"description\":\"Cloudy\"}]}")));

        // When
        WeatherRequest request = new WeatherRequest(city, country);
        Mono<WeatherResponse> weatherResponseMono = weatherService.fetchWeather(request);

        // Then
        StepVerifier.create(weatherResponseMono)
                .expectNextMatches(response -> response.getCity().equals("New York") && response.getCountry().equals("US"))
                .verifyComplete();


    }
}
