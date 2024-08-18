package integration.io.services.weather.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock;
import com.maciejwalkowiak.wiremock.spring.EnableWireMock;
import com.maciejwalkowiak.wiremock.spring.InjectWireMock;
import io.services.weather.Application;
import io.services.weather.domain.WeatherRequest;
import io.services.weather.domain.WeatherResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@EnableWireMock(
        @ConfigureWireMock(name = "weather-service")
)
class WeatherControllerTest {

    @LocalServerPort
    private int port;


    @InjectWireMock("weather-service")
    private WireMockServer wiremock;

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    void shouldReturnWeatherDataForValidRequest() {

        String city = "London";
        String country = "GB";

        wiremock.stubFor(get(urlPathEqualTo("/data/2.5/weather"))
                .withQueryParam("q", equalTo(city + "," + country))
                .withQueryParam("appid", equalTo("ADHSAKSAHHDS"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"name\":\"London\",\"sys\":{\"country\":\"GB\"},\"weather\":[{\"description\":\"Sunny\"}]}")));

        String url = "http://localhost:" + port + "/api/weather";

        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Api-Key", "ADHSAKSAHHDS");

        WeatherRequest request = WeatherRequest.builder()
                .city("London")
                .country("GB")
                .build();

        HttpEntity<WeatherRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<WeatherResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, WeatherResponse.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCity()).isEqualTo("London");
        assertThat(response.getBody().getCountry()).isEqualTo("GB");

    }

}