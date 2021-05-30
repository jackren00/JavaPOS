package gui;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class RegisterController {

    private DatabaseConn db = new DatabaseConn();
    private Hashing hashing = new Hashing();
    private Controller cont = new Controller();
    private Popup pop = new Popup();

    @FXML
    private TextField firstAndLastName, email, password, confirmPassword;

    public void addUser(ActionEvent event){
        // If the passwords entered do not match
        if(!password.getText().equals(confirmPassword.getText())){
            pop.createPopup("Invalid Password", "The passwords you entered did not match");
            return;
        }

        // If the email is not valid
        if(!db.isValidEmail(email.getText())){
            pop.createPopup("Invalid Email", "The email address you entered was invalid");
            return;
        }

        // Otherwise double check the user doesn't already exist
        String[] cols = {"name"};
        ArrayList<String> users = db.queryDatabase("SELECT * FROM customers WHERE email = \'" + email.getText() + "\'", cols);

        // If the user already exists
        if(users.size() != 0){
            pop.createPopup("User Already Exists", "Sorry, but a user with that email address already exists");
            return;
        }

        // If the password is weak
        // I.E. less than 10 characters
        String passText = password.getText();
        if(passText.length() < 10) {
            pop.createPopup("Weak Password", "You entered an weak password\nThe password must have at least 10 characters");
            return;
        }

        // Hash the password using SHA-256
        String passwd = hashing.createPassword(passText);

        // Add the user to the database
        String part = "(\'" + firstAndLastName.getText() + "\', \'" + email.getText() + "\', \'" + passwd + "\');";
        db.queryDatabase("INSERT INTO customers (name, email, password) VALUES\n" + part, null);

        // GO BACK TO LOGIN
        cont.switchScene(event, "Login");
        return;
    }
}