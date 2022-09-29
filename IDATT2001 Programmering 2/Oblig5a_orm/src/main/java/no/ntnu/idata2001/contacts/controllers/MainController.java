package no.ntnu.idata2001.contacts.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import no.ntnu.idata2001.contacts.model.*;
import no.ntnu.idata2001.contacts.views.ContactDetailsDialog;
import no.ntnu.idata2001.contacts.views.ContactsApp;

/**
 * The main controller for the application. Handles all actions related to the
 * buttons in the user interface: "Add Contact", "Delete" ... "Exit".
 * The controller (in a MVC-structure) takes care of the link between
 * the user interface view-part, and the model in the business layer of
 * the application.
 * The Controller and View have to work very close together.
 *
 * @author Arne Styve
 * @version 2020-03-16
 */
public class MainController {
  private final Logger logger;

  // File used for object serialization. Used by the methods
  // saveAddressBookToFile() and loadAddressBookFromFile()
  private static final String DATA_FILE_NAME = "addressbook.dat";

  /**
   * Creates an instance of the MainController class, initialising
   * the logger.
   */
  public MainController() {
    this.logger = Logger.getLogger(getClass().toString());
  }


  /**
   * Display the input dialog to get input to create a new Contact.
   * If the user confirms creating a new contact, a new instance
   * of ContactDetails is created and added to the AddressBook provided.
   *
   * @param addressBook the address book to add the new contact to.
   * @param parent      the parent calling this method. Use this parameter to access public methods
   *                    in the parent, like updateObservableList().
   */
  public void addContact(AddressBook addressBook, ContactsApp parent) {

    ContactDetailsDialog contactsDialog = new ContactDetailsDialog();

    Optional<ContactDetails> result = contactsDialog.showAndWait();

    if (result.isPresent()) {
      ContactDetails newContactDetails = result.get();
      addressBook.addContact(newContactDetails);
      parent.updateObservableList();
    }
  }

  /**
   * Edit the selected item.
   *
   * @param selectedContact the contact to edit. Changes made by the user are updated on the
   *                        selectedContact object provided.
   * @param parent          the parent view making the call
   */
  public void editContact(ContactDetails selectedContact, ContactsApp parent) {
    if (selectedContact == null) {
      showPleaseSelectItemDialog();
    } else {
      ContactDetailsDialog contactDialog = new ContactDetailsDialog(selectedContact, true);
      contactDialog.showAndWait();

      parent.updateObservableList();
    }
  }

  /**
   * Deletes the Contact selected in the table. If no Contact is
   * selected, nothing is deleted, and the user is informed that he/she must
   * select which Contact to delete.
   *
   * @param selectedContact the Contact to delete. If no Contact has been selected,
   *                        this parameter will be <code>null</code>
   * @param addressBook     the contact register to delete the selectedContact from
   * @param parent          the parent view making the call.
   */
  public void deleteContact(ContactDetails selectedContact,
                            AddressBook addressBook,
                            ContactsApp parent) {
    if (selectedContact == null) {
      showPleaseSelectItemDialog();
    } else {
      if (showDeleteConfirmationDialog()) {
        addressBook.removeContact(selectedContact.getPhone());
        parent.updateObservableList();
      }
    }
  }


