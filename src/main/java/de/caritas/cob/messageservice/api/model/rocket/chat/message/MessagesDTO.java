package de.caritas.cob.messageservice.api.model.rocket.chat.message;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.caritas.cob.messageservice.api.model.AliasMessageDTO;
import de.caritas.cob.messageservice.api.model.AttachmentDTO;
import de.caritas.cob.messageservice.api.model.FileDTO;
import de.caritas.cob.messageservice.api.model.jsondeserializer.AliasJsonDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Rocket.Chat message model (sub of MessageStreamDTO)
 * 
 * https://rocket.chat/docs/developer-guides/rest-api/groups/messages/
 *
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Messages")
public class MessagesDTO {

  @Schema(required = true, example = "M73fE4WhYF4peYB3s")
  private String _id;

  @Schema(required = false, example = "%7B%0A%20%20%22message%22%3A%20")
  @JsonDeserialize(using = AliasJsonDeserializer.class)
  private AliasMessageDTO alias;

  @Schema(required = true, example = "fR2Rz7dmWmHdXE8uz")
  private String rid;

  @Schema(required = true, example = "Lorem ipsum dolor sit amet, consetetur...")
  private String msg;

  @Schema(required = true, example = "2018-11-15T09:33:00.057Z")
  private String ts;

  @Schema(required = true)
  private UserDTO u;

  @Schema(required = true)
  private boolean unread;

  @JsonIgnore
  @Schema(required = true)
  private String[] mentions;

  @JsonIgnore
  @Schema(required = true)
  private String[] channels;

  @Schema(required = true, example = "2018-11-15T09:33:00.067Z")
  private String _updatedAt;

  @Schema(required = false)
  private AttachmentDTO[] attachments;

  @Schema(required = false)
  private FileDTO file;

  @Schema
  private String t;

  @JsonIgnore
  public @NonNull String getCreatorId() {
    return u.get_id();
  }

  @JsonIgnore
  public String getFileId() {
    return isNull(file) ? null : file.getId();
  }

  @JsonIgnore
  public boolean hasFile() {
    return nonNull(file) && nonNull(file.getId());
  }
}
