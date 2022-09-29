package no.ntnu.idata2001.contacts.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Holds details about a contact, like name, address and phone number.
 * Based on the example in the book "Objects first with Java" by David J. Barnes
 * and Michael Kölling.
 *
 * @author David J. Barnes and Michael Kölling and Arne Styve
 * @version 2020.03.16
 */
@Entity
public class ContactDetails implements Comparable<ContactDetails>, Serializable {
//  @GeneratedValue(strategy= GenerationType.IDENTITY)
//  @Column(name = "id", unique = true)
//  private int id;
  @Id
  private String phone;
  private String name;
  private String address;

  /**
   * Initiates the contact details
   */
  public ContactDetails() {}

  /**
   * Set up the contact details. All details are trimmed to remove
   * trailing white space.
   *
   * @param name    The name.
   * @param phone   The phone number.
   * @param address The address.
   */
  public ContactDetails(String name, String phone, String address) {
    // Use blank strings if any of the arguments is null.
    if (name == null) {
      name = "";
    }
    if (phone == null) {
      phone = "";
    }
    if (address == null) {
      address = "";
    }
    this.name = name.trim();
    this.phone = phone.trim();
    this.address = address.trim();
  }

//  public int getId() {
//		return id;
//	}
//
//  public void setId(int id) {
//		this.id = id;
//	}

  /**
   * Returns the name.
   *
   * @return The name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the contact.
   * If the name is <code>null</code>, or is empty (length is 0), a
   * {@link java.lang.IllegalArgumentException} will be thrown.
   *
   * @param name the new name of the contact
   * @throws IllegalArgumentException if the name is <code>null</code> or empty.
   */
  public void setName(String name) {
    if (name == null) throw new IllegalArgumentException("Name should not be null!!");
    if (name.trim().length() == 0) throw new IllegalArgumentException("Name cannot be empty.");
    this.name = name;
  }

  /**
   * Returns the phone number.
   *
   * @return The telephone number.
   */
  public String getPhone() {
    return phone;
  }

  /**
   * Sets the phone number of the contact.
   * If the phone number is <code>null</code>, or is empty (length is 0), a
   * {@link java.lang.IllegalArgumentException} will be thrown.
   *
   * @param phoneNumber the new name of the contact
   * @throws IllegalArgumentException if the name is <code>null</code> or empty.
   */
  public void setPhone(String phoneNumber) {
    if (phoneNumber == null) throw new IllegalArgumentException("Phone number should not be null!!");
    if (phoneNumber.trim().length() == 0) throw new IllegalArgumentException("Phone number cannot be empty.");
    this.phone = phoneNumber;
  }

  /**
   * Returns the address.
   *
   * @return The address.
   */
  public String getAddress() {
    return address;
  }

  /**
   * Sets the address of the contact.
   * If the address is <code>null</code>, or is empty (length is 0), a
   * {@link java.lang.IllegalArgumentException} will be thrown.
   *
   * @param address the new name of the contact
   * @throws IllegalArgumentException if the name is <code>null</code> or empty.
   */
  public void setAddress(String address) {
    if (address == null) throw new IllegalArgumentException("Address should not be null!!");
    if (address.trim().length() == 0) throw new IllegalArgumentException("Address cannot be empty.");
    this.address = address;
  }

  /**
   * Test for content equality between two objects.
   *
   * @param other The object to compare to this one.
   * @return true if the argument object is a set
   *         of contact details with matching attributes.
   */
  public boolean equals(Object other) {
    if (other instanceof ContactDetails) {
      ContactDetails otherDetails = (ContactDetails) other;
      return name.equals(otherDetails.getName())
        && phone.equals(otherDetails.getPhone())
        && address.equals(otherDetails.getAddress());
    } else {
      return false;
    }
  }


  /**
   * Compare these details against another set, for the purpose
   * of sorting. The fields are sorted by name, phone, and address.
   *
   * @param otherDetails The details to be compared against.
   * @return  a negative integer if this comes before the parameter,
   *          zero if they are equal and a positive integer if this
   *          comes after the second.
   */
  public int compareTo(ContactDetails otherDetails) {
    int comparison = name.compareTo(otherDetails.getName());
    if (comparison != 0) {
      return comparison;
    }
    comparison = phone.compareTo(otherDetails.getPhone());
    if (comparison != 0) {
      return comparison;
    }
    return address.compareTo(otherDetails.getAddress());
  }

  /**
   * Compute a hashcode using the rules to be found in
   * "Effective Java", by Joshua Bloch.
   *
   * @return A hashcode for ContactDetails.
   */
  public int hashCode() {
    int code = 17;
    code = 37 * code + name.hashCode();
    code = 37 * code + phone.hashCode();
    code = 37 * code + address.hashCode();
    return code;
  }

  /**
   * Returns a multi-line string containing the name, phone, and address. Only to
   * be used for debugging purposes. NEVER to be used as part of the interaction with
   * the user!!
   *
   * @return A multi-line string containing the name, phone, and address.
   */
  @Override
  public String toString() {
    return name + "\n" + phone + "\n" + address;
  }

}