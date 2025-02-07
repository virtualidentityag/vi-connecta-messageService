package de.caritas.cob.messageservice.api.exception;

import java.io.Serial;

public class NoMasterKeyException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 362702101121444833L;

  /**
   * Exception, when no master-key is set
   *
   * @param message
   */
  public NoMasterKeyException(String message) {
    super(message);
  }

}
