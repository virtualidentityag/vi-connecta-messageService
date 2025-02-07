package de.caritas.cob.messageservice.api.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UrlEncodingDecodingUtilsTest {

  private static final String DECODED_STRING = "töst#$";
  private static final String ENCODED_STRING = "t%C3%B6st%23%24";

  @Test
  void urlEncodeString_Should_ReturnEncodedString() {
    assertEquals(ENCODED_STRING, UrlEncodingDecodingUtils.urlEncodeString(DECODED_STRING));
  }

  @Test
  void urlDecodeString_Should_ReturnEncodedString() {
    assertEquals(DECODED_STRING, UrlEncodingDecodingUtils.urlDecodeString(ENCODED_STRING));
  }

}
