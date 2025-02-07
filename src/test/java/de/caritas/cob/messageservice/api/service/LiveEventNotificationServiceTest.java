package de.caritas.cob.messageservice.api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import de.caritas.cob.messageservice.api.service.helper.ServiceHelper;
import de.caritas.cob.messageservice.config.apiclient.ApiControllerFactory;
import de.caritas.cob.messageservice.userservice.generated.ApiClient;
import de.caritas.cob.messageservice.userservice.generated.web.LiveproxyControllerApi;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class LiveEventNotificationServiceTest {

  @InjectMocks private LiveEventNotificationService liveEventNotificationService;

  @Mock private LiveproxyControllerApi liveproxyControllerApi;
  @Mock private ServiceHelper serviceHelper;
  @Mock private ApiControllerFactory clientFactory;

  @BeforeEach
  void setup() {
    when(clientFactory.liveproxyControllerApi()).thenReturn(liveproxyControllerApi);
  }

  @Test
  void sendLiveEvent_Should_notTriggerLiveEvent_When_rcGroupIdIsNull() {
    this.liveEventNotificationService.sendLiveEvent(null, null, Optional.empty());

    verifyNoMoreInteractions(this.liveproxyControllerApi);
    verifyNoMoreInteractions(this.serviceHelper);
  }

  @Test
  void sendLiveEvent_Should_notTriggerLiveEvent_When_rcGroupIdIsEmpty() {
    this.liveEventNotificationService.sendLiveEvent(null, null, Optional.empty());

    verifyNoMoreInteractions(this.liveproxyControllerApi);
    verifyNoMoreInteractions(this.serviceHelper);
  }

  @Test
  void sendLiveEvent_Should_triggerLiveEventWithHeaders_When_rcGroupIdIsValid() {
    ApiClient apiClient = mock(ApiClient.class);
    when(this.liveproxyControllerApi.getApiClient()).thenReturn(apiClient);
    HttpHeaders headers = new HttpHeaders();
    headers.add("header 1", "value 1");
    headers.add("header 2", "value 2");
    when(serviceHelper.getKeycloakAndCsrfAndOriginHttpHeaders(anyString(), any(Optional.class)))
        .thenReturn(headers);

    this.liveEventNotificationService.sendLiveEvent("valid", "", Optional.empty());

    verify(this.liveproxyControllerApi, times(1)).sendLiveEvent("valid");
    verify(this.serviceHelper, times(1)).getKeycloakAndCsrfAndOriginHttpHeaders(anyString(), any());
    verify(apiClient, times(2)).addDefaultHeader(anyString(), anyString());
  }

  @Test
  void sendLiveEvent_Should_logError_When_apiClientThrowsRestClientException() {
    doThrow(new RestClientException(""))
        .when(this.liveproxyControllerApi)
        .sendLiveEvent(anyString());
    when(this.liveproxyControllerApi.getApiClient()).thenReturn(mock(ApiClient.class));
    when(serviceHelper.getKeycloakAndCsrfAndOriginHttpHeaders(anyString(), any(Optional.class)))
        .thenReturn(new HttpHeaders());

    this.liveEventNotificationService.sendLiveEvent("valid", "", Optional.empty());
  }
}
