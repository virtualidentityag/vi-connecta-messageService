package de.caritas.cob.messageservice.api.service.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.caritas.cob.messageservice.api.helper.AuthenticatedUser;
import de.caritas.cob.messageservice.api.service.TenantHeaderSupplier;
import java.util.Enumeration;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class ServiceHelperTest {

  private final String FIELD_NAME_CSRF_TOKEN_HEADER_PROPERTY = "csrfHeaderProperty";
  private final String FIELD_NAME_CSRF_TOKEN_COOKIE_PROPERTY = "csrfCookieProperty";
  private final String CSRF_TOKEN_HEADER_VALUE = "X-CSRF-TOKEN";
  private final String CSRF_TOKEN_COOKIE_VALUE = "CSRF-TOKEN";
  private final String AUTHORIZATION = "Authorization";

  @Mock
  private AuthenticatedUser authenticatedUser;

  @Spy
  private TenantHeaderSupplier tenantHeaderSupplier;

  private HttpServletRequest httpServletRequest  = new MockHttpServletRequest();

  private ServletRequestAttributes requestAttributes = new ServletRequestAttributes(httpServletRequest);

  @InjectMocks
  private ServiceHelper serviceHelper;

  @Mock
  private Enumeration<String> headers;

  @BeforeEach
  void setup() throws NoSuchFieldException, SecurityException {
    givenRequestContextIsSet();
    ReflectionTestUtils.setField(serviceHelper, FIELD_NAME_CSRF_TOKEN_HEADER_PROPERTY, CSRF_TOKEN_HEADER_VALUE);
    ReflectionTestUtils.setField(serviceHelper, FIELD_NAME_CSRF_TOKEN_COOKIE_PROPERTY, CSRF_TOKEN_COOKIE_VALUE);
  }

  private void givenRequestContextIsSet() {
    RequestContextHolder.setRequestAttributes(requestAttributes);
  }

  /**
   * Tests for method: getKeycloakAndCsrfHttpHeaders
   */

  @Test
  void getKeycloakAndCsrfHttpHeaders_Should_Return_HeaderWithCorrectContentType() {

    HttpHeaders result = serviceHelper.getKeycloakAndCsrfAndOriginHttpHeaders(
        RandomStringUtils.randomAlphanumeric(16), Optional.empty());
    assertEquals(MediaType.APPLICATION_JSON, result.getContentType());

  }

  @Test
  void getKeycloakAndCsrfHttpHeaders_Should_Return_HeaderWithCookiePropertyNameFromProperties() {

    HttpHeaders result = serviceHelper.getKeycloakAndCsrfAndOriginHttpHeaders(
        RandomStringUtils.randomAlphanumeric(16), Optional.empty());
    assertTrue(result.get("Cookie").toString().startsWith("[" + CSRF_TOKEN_COOKIE_VALUE + "="));

  }

  @Test
  void getKeycloakAndCsrfHttpHeaders_Should_Return_HeaderWithPropertyNameFromProperties() {

    HttpHeaders result = serviceHelper.getKeycloakAndCsrfAndOriginHttpHeaders(
        RandomStringUtils.randomAlphanumeric(16), Optional.empty());
    assertNotNull(result.get(CSRF_TOKEN_HEADER_VALUE));

  }

  @Test
  void getKeycloakAndCsrfHttpHeaders_Should_Return_HeaderWithBearerAuthorization() {

    HttpHeaders result = serviceHelper.getKeycloakAndCsrfAndOriginHttpHeaders(
        RandomStringUtils.randomAlphanumeric(16), Optional.empty());
    assertNotNull(result.get(AUTHORIZATION));

  }

}
