package de.caritas.cob.messageservice;


import io.sentry.SentryOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentryConfiguration {

  @Value("${onlineberatung.sentry.dsn}")
  private String sentryDsn;

  @Value("${sentry.environment}")
  private String environment;


  @Bean
  public SentryOptions sentryOptions() {
    SentryOptions options = new SentryOptions();
    options.setDsn(sentryDsn);
    options.setEnvironment(environment);
    options.setTag("service", "MessageService");
    options.setRelease("2.0.0");
    options.setTracesSampleRate(0.5);
    options.setSendDefaultPii(false);
    return options;
  }
}
