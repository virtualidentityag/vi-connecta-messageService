package de.caritas.cob.messageservice.api.service.statistics;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import de.caritas.cob.messageservice.api.service.statistics.event.CreateMessageStatisticsEvent;
import de.caritas.cob.messageservice.statisticsservice.generated.web.model.EventType;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.messaging.Message;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

  private static final String FIELD_NAME_STATISTICS_ENABLED = "statisticsEnabled";
  private static final String FIELD_NAME_RABBIT_EXCHANGE_NAME = "rabbitMqExchangeName";
  private static final String RABBIT_EXCHANGE_NAME = "exchange";
  private static final String PAYLOAD = "payload";
  @Mock Logger logger;
  private CreateMessageStatisticsEvent createMessageStatisticsEvent;
  private EventType eventType = EventType.ASSIGN_SESSION;
  @InjectMocks private StatisticsService statisticsService;
  @Mock private AmqpTemplate amqpTemplate;

  @BeforeEach
  void setup() {
    createMessageStatisticsEvent = Mockito.mock(CreateMessageStatisticsEvent.class);
    Mockito.lenient().when(createMessageStatisticsEvent.getEventType()).thenReturn(eventType);
    Mockito.lenient().when(createMessageStatisticsEvent.getPayload()).thenReturn(Optional.of(PAYLOAD));
    setField(statisticsService, FIELD_NAME_RABBIT_EXCHANGE_NAME, RABBIT_EXCHANGE_NAME);
  }

  @Test
  void fireEvent_Should_NotSendStatisticsMessage_WhenStatisticsIsDisabled() {

    setField(statisticsService, FIELD_NAME_STATISTICS_ENABLED, false);
    statisticsService.fireEvent(createMessageStatisticsEvent);
    verify(amqpTemplate, times(0))
        .convertAndSend(eq(RABBIT_EXCHANGE_NAME), anyString(), any(Message.class));
  }

  @Test
  void fireEvent_Should_SendStatisticsMessage_WhenStatisticsIsEnabled() {

    setField(statisticsService, FIELD_NAME_STATISTICS_ENABLED, true);
    when(createMessageStatisticsEvent.getEventType()).thenReturn(eventType);
    when(createMessageStatisticsEvent.getPayload()).thenReturn(Optional.of(PAYLOAD));

    statisticsService.fireEvent(createMessageStatisticsEvent);
    verify(amqpTemplate, times(1))
        .convertAndSend(
            eq(RABBIT_EXCHANGE_NAME),
            anyString(),
            eq(
                buildPayloadMessage()));
  }

  @Test
  void fireEvent_Should_NotSendMessageToQueue_WhenPayloadIsEmpty() {

    setField(statisticsService, FIELD_NAME_STATISTICS_ENABLED, true);
    when(createMessageStatisticsEvent.getPayload()).thenReturn(Optional.empty());
    statisticsService.fireEvent(createMessageStatisticsEvent);
    verifyNoInteractions(amqpTemplate);
  }

  @Test
  void fireEvent_Should_UseEventTypeAsTopicAndSendPayloadOfEvent() {

    setField(statisticsService, FIELD_NAME_STATISTICS_ENABLED, true);
    statisticsService.fireEvent(createMessageStatisticsEvent);
    verify(amqpTemplate, times(1))
        .convertAndSend(
            RABBIT_EXCHANGE_NAME,
            eventType.toString(),
            buildPayloadMessage());
  }

  private org.springframework.amqp.core.Message buildPayloadMessage() {
    return MessageBuilder.withBody(PAYLOAD.getBytes(StandardCharsets.UTF_8))
        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
        .build();
  }

}
