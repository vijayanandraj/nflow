package io.nflow.rest.config;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

import javax.inject.Named;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nflow.engine.internal.config.EngineConfiguration;
import io.nflow.engine.internal.config.NFlow;

@Configuration
@Import(EngineConfiguration.class)
@ComponentScan("io.nflow.rest")
public class RestConfiguration {

  public static final String REST_OBJECT_MAPPER = "nflowRestObjectMapper";

  @Bean
  @Named(REST_OBJECT_MAPPER)
  public ObjectMapper nflowRestObjectMapper(@NFlow ObjectMapper nflowObjectMapper) {
    ObjectMapper restObjectMapper = nflowObjectMapper.copy();
    restObjectMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
    return restObjectMapper;
  }
}
