package de.caritas.cob.messageservice.api.model.jsondeserializer;

import static de.caritas.cob.messageservice.api.model.VideoCallMessageDTO.EventTypeEnum.IGNORED_CALL;
import static de.caritas.cob.messageservice.testhelper.TestConstants.MESSAGE_ALIAS_DTO_EMPTY_ALIAS_JSON;
import static de.caritas.cob.messageservice.testhelper.TestConstants.MESSAGE_ALIAS_DTO_NULL_ALIAS_JSON;
import static de.caritas.cob.messageservice.testhelper.TestConstants.MESSAGE_FORWARD_ALIAS_JSON_WITH_DECODED_USERNAME;
import static de.caritas.cob.messageservice.testhelper.TestConstants.MESSAGE_FORWARD_ALIAS_JSON_WITH_ENCODED_USERNAME;
import static de.caritas.cob.messageservice.testhelper.TestConstants.RC_USER_ID;
import static de.caritas.cob.messageservice.testhelper.TestConstants.TIMESTAMP;
import static de.caritas.cob.messageservice.testhelper.TestConstants.USERNAME_DECODED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.messageservice.api.helper.UserHelper;
import de.caritas.cob.messageservice.api.model.AliasMessageDTO;
import de.caritas.cob.messageservice.api.model.ForwardMessageDTO;
import de.caritas.cob.messageservice.api.model.VideoCallMessageDTO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ClassUtils;

@ExtendWith(MockitoExtension.class)
class AliasJsonDeserializerTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final UserHelper userHelper = new UserHelper();
  private final AliasJsonDeserializer aliasJsonDeserializer = new AliasJsonDeserializer(userHelper);
  private final String decodedUsername = "username";
  private final String encodedUsername = userHelper.encodeUsername(decodedUsername);

  @Test
  void aliasJsonDeserializer_Schould_haveNoArgsConstructor() {
    assertTrue(ClassUtils.hasConstructor(AliasJsonDeserializer.class));
  }

  @Test
  void deserialize_Schould_convertAliasWithEncodedUsernameToForwardMessageDTO()
      throws IOException {
    ForwardMessageDTO result =
        deserializeOldAliasJson(MESSAGE_FORWARD_ALIAS_JSON_WITH_ENCODED_USERNAME)
            .getForwardMessageDTO();
    assertEquals(RC_USER_ID, result.getRcUserId());
    assertEquals(TIMESTAMP, result.getTimestamp());
    assertEquals(USERNAME_DECODED, result.getUsername());
  }

  @Test
  void deserialize_Schould_convertAliasWithDecodedUsernameToForwardMessageDTO()
      throws IOException {
    ForwardMessageDTO result =
        deserializeOldAliasJson(MESSAGE_FORWARD_ALIAS_JSON_WITH_DECODED_USERNAME)
            .getForwardMessageDTO();
    assertEquals(RC_USER_ID, result.getRcUserId());
    assertEquals(TIMESTAMP, result.getTimestamp());
    assertEquals(USERNAME_DECODED, result.getUsername());
  }

  @Test
  void deserialize_Schould_ReturnNull_IfAliasIsEmpty()
      throws IOException {
    AliasMessageDTO result = deserializeOldAliasJson(MESSAGE_ALIAS_DTO_EMPTY_ALIAS_JSON);
    assertNull(result);
  }

  @Test
  void deserialize_Schould_ReturnNull_IfAliasIsNull()
      throws IOException {
    AliasMessageDTO result = deserializeOldAliasJson(MESSAGE_ALIAS_DTO_NULL_ALIAS_JSON);
    assertNull(result);
  }

  @Test
  void deserialize_Should_returnAliasDTOWithDecodedUsername_When_usernameIsEncoded()
      throws Exception {
    String aliasMessageDTO = asJsonString(new AliasMessageDTO()
        .videoCallMessageDTO(new VideoCallMessageDTO()
            .eventType(IGNORED_CALL)
            .initiatorUserName(encodedUsername)
            .initiatorRcUserId("rcUserId")));

    AliasMessageDTO result = deserializeNewAliasJson(aliasMessageDTO);

    assertThat(result.getForwardMessageDTO(), nullValue());
    assertThat(result.getVideoCallMessageDTO(), notNullValue());
    assertThat(result.getVideoCallMessageDTO().getEventType(), is(IGNORED_CALL));
    assertThat(result.getVideoCallMessageDTO().getInitiatorUserName(), is(decodedUsername));
  }

  @Test
  void deserialize_Should_returnAliasDTOWithDecodedUsername_When_usernameIsDecoded()
      throws Exception {
    String aliasMessageDTO = asJsonString(new AliasMessageDTO()
        .videoCallMessageDTO(new VideoCallMessageDTO()
            .eventType(IGNORED_CALL)
            .initiatorUserName(decodedUsername)
            .initiatorRcUserId("rcUserId")));

    AliasMessageDTO result = deserializeNewAliasJson(aliasMessageDTO);

    assertThat(result.getForwardMessageDTO(), nullValue());
    assertThat(result.getVideoCallMessageDTO(), notNullValue());
    assertThat(result.getVideoCallMessageDTO().getEventType(), is(IGNORED_CALL));
    assertThat(result.getVideoCallMessageDTO().getInitiatorUserName(), is(decodedUsername));
  }

  @Test
  void deserialize_Should_returnAliasDTOWithDecodedUsernames_When_usernamesAreEncoded()
      throws Exception {
    String aliasMessageDTO = asJsonString(new AliasMessageDTO()
        .forwardMessageDTO(new ForwardMessageDTO()
            .message("message")
            .rcUserId("rcUserId")
            .timestamp("timestamp")
            .username(encodedUsername))
        .videoCallMessageDTO(new VideoCallMessageDTO()
            .eventType(IGNORED_CALL)
            .initiatorUserName(encodedUsername)
            .initiatorRcUserId("rcUserId")));

    AliasMessageDTO result = deserializeNewAliasJson(aliasMessageDTO);

    assertThat(result.getForwardMessageDTO(), notNullValue());
    assertThat(result.getForwardMessageDTO().getMessage(), is("message"));
    assertThat(result.getForwardMessageDTO().getRcUserId(), is("rcUserId"));
    assertThat(result.getForwardMessageDTO().getTimestamp(), is("timestamp"));
    assertThat(result.getForwardMessageDTO().getUsername(), is(decodedUsername));
    assertThat(result.getVideoCallMessageDTO(), notNullValue());
    assertThat(result.getVideoCallMessageDTO().getEventType(), is(IGNORED_CALL));
    assertThat(result.getVideoCallMessageDTO().getInitiatorUserName(), is(decodedUsername));
  }

  private AliasMessageDTO deserializeOldAliasJson(String json) throws IOException {
    InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
    JsonParser jsonParser = objectMapper.getFactory().createParser(stream);
    jsonParser.nextToken();
    jsonParser.nextToken();
    jsonParser.nextToken();
    DeserializationContext deserializationContext = objectMapper.getDeserializationContext();
    return aliasJsonDeserializer.deserialize(jsonParser, deserializationContext);
  }

  private AliasMessageDTO deserializeNewAliasJson(String json) throws IOException {
    JsonParser jsonParser = mock(JsonParser.class);
    when(jsonParser.getValueAsString()).thenReturn(json);
    return aliasJsonDeserializer.deserialize(jsonParser, null);
  }

  private String asJsonString(AliasMessageDTO aliasMessageDTO) throws JsonProcessingException {
    return objectMapper.writeValueAsString(aliasMessageDTO);
  }

}
