package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class LoginController {

    private DatabaseConn db = new DatabaseConn();
    private Hashing hashing = new Hashing();
    private Controller cont = new Controller();
    private Popup pop = new Popup();

    @FXML
    private TextField password, emailAddress;

    // Checks login credentials
    public void checkLogin(ActionEvent event){
        // Queries the database to get the password of the given email if it exists as an employee
        String[] cols = {"password", "admin"};
        ArrayList<String> results = db.queryDatabase("SELECT * FROM employees WHERE email = \'" + emailAddress.getText() + "\'", cols);

        // If it does exist
        if(results.size() != 0){
            String[] tempArr = results.get(0).split("--");
            String temporary = tempArr[0];
            Boolean passIsCorrect = hashing.comparePassword(password.getText(), temporary);

            // If the passwords dont match
            if(!passIsCorrect){
                pop.createPopup("Invalid Password", "You entered an incorrect password");
                return;
            }

            // If they are Admin
            if(tempArr[1].equals("t")){
                cont.switchScene(event, "AdminMainMenu");
            } else {
                cont.switchScene(event, "OrderQueue");
            }

            return;
        }

        // Otherwise query the customer table
        cols = new String[]{"password", "id"};
        results = db.queryDatabase("SELECT * FROM customers WHERE email = \'" + emailAddress.getText() + "\'", cols);

        // If they don't exist
        if(results.size() == 0){
            pop.createPopup("Invalid Password", "You entered an incorrect email address or password");
            return;
        }

        // If the passwords don't match
        String temporary = results.get(0).split("--")[0];
        Boolean passIsCorrect = hashing.comparePassword(password.getText(), temporary);
        if(!passIsCorrect){
            pop.createPopup("Invalid Password", "You entered an incorrect password");
            return;
        }

        // Otherwise everything is good
        StaticInfo.userId = results.get(0).split("--")[1];
        cont.switchScene(event, "CustomerMainMenu");
        return;
    }

    // GOTO the signup page
    public void gotoSignup(ActionEvent event){
        cont.switchScene(event, "Register");
        return;
    }
}
