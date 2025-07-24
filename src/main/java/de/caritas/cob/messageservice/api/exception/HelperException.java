package de.caritas.cob.messageservice.api.exception;

import java.io.Serial;

public class HelperException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = -1321906171569622899L;

  /**
   *
   * Exception for helper errors
   *
   */
  public HelperException(String message, Exception exception) {
    super(message, exception);
  }
}
