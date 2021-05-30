package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
    private Parent root;
    private Stage stage;
    private Scene scene;

    // Function to switch scene from one fxml file to another
    public void switchScene(ActionEvent event, String filename){
        try {
            root = FXMLLoader.load(getClass().getResource(filename + ".fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) { e.printStackTrace(); }
    }
}
