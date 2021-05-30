package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CustomerProductCustomizationController implements Initializable {

    private DatabaseConn db = new DatabaseConn();
    private Controller cont = new Controller();

    private ArrayList<String> ingredientNames = new ArrayList<String>();
    private ToggleGroup cheeseGroup, breadGroup;
    private ArrayList<CheckBox> checkBoxes;

    @FXML private GridPane customizationTable;
    @FXML private Label productName;
    @FXML private ImageView productImage;

    @Override
    public void initialize(URL url, ResourceBundle rb){

        // Setting the name of the customizer
        String[] cols = {"name", "product_type"};
        ArrayList<String> k = db.queryDatabase("SELECT * FROM products WHERE id = " + StaticInfo.productId, cols);
        String name = k.get(0).split("--")[0];
        productName.setText(name);

        // Sets the image dimensions
        //
        // I know that these are hard coded values, but for reasons that are still unclear, this is the only way
        // we can get this part working
        productImage.setPreserveRatio(false);
        productImage.setFitHeight(270);
        productImage.setFitWidth(250);

        /*
            This selects the image based on the name of the product
         */
        name = name.replace(" ", "").toLowerCase();
        try{
            productImage.setImage(new Image("imgs/products/"+name+".jpg"));
        }catch (Exception e){
            productImage.setImage(new Image("imgs/products/sauces.jpg"));
        }


        /*
            The code below gets all the ingredients of the product
            Then it figures out the types of ingredients used on the product
            With the exception of meat, these will become the columns in the customizable table
         */
        cols = new String[]{"ingredient_type", "name"};
        String query = "SELECT * FROM ingredients\n" +
                "INNER JOIN product_ingredient ON ingredients.id = product_ingredient.ingredient_id\n" +
                "WHERE product_ingredient.product_id = " + StaticInfo.productId;
        ArrayList<String> f = db.queryDatabase(query, cols);

        ingredientNames = new ArrayList<String>();
        for(String u : f){
            ingredientNames.add(u.split("--")[1]);
        }

        ArrayList<String> types = new ArrayList<>();
        for(String s : f){
            s = s.split("--")[0];
            Boolean sentinel = false;
            for(int i = 0; i < types.size(); i++){
                if(types.get(i).equals(s)){ sentinel = true; }
            }
            if(!sentinel){
                if(!s.equals("meat")) {
                    types.add(s);
                }
            }
        }

        int nCols = types.size();
        Color white = Color.color(1, 1, 1);
        if(nCols == 1){
            Label L = new Label("No Customization Options Available");
            L.setTextFill(white);
            customizationTable.add(L, 0, 0);
        } else {
            checkBoxes = new ArrayList<>();
            for(int i = 0; i < nCols; i++){
                String temp = types.get(i);
                temp = temp.substring(0, 1).toUpperCase() + temp.substring(1);


                // Create a bold white label and set it as the first item in the column
                Label header = new Label(temp);
                header.setTextFill(white);
                header.setStyle("-fx-font-weight: bold");
                customizationTable.addColumn(i, header);

                String[] c = {"name"};
                String q = "SELECT * FROM ingredients WHERE ingredient_type = \'" + types.get(i).toLowerCase() + "\'";
                ArrayList<String> ings = db.queryDatabase(q, c);

                if(types.get(i).equals("cheese")){
                    cheeseGroup = new ToggleGroup();
                    for(int j = 1; j < ings.size()+1; j++){
                        String enam = ings.get(j-1).split("--")[0];
                        RadioButton rbn = new RadioButton(enam);
                        rbn.setTextFill(white);
                        rbn.setToggleGroup(cheeseGroup);
                        customizationTable.add(rbn, i, j);
                        for(String eman : ingredientNames){
                            if(enam.equals(eman)){
                                rbn.setSelected(true);
                            }
                        }
                    }
                } else if(types.get(i).equals("bread")){
                    breadGroup = new ToggleGroup();
                    for(int j = 1; j < ings.size()+1; j++){
                        String enam = ings.get(j-1).split("--")[0];
                        RadioButton rbn = new RadioButton(enam);
                        rbn.setTextFill(white);
                        rbn.setToggleGroup(breadGroup);
                        customizationTable.add(rbn, i, j);
                        for(String eman : ingredientNames){
                            if(enam.equals(eman)){
                                rbn.setSelected(true);
                            }
                        }
                    }
                } else {
                    for(int j = 1; j < ings.size()+1; j++){
                        String enam = ings.get(j-1).split("--")[0];
                        CheckBox cbx = new CheckBox(enam);
                        cbx.setTextFill(white);
                        checkBoxes.add(cbx);
                        customizationTable.add(cbx, i, j);
                        for(String eman : ingredientNames){
                            if(enam.equals(eman)){
                                cbx.setSelected(true);
                            }
                        }
                    }
                }
            }
        }
    }

    public void addToCard(ActionEvent event){
        String modifications = "";

        // Checking to see if the cheese was changed from default (if changed at all)
        if(cheeseGroup != null){
            if(cheeseGroup.getSelectedToggle() != null) {
                Boolean sentinel = false;
                for (String name : ingredientNames) {
                    if(((RadioButton)cheeseGroup.getSelectedToggle()).getText().equals(name)){
                        sentinel = true;
                    }
                }

                if(!sentinel){
                    modifications += "CHEESE : " + ((RadioButton)cheeseGroup.getSelectedToggle()).getText() + "--";
                }
            }
        }

        // Checking to see if the bread was changed from default (if changed at all)
        if(breadGroup != null){
            if(breadGroup.getSelectedToggle() != null) {
                Boolean sentinel = false;
                for (String name : ingredientNames) {
                    if(((RadioButton)breadGroup.getSelectedToggle()).getText().equals(name)){
                        sentinel = true;
                    }
                }

                if(!sentinel){
                    modifications += "BREAD : " + ((RadioButton)breadGroup.getSelectedToggle()).getText() + "--";
                }
            }
        }

        // Checking every other checkbox to see if it was added
        if(checkBoxes != null) {
            for (CheckBox cb : checkBoxes) {
                if (cb.isSelected()) {
                    Boolean sentinel = false;
                    for (String name : ingredientNames) {
                        if (cb.getText().equals(name)) {
                            sentinel = true;
                        }
                    }

                    if (!sentinel) {
                        modifications += "ADD : " + cb.getText() + "--";
                    }
                }
            }

            // Checking every other checkbox to see if it was removed
            for (String name : ingredientNames) {
                for (CheckBox cb : checkBoxes) {
                    if (cb.getText().equals(name) && !cb.isSelected()) {
                        modifications += "REMOVE : " + cb.getText() + "--";
                    }
                }
            }
        }

        String k = StaticInfo.productId + "--" + productName.getText() + "--" + modifications;
        StaticInfo.productPairs.add(k);
        StaticInfo.productId = "";
        StaticInfo.menuReference = "";
        cont.switchScene(event, "CustomerCart");
    }

    public void goBack(ActionEvent event) {
        cont.switchScene(event, "CustomerMainMenu");
    }

    public void goToCart(ActionEvent event) {
        cont.switchScene(event, "CustomerCart");
    }

    public void performSignOut(ActionEvent event) {
        cont.switchScene(event, "Login");
    }

    private Boolean isIn(String sub, String word){
        word = word.toLowerCase();
        sub = sub.toLowerCase();
        for(int i = 0; i < word.length()-sub.length()+1; i++){
            if(word.substring(i, i+sub.length()).equals(sub)){
                return true;
            }
        }
        return false;
    }
}