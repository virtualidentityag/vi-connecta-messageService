package de.caritas.cob.messageservice.api.helper;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserTest {

  @Test
  void AuthenticatedUser_Should_ThrowNullPointerExceptionWhenArgumentsAreNull() {
    assertThrows(NullPointerException.class, () -> {
      new AuthenticatedUser(null, null, null, null, null);
    });
  }

  @Test
  void AuthenticatedUser_Should_ThrowNullPointerExceptionWhenUserIdIsNull() {
    assertThrows(NullPointerException.class, () -> {
      AuthenticatedUser authenticatedUser = new AuthenticatedUser();
      authenticatedUser.setUserId(null);
    });
  }

  @Test
  void AuthenticatedUser_Should_ThrowNullPointerExceptionWhenUsernameIsNull() {
    assertThrows(NullPointerException.class, () -> {
      AuthenticatedUser authenticatedUser = new AuthenticatedUser();
      authenticatedUser.setUsername(null);
    });
  }

  @Test
  void AuthenticatedUser_Should_ThrowNullPointerExceptionWhenAccessTokenIsNull() {
    assertThrows(NullPointerException.class, () -> {
      AuthenticatedUser authenticatedUser = new AuthenticatedUser();
      authenticatedUser.setAccessToken(null);
    });
  }
}
