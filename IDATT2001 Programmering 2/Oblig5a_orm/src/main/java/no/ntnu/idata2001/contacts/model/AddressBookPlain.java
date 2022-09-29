package no.ntnu.idata2001.contacts.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Represents an Address book containing contacts with contact details.
 * Based on the example in the book "Objects first with Java" by David J. Barnes
 * and Michael Kölling.
 *
 * <p>Each contact is stored in a TreeMap using the phone number as the key.
 *
 * @author David J. Barnes and Michael Kölling and Arne Styve
 * @version 2020.03.16
 */
public class AddressBookPlain implements Serializable, Iterable<ContactDetails>, AddressBook {
  // Storage for an arbitrary number of details.
  // We have chosen to use TreeMap instead of HashMap in this example, the
  // main difference being that a TreeMap is sorted. That is, the keys are sorted,
  // so when retrieving an Iterator from a TreeMap-collection, the iterator will
  // iterate in a sorted manner, which wil not be the case for a HashMap.
  // TreeMap is a bit less efficient than a HashMap in terms of searching, du to the
  // sorted order. For more details on the difference:
  // https://javatutorial.net/difference-between-hashmap-and-treemap-in-java
  private TreeMap<String, ContactDetails> book;

  /**
   * Creates an instance of the AddressBook, initialising the instance.
   */
  public AddressBookPlain() {
    book = new TreeMap<>();
  }

  /**
   * Add a new contact to the address book.
   *
   * @param contact The contact to be added.
   */
  @Override
  public void addContact(ContactDetails contact) {
    if (contact != null) {
      book.put(contact.getPhone(), contact);
    }
  }

  /**
   * Remove the contact with the given phonenumber from the address book.
   * The phone number should be one that is currently in use.
   *
   * @param phoneNumber The phone number to the contact to remove
   */
  @Override
  public void removeContact(String phoneNumber) {
      this.book.remove(phoneNumber);
  }

  /**
   * Returns all the contacts as a collection.
   *
   * @return all the contacts as a collection.
   */
  @Override
  public Collection<ContactDetails> getAllContacts() {
    return this.book.values();
  }

  @Override
  public Iterator<ContactDetails> iterator() {
    return this.book.values().iterator();
  }

  @Override
  public void close() {

  }
}
