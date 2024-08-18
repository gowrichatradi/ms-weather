package unit.io.services.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.services.weather.domain.GetWeatherResponse;
import io.services.weather.domain.WeatherRequest;
import io.services.weather.domain.WeatherResponse;
import io.services.weather.entity.WeatherData;
import io.services.weather.repository.WeatherDataRepository;
import io.services.weather.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.function.Function;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WeatherDataRepository repository;

    @Mock
    private ObjectMapper mapper;


    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weatherService, "hostname", "somehost");
        ReflectionTestUtils.setField(weatherService, "path", "/path/to/api");
        ReflectionTestUtils.setField(weatherService, "openWeatherApiKey", "dummyApiKey");
    }

    @Test
    void shouldFetchWeatherFromDatabaseWhenAvailable() throws JsonProcessingException {
        when(repository.findWeatherDataByCityAndCountry(any(), any())).thenReturn(Mono.just(WeatherData.builder()
                .id(1L)
                .timestamp(LocalDateTime.now().minusMinutes(10))
                .city("BVRM")
                .country("IN")
                .data("some-data")
                .build()));

        when(mapper.readValue("some-data", GetWeatherResponse.class)).thenReturn(new GetWeatherResponse());

        Mono<WeatherResponse> weatherResponseMono = weatherService.fetchWeather(WeatherRequest.builder()
                .city("BVRM")
                .country("IN")
                .build());

        StepVerifier.create(weatherResponseMono)
                .expectNextMatches(response -> response.getCity().equals("BVRM") && response.getCountry().equals("IN"))
                .verifyComplete();

        verifyNoInteractions(webClient);

    }


    @Test
    void shouldFetchFromApiWhenDatabaseNotAvailable() {
        when(repository.findWeatherDataByCityAndCountry(any(), any())).thenReturn(Mono.empty());

        WebClient.RequestHeadersUriSpec headersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        when(webClient.get()).thenReturn(headersUriSpec);
        when(headersUriSpec.uri(any(Function.class))).thenReturn(headersUriSpec);
        when(headersUriSpec.exchangeToMono(any())).thenReturn(Mono.just(WeatherResponse.builder()
                .city("BVRM")
                .country("IN")
                .weatherReport("Clar Skies")
                .build()));

        Mono<WeatherResponse> weatherResponseMono = weatherService.fetchWeather(WeatherRequest.builder()
                .city("BVRM")
                .country("IN")
                .build());

        StepVerifier.create(weatherResponseMono)
                .expectNextMatches(response -> response.getCity().equals("BVRM") && response.getCountry().equals("IN") && response.getWeatherReport().equals("Clar Skies"))
                .verifyComplete();

        verify(repository, times(1)).findWeatherDataByCityAndCountry(any(), any());
        verify(webClient, times(1)).get();
    }


}