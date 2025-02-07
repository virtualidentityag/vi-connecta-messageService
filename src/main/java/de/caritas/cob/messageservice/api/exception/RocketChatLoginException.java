package de.caritas.cob.messageservice.api.exception;

import java.io.Serial;

public class RocketChatLoginException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 5198347832036308397L;

  /**
   * Exception when login for technical user in Rocket.Chat fails
   * 
   * @param ex
   */
  public RocketChatLoginException(Exception ex) {
    super(ex);
  }

  public RocketChatLoginException(String message) {
    super(message);
  }
}
