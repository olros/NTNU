package Gui;

import Logger.MyLogger;
import MemberArchive.MemberArchive;
import MemberArchive.Personals;
import MemberArchive.BonusMember;
import MemberArchive.SilverMember;
import MemberArchive.GoldMember;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;

public class Main extends Application {

    private MemberArchive memberArchive = new MemberArchive();
    TableView tableView;
    private MyLogger myLogger = new MyLogger();

    public Main() throws IOException {
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Personals ole = new Personals("Ole", "Olsen", "ole.olsen@dot.com", "ole");
            Personals tove = new Personals("Tove", "Hansen", "tove.hansen@dot.com", "tove");
            Personals arne = new Personals("Arne", "Arnesen", "arne.arnesen@dot.com", "arne");
            memberArchive.newMember(ole, LocalDate.of(2018, 2, 15));
            int n = memberArchive.newMember(tove, LocalDate.of(2019, 4, 11));
            int no = memberArchive.newMember(arne, LocalDate.of(2020, 1, 15));

            memberArchive.registerPoints(no, 80000);
            memberArchive.registerPoints(n, 30000);


        } catch (Exception e) {
            myLogger.getLogger().log(Level.FINE,e.getMessage());
        }

        primaryStage.setTitle("Memberarchive");
        primaryStage.setScene(mainScreen());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Scene mainScreen() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tabAllMembers = new Tab("All members", getAllMembers());
        Tab tabAdd = new Tab("Add new member"  , addMember());

        tabPane.getTabs().add(tabAllMembers);
        tabPane.getTabs().add(tabAdd);

        return new Scene(tabPane, 540, 400);
    }

    public VBox getAllMembers() {
        tableView = new TableView();

        TableColumn<String, BonusMember> column1 = new TableColumn<>("Member number");
        column1.setCellValueFactory(new PropertyValueFactory<>("MemberNo"));
        TableColumn<String, BonusMember> column2 = new TableColumn<>("Name");
        column2.setCellValueFactory(new PropertyValueFactory<>("Personals"));
        TableColumn<String, BonusMember> column3 = new TableColumn<>("Enrolled");
        column3.setCellValueFactory(new PropertyValueFactory<>("EnrolledDate"));
        TableColumn<String, BonusMember> column4 = new TableColumn<>("Points");
        column4.setCellValueFactory(new PropertyValueFactory<>("Points"));


        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        tableView.getColumns().add(column3);
        tableView.getColumns().add(column4);

        tableView.getItems().addAll(memberArchive.getMemberList());

        Button button1 = new Button("Delete member");
        button1.setOnAction(v -> {
            try {
                memberArchive.getMemberList().remove(tableView.getSelectionModel().getSelectedItem());
                updateMembers();
            } catch (Exception e) {
                myLogger.getLogger().log(Level.FINE,e.getMessage());
            }
        });
        Button button2 = new Button("Check and upgrade members");
        button2.setOnAction(v -> {
            memberArchive.checkMembers(LocalDate.of(2020, 2, 10));
            updateMembers();
        });
        Button button3 = new Button("View member details");
        button3.setOnAction(v -> {
            try {
                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.WINDOW_MODAL);

                Text aboutMember = new Text(tableView.getSelectionModel().getSelectedItem().toString());
                Text type = new Text("Basic member");
                if (tableView.getSelectionModel().getSelectedItem() instanceof SilverMember) {
                    type.setText("Silver member");
                } else if (tableView.getSelectionModel().getSelectedItem() instanceof GoldMember) {
                    type.setText("Gold member");
                }

                VBox vbox = new VBox(aboutMember, type);
                vbox.setAlignment(Pos.CENTER);
                vbox.setPadding(new Insets(15));

                dialogStage.setScene(new Scene(vbox));
                dialogStage.show();
            } catch (Exception e) {
                myLogger.getLogger().log(Level.FINE,e.getMessage());
            }
        });
        HBox hBox = new HBox(20, button1, button2, button3);
        hBox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(0, tableView, hBox);

        return vbox;
    }

    public VBox addMember() {

        Label firstNameLabel = new Label("First name");
        TextField firstNameField = new TextField();
        Label lastNameLabel = new Label("Last name");
        TextField lastNameField = new TextField();
        Label emailLabel = new Label("Email");
        TextField emailField = new TextField();
        Label passwordLabel = new Label("Password");
        PasswordField  passwordField = new PasswordField ();

        Button submit = new Button("Submit");
        submit.setOnAction(action -> {
            try {
                Personals personals = new Personals(firstNameField.getText(), lastNameField.getText(), emailField.getText(), passwordField.getText());
                memberArchive.newMember(personals, LocalDate.now());
                firstNameField.setText("");
                lastNameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                updateMembers();
            } catch (Exception e) {
                myLogger.getLogger().log(Level.FINE,e.getMessage());
            }
        });

        VBox vbox = new VBox(10,firstNameLabel, firstNameField, lastNameLabel, lastNameField, emailLabel, emailField, passwordLabel, passwordField, submit);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    public void updateMembers() {
        tableView.getItems().clear();
        tableView.getItems().addAll(memberArchive.getMemberList());
    }
}
