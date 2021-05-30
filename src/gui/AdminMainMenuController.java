package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.ArrayList;

public class AdminMainMenuController {

    private Controller cont = new Controller();
    private Popup pop = new Popup();
    private DatabaseConn db = new DatabaseConn();

    public void logout(ActionEvent event){
        StaticInfo.userId = "";
        cont.switchScene(event, "Login");
        return;
    }

    public void expandTrends(ActionEvent event){
        cont.switchScene(event, "AdminTrend");
        return;
    }

    public void expandQueue(ActionEvent event){
        cont.switchScene(event, "OrderQueue");
        return;
    }

    public void expandMenu(ActionEvent event){
        String btnName = event.getSource().toString().split(",")[0];
        btnName = btnName.split("=")[1];
        btnName = btnName.substring(0, btnName.length()-3);
        if(btnName.equals("extras")){ btnName = "misc"; }
        if(btnName.equals("beverage")){ btnName = "drink"; }

        StaticInfo.menuReference = btnName;
        cont.switchScene(event, "AdminExpandMenu");
        return;
    }
}