package io.services.weather.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class KeySpecs {
    private int requestCount;
    private LocalDateTime firstRequestTime;

    public KeySpecs() {
        this.requestCount = 1;
        this.firstRequestTime = LocalDateTime.now();
    }


    public void incrementRequestCount() {
        this.requestCount++;
    }

    public void reset() {
        this.requestCount = 1;
        this.firstRequestTime = LocalDateTime.now();
    }
    
}
