package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CustomerCartController implements Initializable {

    private DatabaseConn db = new DatabaseConn();
    private Controller cont = new Controller();
    private Popup pop = new Popup();

    @FXML private TableView<Product> orderedItems;
    @FXML private TableColumn<Product, Button> productButton;
    @FXML private TableColumn<Product, Button> productMods;
    @FXML private TableColumn<Product,String> productName;
    @FXML private TableColumn<Product, String> productPrice;
    @FXML private CheckBox favoriteCheck;

    @FXML private Label subtotal, discount, taxes, total;
    private int totalval;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        orderedItems.setPlaceholder(new Label("There are no items in your cart!"));
        subtotal.setText("$0.00");
        discount.setText("$0.00");
        taxes.setText("$0.00");
        total.setText("$0.00");
        // observable list for product info

        // set up columns in table
        productName.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        productPrice.setCellValueFactory(new PropertyValueFactory<Product, String>("price"));
        productButton.setCellValueFactory(new PropertyValueFactory<Product, Button>("button"));
        productMods.setCellValueFactory(new PropertyValueFactory<Product, Button>("modify"));

        // load cart data
        if (StaticInfo.productPairs.size() > 0) {
            orderedItems.setItems(getProduct());
        }
    }

    public ObservableList<Product> getProduct() {
        ObservableList<Product> cart = FXCollections.observableArrayList();

        // Creating button array for each product
        Button[] buttons = new Button[StaticInfo.productPairs.size()];
        for(int i = 0; i < buttons.length; i++) {
            int index = i;
            buttons[i] = new Button("X");
            buttons[i].setOnAction(e -> {
                // remove product
                StaticInfo.productPairs.remove(index);
                // update cart
                if (StaticInfo.productPairs.size() > 0) {
                    orderedItems.setItems(getProduct());
                } else {
                    orderedItems.getItems().clear();
                    subtotal.setText("$0.00");
                    discount.setText("$0.00");
                    taxes.setText("$0.00");
                    total.setText("$0.00");
                }
            });
        }

        // Modify Buttons
        Button[] modBtns = new Button[StaticInfo.productPairs.size()];
        for(int i = 0; i < modBtns.length; i++) {
            int index = i;
            modBtns[i] = new Button("Modify");
            int prodNum = i;
            modBtns[i].setOnAction(e -> {
                // Go To Product Modification
                StaticInfo.productId = StaticInfo.productPairs.get(prodNum).split("--")[0];
                // remove product
                StaticInfo.productPairs.remove(index);
                // update cart
                cont.switchScene(e, "CustomerProductCustomization");
            });
        }

        // Adding the buttons to the Product items
        for(int i = 0; i < StaticInfo.productPairs.size(); i++) {
            String[] prods = StaticInfo.productPairs.get(i).split("--");
            cart.add(new Product(Integer.parseInt(prods[0]), i, buttons[i], modBtns[i]));
        }

        // Calculating costs
        int subtot = 0;
        int disc = 0;
        int tax = 0;
        int tot = 0;

        // Setting the subtotal
        for(int j = 0; j < cart.size(); j++){
            String temp = cart.get(j).getPrice().substring(1);
            temp = temp.replace(".", "");
            subtot += Integer.parseInt(temp);
        }
        String sSubtot = Integer.toString(subtot);
        subtotal.setText("$" + sSubtot.substring(0,sSubtot.length() - 2) + "." + sSubtot.substring(sSubtot.length() - 2));

        // For now, 0 discounts
        discount.setText("$0.00");

        // Texas State Sales Tax is 6.25
        tax = (int)((double)subtot * 0.0625);
        String temp = Integer.toString(tax);
        if(temp.length() < 3){
            taxes.setText("$0."+temp);
        } else {
            taxes.setText("$" + temp.substring(0,temp.length() - 2) + "." + temp.substring(temp.length() - 2));
        }


        // Final total
        tot = subtot + tax;
        totalval = tot;
        temp = Integer.toString(tot);
        if(temp.length() < 3){
            total.setText("$0."+temp);
        } else {
            total.setText("$" + temp.substring(0,temp.length() - 2) + "." + temp.substring(temp.length() - 2));
        }

        return cart;
    }

    public void goToMainMenu(ActionEvent event) {
        cont.switchScene(event, "CustomerMainMenu");
        return;
    }

    public void goBack(ActionEvent event) {
        cont.switchScene(event, "CustomerMainMenu");
        return;
    }

    public void performPayNow(ActionEvent event) {
        // checking that there are items in the cart
        if(StaticInfo.productPairs.size() < 1) {
            pop.createPopup("No items in cart", "You have not added anything to your cart");
            return;
        }

        String productId;
        String part = "";

        // inserting a new order into the database, saving its id
        String[] oCols = {"id"};
        String date = java.time.LocalDate.now().toString();
        String total = String.valueOf(totalval);

        ArrayList<String> order = db.queryDatabase("INSERT INTO orders (price, date) VALUES (" + total + ", \'" + date + "\') RETURNING id", oCols);
        StaticInfo.orderId = order.get(0).split("--")[0];

        // formatting string for database query
        for(int i = 0; i < StaticInfo.productPairs.size(); i++) {
            productId = StaticInfo.productPairs.get(i).split("--")[0];

            String pms = "";
            int size = StaticInfo.productPairs.get(i).split("--").length;
            String[] tempArr = StaticInfo.productPairs.get(i).split("--");
            for(int j = 2; j < size; j++){
                if(j == size-1){
                    pms += "\"" + tempArr[j] + "\"";
                }else{
                    pms += "\"" + tempArr[j] + "\", ";
                }
            }
            
            // split each product in cart and append it to part
            part += "(" + StaticInfo.orderId + ", " + productId + ", " + "\'{"+ pms +"}\')";

            if(i != StaticInfo.productPairs.size() - 1) {
                part = part + ", ";
            }
        }

        // inserting customer_order entry
        String fav = "FALSE";
        if(favoriteCheck.isSelected()){ fav = "TRUE"; }
        String query = "INSERT INTO customer_order (customer_id, order_id, favorite) VALUES (" + StaticInfo.userId + ", " + StaticInfo.orderId + ", " + fav + ")";
        db.queryDatabase(query, null);

        // adding order_products to database
        query = "INSERT INTO order_product (order_id, product_id, product_mods) VALUES " + part;
        db.queryDatabase(query, null);

        // clearing orders from cart
        StaticInfo.productPairs.clear();

        // adding a new order and getting its id, then
        pop.createPopup("Order placed", "Thank you for your business!");
        cont.switchScene(event, "OrderStatus");
        return;
    }

    public void performSignOut(ActionEvent event) {
        StaticInfo.productPairs = new ArrayList<String>();
        StaticInfo.userId = "";
        cont.switchScene(event, "Login");
        return;
    }

    public void goToCart(ActionEvent event) {
        cont.switchScene(event, "CustomerCart");
        return;
    }


}


