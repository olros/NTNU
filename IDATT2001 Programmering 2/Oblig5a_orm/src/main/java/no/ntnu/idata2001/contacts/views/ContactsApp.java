package no.ntnu.idata2001.contacts.views;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import no.ntnu.idata2001.contacts.controllers.MainController;
import no.ntnu.idata2001.contacts.model.AddressBook;
import no.ntnu.idata2001.contacts.model.ContactDetails;

/**
 * The main window/application of the contacts app.
 */
public class ContactsApp extends Application {

  private static final String VERSION = "0.3.1";

  private MainController mainController;
  private AddressBook addressBook;

  // The JavaFX ObservableListWrapper used to connect tot he underlying AddressBook
  private ObservableList<ContactDetails> addressBookListWrapper;

  // Need to keep track of the TableView-instance since we need to access it
  // from different places in our GUI (menu, doubleclicking, toolbar etc.)
  private TableView<ContactDetails> contactDetailsTableView;

  /**
   * The main starting point of the application.
   *
   * @param args command line arguments provided during startup. Not used in this app.
   */
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void init() throws Exception {
    super.init();

    // Initialise the main controller
    this.mainController = new MainController();

    // Initialise the Address Book from a file
    this.addressBook = this.mainController.loadAddressBookFromDatabase();
  }

  @Override
  public void start(Stage primaryStage) {
//    try {
      // Build the GUI of the main window
      BorderPane root = new BorderPane(); // Create the root node. The Menu will be placed at the top
      VBox topContainer = new VBox();  //Creates a container to hold all Menu Objects.
      MenuBar mainMenu = createMenus();  //Creates our main menu to hold our Sub-Menus.
      ToolBar toolBar = createToolBar();  // Creates a toolbar below the menubar

      // Place the menubar in the topContainer
      topContainer.getChildren().add(mainMenu);
      // Place the Toolbar
      topContainer.getChildren().add(toolBar);

      // Place the top container in the top-section of the BorderPane
      root.setTop(topContainer);
      // Place the StatusBar at the bottom
      root.setBottom(createStatusBar());
      // Place the table view in the centre
      this.contactDetailsTableView = this.createCentreContent();
      root.setCenter(this.contactDetailsTableView);

      // Create the scene, adding the rootNode and setting the default size
      Scene scene = new Scene(root, 600, 500);

      // Set title of the stage (window) and add the scene
      primaryStage.setTitle("Contacts v" + VERSION);
      primaryStage.setScene(scene);

      // Finally, make the stage (window) visible
      primaryStage.show();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
  }

  /**
   * The stop() method is being called by the JavaFX-platform when the
   * platform stops, are being terminated. This would typically happen as a
   * result of the last open window being closed. Override this method to make
   * sure that the application is terminated.
   */
  @Override
  public void stop() {
    // Save the address book to file
//    this.mainController.saveAddressBookToFile(this.addressBook);
    // Exit the application
    System.exit(0);
  }

  /**
   * Build the content of the centre of the main window. This part of the GUI displays
   * a Table of all the contacts in the address book as a table.
   *
   * @return The node to be placed in the centre of the main window.
   */
  private TableView<ContactDetails> createCentreContent() {

    // Create the Table to display all the literature in

    // Define the columns
    // The Name-column
    TableColumn<ContactDetails, String> nameColumn = new TableColumn<>("Name");
    nameColumn.setMinWidth(200);
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

    // The Address-column
    TableColumn<ContactDetails, String> addressColumn = new TableColumn<>("Address");
    addressColumn.setMinWidth(200);
    addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

    // The Phone-column
    TableColumn<ContactDetails, String> phoneColumn = new TableColumn<>("Phone");
    phoneColumn.setMinWidth(200);
    phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

    // Create the TableView instance
    TableView<ContactDetails> tableView = new TableView<>();
    tableView.setItems(this.getAddressBookListWrapper());
    tableView.getColumns().addAll(nameColumn, addressColumn, phoneColumn);

    // Add listener for double click on row
    tableView.setOnMousePressed(mouseEvent -> {
      if (mouseEvent.isPrimaryButtonDown() && (mouseEvent.getClickCount() == 2)) {
        ContactDetails selectedContact = tableView.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
          mainController.showDetails(selectedContact);
        }
      }
    });

