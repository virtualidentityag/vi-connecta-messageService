package de.caritas.cob.messageservice;

import static org.junit.jupiter.api.Assertions.assertNull;

import de.caritas.cob.messageservice.config.AuthenticatedUserConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AuthenticatedUserConfigTest {

  @MockBean
  AuthenticatedUserConfig authenticatedUserConfig;

  @Test
  void getAuthenticatedUser_Should_ReturnNullWhenNoUserSessionActive() {
    assertNull(authenticatedUserConfig.getAuthenticatedUser());
  }
}
