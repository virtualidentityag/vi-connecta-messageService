package de.caritas.cob.messageservice.api.helper;

import static de.caritas.cob.messageservice.testhelper.TestConstants.USERNAME_DECODED;
import static de.caritas.cob.messageservice.testhelper.TestConstants.USERNAME_ENCODED;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserHelperTest {

  private UserHelper userHelper;

  @BeforeEach
  void setup() {
    this.userHelper = new UserHelper();
  }

  @Test
  void encodeUsername_Should_ReturnEncodedUsernameWithReplacedPaddingAndAddedPrefix_WhenDecodedUsernameIsGiven() {
    assertEquals(USERNAME_ENCODED, userHelper.encodeUsername(USERNAME_DECODED));
  }

  @Test
  void encodeUsername_Should_ReturnEncodedUsername_WhenEncodedUsernameIsGiven() {
    assertEquals(USERNAME_ENCODED, userHelper.encodeUsername(USERNAME_ENCODED));
  }

  @Test
  void decodeUsername_Should_ReturnDecodedUsername_WhenEncodedUsernameIsGiven() {
    assertEquals(USERNAME_DECODED, userHelper.decodeUsername(USERNAME_ENCODED));
  }

  @Test
  void decodeUsername_Should_ReturnDecodedUsername_WhenDecodedUsernameIsGiven() {
    assertEquals(USERNAME_DECODED, userHelper.decodeUsername(USERNAME_DECODED));
  }

}
