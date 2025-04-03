package de.caritas.cob.messageservice.config;


import io.sentry.SentryOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SentryConfiguration {

  @Value("${onlineberatung.sentry.dsn}")
  private String sentryDsn;

  @Value("${sentry.environment}")
  private String environment;

  @Value("${sentry.sample-rate:0.5}")
  private Double sampleRate;


  @Bean
  public SentryOptions sentryOptions() {
    log.info("Configuring Sentry with DSN: {}", sentryDsn);
    SentryOptions options = new SentryOptions();
    options.setDsn(sentryDsn);
    options.setEnvironment(environment);
    options.setTag("service", "MessageService");
    options.setRelease("2.0.0");
    options.setTracesSampleRate(sampleRate);
    options.setSendDefaultPii(false);
    return options;
  }
}
