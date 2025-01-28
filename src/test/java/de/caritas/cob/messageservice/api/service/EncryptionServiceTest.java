package de.caritas.cob.messageservice.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import de.caritas.cob.messageservice.api.exception.CustomCryptoException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class EncryptionServiceTest {

  private final String KEY_MASTER = "MasterKeyTestKey";
  private final String KEY_APPLICATION = "ApplicationTestKey";
  private final String KEY_SESSION = "SessionTestKey";
  private final String KEY_SESSION_WRONG = "WrongSessionTestKey";

  private final String MESSAGE_PLAIN = "Das hier ist jetzt mal eine Test-Message";
  private final String MESSAGE_ENCRYPTED =
      "enc:uWHNUkWrQJikGnVpknvB3SkzT1RWHJuY0igDT9p7fGFHWECLBpV2+0eIZF6Qi7J0";

  @InjectMocks
  private EncryptionService encryptionService;

  @Mock
  private LogService logService;

  @BeforeEach
  void setup() throws NoSuchFieldException {
    ReflectionTestUtils.setField(encryptionService, "fragment_applicationKey", KEY_APPLICATION);


    encryptionService.updateMasterKey(KEY_MASTER);
  }

  @Test
  void check_setup() {
    assertEquals(KEY_MASTER, encryptionService.getMasterKey(), "MasterKey was not properly set");
    assertEquals(KEY_APPLICATION,
        encryptionService.getApplicationKey(),
        "ApplicationKey was not properly set");
  }

  @Test
  void updateMasterKey_Should_UpdateMasterKeyFragment() {
    encryptionService.updateMasterKey(KEY_MASTER);
    assertEquals(KEY_MASTER, encryptionService.getMasterKey(), "Cannot properly set MasterKey");
  }

  @Test
  void encrypt_Should_ReturnEncryptedText_WhenProvidedWithValidParameters()
      throws  CustomCryptoException {
    String encryptMessage = encryptionService.encrypt(MESSAGE_PLAIN, KEY_SESSION);
    assertEquals(MESSAGE_ENCRYPTED, encryptMessage, "Did not get the expected encryption result.");
  }

  @Test
  void encrypt_Should_ReturnWrongEncryptedText_WhenProvidedWithInvalidParameters()
      throws  CustomCryptoException {
    String encryptMessage = encryptionService.encrypt(MESSAGE_PLAIN, KEY_SESSION_WRONG);
    Assertions.assertNotEquals(MESSAGE_ENCRYPTED,
        encryptMessage,
        "Did not get the expected encryption result.");
  }

  @Test
  void decrypt_Should_ReturnDecryptedText_WhenProvidedWithValidParameters()
      throws  CustomCryptoException {
    String decryptedMessage = encryptionService.decrypt(MESSAGE_ENCRYPTED, KEY_SESSION);
    assertEquals(MESSAGE_PLAIN, decryptedMessage, "Did not get the expected decrypted result.");
  }

  @Test
  void decrypt_Should_ReturnWrongDecryptedText_WhenProvidedWithInvalidParameters() {
    try {
      encryptionService.decrypt(MESSAGE_ENCRYPTED, KEY_SESSION_WRONG);
      fail("The expected BadPaddingException due to wrong password was not thrown.");
    } catch (CustomCryptoException ex) {
      assertTrue(true, "Expected BadPaddingException thrown");
    }
  }

  @Test
  void decrypt_Should_ReturnNull_WhenMessageIsNull() throws CustomCryptoException {
    assertNull(encryptionService.decrypt(null, KEY_MASTER));
  }

}
