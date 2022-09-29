package no.ntnu.idata2001.contacts.views;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import no.ntnu.idata2001.contacts.model.ContactDetails;


/**
 * A dialog used to get the necessary information about a contact from the
 * user, in order to be able to create a ContactDetails instance to be added to the
 * register. The dialog can be opened in 3 different modes:
 * <ul>
 * <li> EDIT - Used for editing an existing contact.</li>
 * <li> NEW - Used for entering information to create a new contact.</li>
 * <li> INFO - Used to show non-editable info of a contact</li>
 * </ul>
 *
 * @author Arne Styve
 * @version 2020-03-16
 */
public class ContactDetailsDialog extends Dialog<ContactDetails> {

  /**
   * The mode of the dialog. If the dialog is opened to edit an existing
   * Contact, the mode is set to <code>Mode.EDIT</code>. If the dialog is
   * opened to create a new contact, the <code>Mode.NEW</code> is used.
   */
  public enum Mode {
    NEW, EDIT, INFO
  }

  /**
   * The mode of the dialog. NEW if new contact, EDIT if edit existing
   * contact.
   */
  private final Mode mode;

  /**
   * Holds the ContactDetails instance to edit, if any.
   */
  private ContactDetails existingContact = null;

  /**
   * Creates an instance of the ContactDetailsDialog to get information to
   * create a new instance of ContactDetails.
   */
  public ContactDetailsDialog() {
    super();
    this.mode = Mode.NEW;
    // Create the content of the dialog
    createContent();

  }

  /**
   * Creates an instance of the ContactDetailsDialog dialog.
   *
   * @param contact the contact instance to edit
   * @param editable  if set to <code>true</code>, the dialog will enable
   *                  editing of the fields in the dialog. if <code>false</code> the
   *                  information will be displayed in non-editable fields.
   */
  public ContactDetailsDialog(ContactDetails contact, boolean editable) {
    super();
    if (editable) {
      this.mode = Mode.EDIT;
    } else {
      this.mode = Mode.INFO;
    }
    this.existingContact = contact;
    // Create the content of the dialog
    createContent();
  }

  /**
   * Creates the content of the dialog.
   */
  private void createContent() {
    // Set title depending upon mode...
    switch (this.mode) {
      case EDIT:
        setTitle("Contact Details - Edit");
        break;

      case NEW:
        setTitle("Contact Details - Add");
        break;

      case INFO:
        setTitle("Contact Details");
        break;

      default:
        setTitle("Contact Details - UNKNOWN MODE...");
        break;

    }

    // Set the button types.
    getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    TextField name = new TextField();
    name.setPromptText("Name");

    TextField address = new TextField();
    address.setPromptText("Address");

    TextField phoneNumber = new TextField();
    phoneNumber.setPromptText("Phone number");

    // Fill inn data from the provided Newspaper, if not null.
    if ((mode == Mode.EDIT) || (mode == Mode.INFO)) {
      name.setText(existingContact.getName());
      address.setText(existingContact.getAddress());
      phoneNumber.setText(existingContact.getPhone());
      // Set to non-editable if Mode.INFO
      if (mode == Mode.INFO) {
        name.setEditable(false);
        address.setEditable(false);
        phoneNumber.setEditable(false);
      }
    }

    grid.add(new Label("Name:"), 0, 0);
    grid.add(name, 1, 0);
    grid.add(new Label("Address:"), 0, 1);
    grid.add(address, 1, 1);
    grid.add(new Label("Phone number:"), 0, 2);
    grid.add(phoneNumber, 1, 2);

    getDialogPane().setContent(grid);

    // Convert the result to ContactDetails-instance when the OK button is clicked.
    // Check out: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Dialog.html#setResultConverter-javafx.util.Callback-
    // and: https://docs.oracle.com/javase/8/javafx/api/javafx/util/Callback.html
    setResultConverter(
        (ButtonType button) -> {
        ContactDetails result = null;
        if (button == ButtonType.OK) {

          if (mode == Mode.NEW) {
            result = new ContactDetails(name.getText(), phoneNumber.getText(), address.getText());
          } else if (mode == Mode.EDIT) {
            existingContact.setName(name.getText());
            existingContact.setAddress(address.getText());
            existingContact.setPhone(phoneNumber.getText());

            result = existingContact;
          }
        }
        return result;
      }
    );
  }
}

