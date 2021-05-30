package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;

public class CustomerExpandMenuController implements Initializable {

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

        } else if (StaticInfo.menuReference.equals("favorites")) {

            // query database by product_type for product name, price, and id
            String[] cols = {"id", "price"};
            String query = "SELECT orders.id, orders.price FROM orders "+
                    "INNER JOIN customer_order ON customer_order.order_id = orders.id "+
                    "WHERE customer_order.favorite = TRUE AND customer_order.customer_id = " + StaticInfo.userId;
            data = db.queryDatabase(query, cols);

            for(int idx = 0; idx < data.size(); idx++){
                String line = data.get(idx);
                cols = new String[]{"name"};
                query = "SELECT products.name FROM products "+
                        "INNER JOIN order_product ON order_product.product_id = products.id "+
                        "WHERE order_product.order_id = " + line.split("--")[0];
                ArrayList<String> newData = db.queryDatabase(query, cols);

                // Combine names
                String fullName = "";
                for(String name : newData){
                    fullName += name.split("--")[0]+"\n";
                }
                // Replace old data array element
                data.set(idx, fullName + "--" + data.get(idx));
            }

        } else if (StaticInfo.menuReference.equals("recommended")) {

            // query database by product_type for product name, price, and id
            String[] cols = {"product_id"};
            String query = "SELECT product_id FROM order_product ORDER BY order_id DESC LIMIT 1000";
            data = db.queryDatabase(query, cols);

            // List of product ids and their counts
            ArrayList<int[]> counts = new ArrayList<int[]>();
            for (String k : data) {
                int u = parseInt(k.split("--")[0]);
                Boolean sentinel = false;
                for (int i = 0; i < counts.size(); i++) {
                    if (counts.get(i)[0] == u) {
                        counts.get(i)[1] += 1;
                        sentinel = true;
                        break;
                    }
                }
                if (!sentinel) {
                    int[] x = {u, 1};
                    counts.add(x);
                }
            }

            // Sort the top 10 most frequent
            for (int i = 0; i < counts.size(); i++) {
                for (int j = 1; j < (counts.size() - i); j++) {
                    if (counts.get(j - 1)[1] > counts.get(j)[1]) {
                        //swap elements
                        int[] temp = counts.get(j - 1);
                        counts.set(j - 1, counts.get(j));
                        counts.set(j, temp);
                    }
                }
            }

            // Put the ids into data
            data.clear();
            for (int i = counts.size() - 1; i >= 0; i--) {
                data.add(String.valueOf(counts.get(i)[0]));
            }

            cols = new String[]{"name", "id", "price", "product_type"};
            ArrayList<String> newList = new ArrayList<>();

            for (String S : data) {
                query = "SELECT * FROM products WHERE id = " + S;
                String temp = db.queryDatabase(query, cols).get(0);
                if (!temp.split("--")[3].equals("misc")) {
                    newList.add(temp);
                }
            }
            data = newList;
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
            buttons[i] = new Button("Add To Order");
            buttons[i].setOnAction(e -> {
                if (StaticInfo.menuReference.equals("specials")) {
                    // Gets all products associated with a special
                    String[] cols = {"name", "id", "price"};
                    String query = "SELECT products.name, products.id, products.price FROM products "+
                            "INNER JOIN special_product ON special_product.product_id = products.id "+
                            "WHERE special_product.special_id = " + splitData[1];
                    ArrayList<String> specs = db.queryDatabase(query, cols);

                    // Adds the items to the cart
                    for(String product : specs){
                        String[] prod = product.split("--");
                        String k = prod[1] + "--" + prod[0] + "--\'{}\'";
                        StaticInfo.productPairs.add(k);
                    }
                    // Goes to the cart
                    StaticInfo.productId = "";
                    StaticInfo.menuReference = "";
                    cont.switchScene(e, "CustomerCart");
                    return;

                } else if (StaticInfo.menuReference.equals("favorites")) {

                    String[] cols = {"name", "id", "price", "product_mods"};
                    String query = "SELECT products.name, products.id, products.price, order_product.product_mods FROM products "+
                            "INNER JOIN order_product ON order_product.product_id = products.id "+
                            "WHERE order_product.order_id = " + splitData[1];
                    ArrayList<String> favs = db.queryDatabase(query, cols);

                    // Add items to the cart along with their modifications
                    for(String product : favs){
                        String[] prod = product.split("--");
                        String k = prod[1] + "--" + prod[0] + "--\'" + prod[3] + "\'";
                        StaticInfo.productPairs.add(k);
                    }

                    // Goes to the cart
                    StaticInfo.productId = "";
                    StaticInfo.menuReference = "";
                    cont.switchScene(e, "CustomerCart");
                    return;
                }
                else {
                    StaticInfo.productId = splitData[1];
                    cont.switchScene(e, "CustomerProductCustomization");
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
        cont.switchScene(event, "CustomerMainMenu");
    }

    public void goToCart(ActionEvent event) {
        cont.switchScene(event, "CustomerCart");
    }

    public void performSignOut(ActionEvent event) {
        cont.switchScene(event, "Login");
    }
}