  /**
   * Import contacts from a .CSV-file chosen by the user.
   *
   * @param addressBook the address book to import the read contacts into
   * @param parent      the parent making the call to this method. Used for refreshing the
   *                    Observable list used by the TableView.
   */
  public void importFromCsv(AddressBook addressBook, ContactsApp parent) {
    FileChooser fileChooser = new FileChooser();

    // Set extension filter for .csv-file
    FileChooser.ExtensionFilter extFilter =
        new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
    fileChooser.getExtensionFilters().add(extFilter);

    // Show save open dialog
    File file = fileChooser.showOpenDialog(null);
    if (file != null) {
      try {
        AddressBookFileHandler.importFromCsv(addressBook, file);
        parent.updateObservableList();
      } catch (IOException ioe) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("File Import Error");
        alert.setHeaderText("Error during CSV-import.");
        alert.setContentText("Details: " + ioe.getMessage());
        alert.showAndWait();
      }
    }
  }

  /**
   * Export all contacts in the address book to a CSV-file specified by the user.
   *
   * @param addressBook the address book to export contacts from.
   */
  public void exportToCsv(AddressBook addressBook) {
    FileChooser fileChooser = new FileChooser();

    // Set extension filter for .csv-file
    FileChooser.ExtensionFilter extFilter =
        new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
    fileChooser.getExtensionFilters().add(extFilter);

    // Show save file dialog
    File file = fileChooser.showSaveDialog(null);
    if (file != null) {
      try {
        AddressBookFileHandler.exportToCsv((AddressBookPlain) addressBook, file);
      } catch (IOException ioe) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("File Export Error");
        alert.setHeaderText("Error during CSV-export.");
        alert.setContentText("Details: " + ioe.getMessage());
        alert.showAndWait();
      }
    }
  }

  /**
   * Saves an entire address book to a file using object serializing. The address book
   * is saved to a file called "addressbook.dat".
   *
   * @param addressBook the address book to save
   */
  public void saveAddressBookToFile(AddressBook addressBook) {
    try {
      File outFile = new File(DATA_FILE_NAME);
      AddressBookFileHandler.saveToFile(addressBook, outFile);
    } catch (IOException ioe) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("File Save Error");
      alert.setHeaderText("Error while saving the address book.");
      alert.setContentText("Details: " + ioe.getMessage());
      alert.showAndWait();
    }
  }

  /**
   * Loads an entire address book from a file using object serialization.
   * The address book is loaded from the file "addressbook.dat".
   * If file was read successfully, an instance of AddressBook is returned.
   *
   * @return an address book populated by contact details loaded from the file.
   */
  public AddressBook loadAddressBookFromFile() {
    File inFile = new File(DATA_FILE_NAME);
    return AddressBookFileHandler.loadFromFile(inFile);
  }

  /**
   * Loads an entire address book from a database.
   * If file was read successfully, an instance of AddressBook is returned.
   *
   * @return an address book populated by contact details loaded from the database.
   */
  public AddressBookDBHandler loadAddressBookFromDatabase() {
    return new AddressBookDBHandler();
  }

  /**
   * Exit the application. Displays a confirmation dialog.
   */
  public void exitApplication() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirmation Dialog");
    alert.setHeaderText("Exit Application ?");
    alert.setContentText("Are you sure you want to exit this application?");

    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent() && (result.get() == ButtonType.OK)) {
      // ... user choose OK
      Platform.exit();
    }
  }


  /**
   * Displays an example of an alert (info) dialog. In this case an "about"
   * type of dialog.
   *
   * @param version the version of the application, to be displayed in the dialog.
   */
  public void showAboutDialog(String version) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information Dialog - About");
    alert.setHeaderText("Contacts Register\nv" + version);
    alert.setContentText("A brilliant application created by\n"
        + "(C)Arne Styve\n"
        + "2020-03-16");

    alert.showAndWait();
  }

  /**
   * Displays a login dialog using a custom dialog.
   * Just to demonstrate the {@link javafx.scene.control.PasswordField}-control.
   */
  public void showLogInDialog() {
    // Create the custom dialog.
    Dialog<Pair<String, String>> dialog = new Dialog<>();
    dialog.setTitle("Login Dialog");
    dialog.setHeaderText("Look, a Custom Login Dialog");

    // Set the button types.
    ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

    // Create the username and password labels and fields.
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    TextField username = new TextField();
    username.setPromptText("Username");

    PasswordField password = new PasswordField();
    password.setPromptText("Password");

    grid.add(new Label("Username:"), 0, 0);
    grid.add(username, 1, 0);
    grid.add(new Label("Password:"), 0, 1);
    grid.add(password, 1, 1);

    // Enable/Disable login button depending on whether a username was entered.
    Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
    loginButton.setDisable(true);

    // Do some validation .
    username.textProperty().addListener((observable, oldValue, newValue) ->
        loginButton.setDisable(newValue.trim().isEmpty()));

    dialog.getDialogPane().setContent(grid);

    // Request focus on the username field by default.
    Platform.runLater(username::requestFocus);

    // Convert the result to a username-password-pair when the login button is clicked.
    dialog.setResultConverter(
        dialogButton -> {
        if (dialogButton == loginButtonType) {
          return new Pair<>(username.getText(), password.getText());
        }
        return null;
      });

    Optional<Pair<String, String>> result = dialog.showAndWait();

    result.ifPresent(
        usernamePassword -> logger.log(Level.INFO, "Username=" + usernamePassword.getKey()
        + ", Password=" + usernamePassword.getValue()));
  }


  /**
   * Show details of the selected contact item.
   *
   * @param selectedContact the contact object to display the details of
   */
  public void showDetails(ContactDetails selectedContact) {
    if (selectedContact == null) {
      showPleaseSelectItemDialog();
    } else {

      ContactDetailsDialog detailsDialog = new ContactDetailsDialog(selectedContact, false);

      detailsDialog.showAndWait();
    }
  }


  // -----------------------------------------------------------
  //    DIALOGS - minor dialogs lig confirm deletion, enter password etc.
  // -----------------------------------------------------------


  /**
   * Displays a warning informing the user that an item must be selected from
   * the table.
   */
  public void showPleaseSelectItemDialog() {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle("Information");
    alert.setHeaderText("No items selected");
    alert.setContentText("No item is selected from the table.\n"
        + "Please select an item from the table.");

    alert.showAndWait();
  }

  /**
   * Displays a delete confirmation dialog. If the user confirms the delete,
   * <code>true</code> is returned.
   *
   * @return <code>true</code> if the user confirms the delete
   */
  public boolean showDeleteConfirmationDialog() {
    boolean deleteConfirmed = false;

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Delete confirmation");
    alert.setHeaderText("Delete confirmation");
    alert.setContentText("Are you sure you want to delete this item?");

    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent()) {
      deleteConfirmed = (result.get() == ButtonType.OK);
    }
    return deleteConfirmed;
  }

}
