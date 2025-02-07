package de.caritas.cob.messageservice.api.exception;

import java.io.Serial;

public class RocketChatPostMarkGroupAsReadException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = -5666387091852981692L;

  /**
   * Exception, when a Rocket.Chat API call for marking a room as read fails
   * 
   * @param ex
   */
  public RocketChatPostMarkGroupAsReadException(Exception ex) {
    super(ex);
  }

}
