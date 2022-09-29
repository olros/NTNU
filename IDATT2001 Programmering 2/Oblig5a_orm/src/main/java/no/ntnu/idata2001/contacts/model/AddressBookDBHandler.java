package no.ntnu.idata2001.contacts.model;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

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
public class AddressBookDBHandler implements Serializable, Iterable<ContactDetails>, AddressBook {
	// Storage for an arbitrary number of details.
	// We have chosen to use TreeMap instead of HashMap in this example, the
	// main difference being that a TreeMap is sorted. That is, the keys are sorted,
	// so when retrieving an Iterator from a TreeMap-collection, the iterator will
	// iterate in a sorted manner, which wil not be the case for a HashMap.
	// TreeMap is a bit less efficient than a HashMap in terms of searching, du to the
	// sorted order. For more details on the difference:
	// https://javatutorial.net/difference-between-hashmap-and-treemap-in-java
	private EntityManagerFactory efact;

	/**
	 * Creates an instance of the AddressBook, initialising the instance.
	 */
	public AddressBookDBHandler() {
		efact = Persistence.createEntityManagerFactory("contacts-idi");
	}

	/**
	 * Add a new contact to the address book.
	 *
	 * @param contact The contact to be added.
	 */
	@Override
	public void addContact(ContactDetails contact) {
		if (contact != null) {
			EntityManager em = efact.createEntityManager();
			try {
				em.getTransaction().begin();
				em.persist(contact);
				em.getTransaction().commit();
			} finally {
				em.close();
			}
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
		EntityManager em = efact.createEntityManager();
		try {
			ContactDetails contact = em.find(ContactDetails.class, phoneNumber);
			em.getTransaction().begin();
			em.remove(contact);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	/**
	 * Returns all the contacts as a collection.
	 *
	 * @return all the contacts as a collection.
	 */
	@Override
	public Collection<ContactDetails> getAllContacts() {
		EntityManager em = efact.createEntityManager();
		try {
			Query q = em.createQuery("SELECT Object(o) FROM ContactDetails o", ContactDetails.class);
			return q.getResultList();
		} finally {
			em.close();
		}
	}

	@Override
	public Iterator<ContactDetails> iterator() {
		return getAllContacts().iterator();
	}

	@Override
	public void close() {
		efact.close();
	}
}
