package gui;

import javafx.event.ActionEvent;

public class CustomerMainMenuController {

    private Controller cont = new Controller();
    private Popup pop = new Popup();
    private DatabaseConn db = new DatabaseConn();

    public void logout(ActionEvent event){
        StaticInfo.userId = "";
        cont.switchScene(event, "Login");
        return;
    }

    public void seeCart(ActionEvent event){ cont.switchScene(event, "CustomerCart"); }

    /*
        This is the expandMenu function. The purpose is to act as a dynamic pane for all types of products.
        It will initialize in it's controller with the current menu list for the type clicked on. We need to find
        a way to utilize the button id that was clicked on to create this list.
     */
    public void expandMenu(ActionEvent event){
        String btnName = event.getSource().toString().split(",")[0];
        btnName = btnName.split("=")[1];
        btnName = btnName.substring(0, btnName.length()-3);
        if(btnName.equals("extras")){ btnName = "misc"; }
        if(btnName.equals("beverage")){ btnName = "drink"; }

        StaticInfo.menuReference = btnName;
        cont.switchScene(event, "CustomerExpandMenu");
        return;
    }
}