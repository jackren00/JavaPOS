package gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Popup {
    // Creates a popup that just notifies the user of something
    public void createPopup(String title, String description) {
        Alert albert = new Alert(Alert.AlertType.INFORMATION);
        albert.setTitle("Notification");
        albert.setHeaderText(title);
        albert.setContentText(description);
        albert.showAndWait();
    }

    // Popup for the admin to be able to change the price and availability of a product
    public String adminChangePopup(String title){
        final String[] retStr = {"CANCEL"};

        Stage window = new Stage();
        window.setTitle(title);
        window.setMinWidth(800);
        window.setMinHeight(450);

        Button cancelBtn = new Button("CANCEL");
        Button modifyBtn = new Button("MODIFY");
        Button unavailBtn = new Button("UNAVAILABLE");
        TextArea text = new TextArea();
        Label label = new Label("New Price:");

        cancelBtn.setOnAction(e -> {
            retStr[0] = "CANCEL";
            window.close();
        });

        unavailBtn.setOnAction(e -> {
            retStr[0] = "UNAVAILABLE";
            window.close();
        });

        modifyBtn.setOnAction(e -> {
           retStr[0] = text.getText();
           window.close();
        });

        // popup box styling
        text.setPrefHeight(300);
        HBox buttonRow = new HBox();
        buttonRow.getChildren().addAll(cancelBtn, modifyBtn, unavailBtn);
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.setSpacing(100);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, text, buttonRow);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return retStr[0];
    }
}
