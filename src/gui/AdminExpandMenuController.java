package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TreeMap;

import static java.lang.Integer.parseInt;

public class AdminExpandMenuController implements Initializable {
    private DatabaseConn db = new DatabaseConn();
    private Popup pop = new Popup();
    private Controller cont = new Controller();

    @FXML private TableView<Product> menuItems;
    @FXML private TableColumn<Product, String> productName;
    @FXML private TableColumn<Product, String> productPrice;
    @FXML private TableColumn<Product, Button> productButton;

    @FXML private Text itemName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String k = StaticInfo.menuReference.toLowerCase();
        k = k.substring(0, 1).toUpperCase() + k.substring(1);
        itemName.setText(k);

        // set up columns in table
        productName.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        productPrice.setCellValueFactory(new PropertyValueFactory<Product, String>("price"));
        productButton.setCellValueFactory(new PropertyValueFactory<Product, Button>("button"));

        // load cart data
        menuItems.setItems(getProducts());
    }

    public ObservableList<Product> getProducts() {
        ObservableList<Product> menu = FXCollections.observableArrayList();
        ArrayList<String> data = new ArrayList<String>();
        if (StaticInfo.menuReference.equals("specials")) {

            // query database by product_type for product name, price, and id
            String[] cols = {"name", "id", "price"};
            String query = "SELECT * FROM specials";
            data = db.queryDatabase(query, cols);

        } else {

            // query database by product_type for product name, price, and id
            String[] cols = {"name", "id", "price"};
            String query = "SELECT * FROM products WHERE product_type = \'" + StaticInfo.menuReference + "\'";
            data = db.queryDatabase(query, cols);
        }

        // Creating button for each product
        Button[] buttons = new Button[data.size()];

        // each button adds the given product to the menu
        for (int i = 0; i < data.size(); i++) {
            // splitting data
            String[] splitData = data.get(i).split("--");

            // creating a button for each item on the cart
            int index = i;
            buttons[i] = new Button("Modify");
            buttons[i].setOnAction(e -> {
                if (StaticInfo.menuReference.equals("specials")) {

                } else {
                    StaticInfo.productId = splitData[1];
                    String k = ((Button)e.getSource()).getText();
                    String newVal = pop.adminChangePopup("Modify " + k.toUpperCase(Locale.ROOT) + "?");
                    if(!newVal.equals("CANCEL")){
                        if(newVal.equals("UNAVAILABLE")){
                            db.queryDatabase("UPDATE products SET price = -1 WHERE name = \'" + splitData[0] + "\'", null);
                            return;
                        }
                        try {
                            if (parseInt(newVal) >= 0) {
                                db.queryDatabase("UPDATE products SET price = " + newVal + " WHERE name = \'" + splitData[0] + "\'", null);
                                return;
                            }
                        } catch(Exception ex){
                            System.err.println(ex.toString());
                        }
                    }
                }
            });

            // Price formatting & "Unavailable Checking"
            String c = splitData[2];
            if (!c.equals("-1")) {

                c = "$" + c.substring(0, c.length() - 2) + "." + c.substring(c.length() - 2);

                // add to product to menu
                menu.add(new Product(splitData[0], c, Integer.parseInt(splitData[1]), i, buttons[i]));
            }
        }
        return menu;
    }

    public void goBack(ActionEvent event) {
        cont.switchScene(event, "AdminMainMenu");
        return;
    }

    public void performSignOut(ActionEvent event) {
        cont.switchScene(event, "Login");
        return;
    }
}
