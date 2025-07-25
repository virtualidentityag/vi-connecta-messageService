package de.caritas.cob.messageservice.api.exception;

import java.io.Serial;

public class CustomCryptoException extends Exception {

  @Serial
  private static final long serialVersionUID = 1232112575979020932L;

  /**
   * Exception when something with the encryption goes wrong
   * @param ex
   */
  public CustomCryptoException(Exception ex) {
    super(ex);
  }

}
