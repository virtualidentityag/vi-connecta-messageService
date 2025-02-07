package de.caritas.cob.messageservice.api.exception;

import java.io.Serial;
import java.util.function.Consumer;

public class RocketChatBadRequestException extends CustomLoggableResponseException {

  @Serial
  private static final long serialVersionUID = 362702101121444833L;

  /**
   * Exception, when a Rocket.Chat API call for message posting fails due to a Bad Request
   * 
   * @param message
   */
  public RocketChatBadRequestException(String message, Consumer<String> loggingMethod) {
    super(message, loggingMethod);
  }

}