    return tableView;
  }


  /**
   * Updates the ObservableArray wrapper with the current content in the
   * Literature register. Call this method whenever changes are made to the
   * underlying LiteratureRegister.
   */
  public void updateObservableList() {
    this.addressBookListWrapper.setAll(this.addressBook.getAllContacts());
  }

  /**
   * Returns an ObservableList holding the contacts to display.
   *
   * @return an ObservableList holding the contacts to display.
   */
  private ObservableList<ContactDetails> getAddressBookListWrapper() {
    // Create an ObservableArrayList wrapping the LiteratureRegister
    addressBookListWrapper
      = FXCollections.observableArrayList(this.addressBook.getAllContacts());
    return addressBookListWrapper;
  }


  /**
   * Create a statusbar to be added at the bottom of the GUI.
   *
   * @return a status bar
   */
  private Node createStatusBar() {
    HBox statusBar = new HBox();
    statusBar.setStyle("-fx-background-color: #999999;");
    statusBar.getChildren().add(new Text("Status: OK"));

    return statusBar;
  }

  /**
   * Build the toolbar of the main window. The toolbar holds a series of buttons
   * to perform the actions of the application, like adding, editing and deleting contacts.
   *
   * @return The node holding the toolbar, to be placed at the top of the main window.
   */
  private ToolBar createToolBar() {

    //Create some Buttons.
    // Create the add new contact buton
    Button addContactBtn = new Button("Add contact");

    //Set the icon/graphic for the ToolBar Buttons.
    addContactBtn.setGraphic(
        new ImageView(
          new Image("file:src/main/java/no.ntnu.idata2001.contacts/views/icons/add_contact@2x.png")));

    addContactBtn.setOnAction(actionEvent -> mainController.addContact(this.addressBook, this));

    // Add the edit contact-button in the toolbar
    Button editContactBtn = new Button("Edit contact");
    editContactBtn.setGraphic(new ImageView(
        new Image("file:src/main/java/no.ntnu.idata2001.contacts/views/icons/edit_contact@2x.png")));

    editContactBtn.setOnAction(event ->
        mainController.editContact(
          this.contactDetailsTableView.getSelectionModel().getSelectedItem(), this));

    // Add the delete contact-button in the tool bar
    Button deleteContactBtn = new Button("Delete contact");
    deleteContactBtn.setGraphic(new ImageView(
        new Image("file:src/main/java/no.ntnu.idata2001.contacts/views/icons/remove_contact@2x.png")));

    deleteContactBtn.setOnAction(event ->
        mainController.deleteContact(
          this.contactDetailsTableView.getSelectionModel().getSelectedItem(),
              this.addressBook, this));


    //Add the Buttons to the ToolBar.
    ToolBar toolBar = new ToolBar();
    toolBar.getItems().addAll(addContactBtn, deleteContactBtn, editContactBtn);
    return toolBar;
  }

  /**
   * Creates the menu bar to be placed above the toolbar.
   *
   * @return a menubar
   */
  private MenuBar createMenus() {

    // ----- The File-menu ------
    Menu menuFile = new Menu("File");

    MenuItem importFromCsv = new MenuItem("Import from .CSV...");
    importFromCsv.setOnAction(event -> mainController.importFromCsv(this.addressBook, this));
    menuFile.getItems().add(importFromCsv);

    MenuItem exportToCsv = new MenuItem("Export to .CSV...");
    exportToCsv.setOnAction(event -> mainController.exportToCsv(this.addressBook));
    menuFile.getItems().add(exportToCsv);

    // Add a separator line before Exit
    menuFile.getItems().add(new SeparatorMenuItem());

    MenuItem exitApp = new MenuItem("Exit");
    exitApp.setOnAction(event -> mainController.exitApplication());
    menuFile.getItems().add(exitApp);

    // ----- The Help-menu ------
    Menu menuHelp = new Menu("Help");
    MenuItem about = new MenuItem("About");
    about.setOnAction(event -> mainController.showAboutDialog(VERSION));
    menuHelp.getItems().add(about);

    // ----- The LogIn-menu ------
    Menu menuAdmin = new Menu("Admin");
    MenuItem loginMenu = new MenuItem("Log In");
    loginMenu.setOnAction(event -> mainController.showLogInDialog());
    menuAdmin.getItems().add(loginMenu);

    // ----- The Edit-menu ------


    // Add contact
    Image addContactIcon = new Image("file:src/main/java/no.ntnu.idata2001.contacts/views/icons/add_contact@2x.png");
    ImageView addContactView = new ImageView(addContactIcon);
    addContactView.setFitWidth(15);
    addContactView.setFitHeight(15);
    MenuItem addContactMenu = new MenuItem("Add new Contact ...");
    addContactMenu.setGraphic(addContactView);
    addContactMenu.setAccelerator(new KeyCodeCombination(KeyCode.A));

    addContactMenu.setOnAction(event -> mainController.addContact(this.addressBook, this));

    Menu menuEdit = new Menu("Edit");
    menuEdit.getItems().add(addContactMenu);

    // Edit contact
    Image editContactIcon =
        new Image("file:src/main/java/no.ntnu.idata2001.contacts/views/icons/edit_contact@2x.png");
    ImageView editContactView = new ImageView(editContactIcon);
    editContactView.setFitWidth(15);
    editContactView.setFitHeight(15);
    MenuItem editContactMenu = new MenuItem("Edit Selected Contact");
    editContactMenu.setGraphic(editContactView);
    editContactMenu.setAccelerator(new KeyCodeCombination(KeyCode.E));

    editContactMenu.setOnAction(event ->
        mainController.editContact(
          this.contactDetailsTableView.getSelectionModel().getSelectedItem(), this));

    menuEdit.getItems().add(editContactMenu);

    // Remove contact
    Image removeContactIcon =
        new Image("file:src/main/java/no.ntnu.idata2001.contacts/views/icons/remove_contact@2x.png");
    ImageView removeContactView = new ImageView(removeContactIcon);
    removeContactView.setFitWidth(15);
    removeContactView.setFitHeight(15);
    MenuItem removeContactMenu = new MenuItem("Remove Selected Contact");
    removeContactMenu.setGraphic(removeContactView);
    removeContactMenu.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));

    removeContactMenu.setOnAction(event -> mainController.deleteContact(
        this.contactDetailsTableView.getSelectionModel()
          .getSelectedItem(), this.addressBook, this));

    menuEdit.getItems().add(removeContactMenu);

    // Create the Menu Bar to hold all the menus
    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(menuFile, menuEdit, menuHelp, menuAdmin);

    return menuBar;
  }

}
