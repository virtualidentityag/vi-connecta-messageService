package de.caritas.cob.messageservice.api.model.rocket.chat.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.caritas.cob.messageservice.api.model.jsondeserializer.DecodeUsernameJsonDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Rocket.Chat user model (sub of MessagesDTO)
 * 
 * https://rocket.chat/docs/developer-guides/rest-api/groups/messages/
 *
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "U")
public class UserDTO {

  @Schema(required = true, example = "vppRFqjrzTsTZ6iEn")
  private String _id;

  @Schema(required = true, example = "test")
  @JsonDeserialize(using = DecodeUsernameJsonDeserializer.class)
  private String username;

  @Schema(required = true, example = "Mustermax")
  private String name;
}
