package no.ntnu.idata2001.contacts.model;

/**
 * Thrown to indicate that an attempt have been made to convert a string to an instance
 * of ContactDetails.
 */
public class ContactDetailsFormatException extends IllegalArgumentException {

  /**
   * Constructs a ContactDetailsFormatException with no detail message.
   */
  public ContactDetailsFormatException() {
    super("Could not convert the string to an ContactDetails-object..");
  }

  /**
   * Constructs a ContactDetailsFormatException with the specified detail message.
   * @param s the detail message
   */
  public ContactDetailsFormatException(String s) {
    super(s);
  }
}
