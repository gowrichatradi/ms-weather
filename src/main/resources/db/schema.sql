CREATE TABLE IF NOT EXISTS weather_data (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              city VARCHAR(255) NOT NULL,
                              country VARCHAR(255) NOT NULL,
                              data CLOB NOT NULL,
                              timestamp TIMESTAMP NOT NULL
);