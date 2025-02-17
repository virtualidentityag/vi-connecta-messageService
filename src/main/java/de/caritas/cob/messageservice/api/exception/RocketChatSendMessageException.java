package de.caritas.cob.messageservice.api.exception;

import java.io.Serial;

public class RocketChatSendMessageException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1232112575979020931L;

  /**
   * Exception, when a Rocket.Chat API call for message posting fails
   * 
   * @param ex the exception
   */
  public RocketChatSendMessageException(Exception ex) {
    super(ex);
  }

}
