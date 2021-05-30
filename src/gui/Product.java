package gui;

import java.util.ArrayList;
import javafx.scene.control.Button;

public class Product {

    private String name;
    private String price;
    private int id;
    private int index;
    private Button button;
    private Button modify;
    private DatabaseConn db = new DatabaseConn();

    Product(int i, int in, Button butt, Button mod) {
        // Query database for name and price of product
        String[] cols = {"name", "price"};
        String query = "SELECT * FROM products WHERE id = " + Integer.toString(i);
        ArrayList<String> data = db.queryDatabase(query, cols);

        // Set name and price to be the name and price from the database
        String[] prod = data.get(0).split("--");
        name = prod[0];
        if (prod[1].length() > 1) {
            price = "$" + prod[1].substring(0,prod[1].length() - 2) + "." + prod[1].substring(prod[1].length() - 2);
        }

        // Set button
        button = butt;
        modify = mod;
        index = in;

        // Set id
        id = i;
    }

    Product(String n, String p, int i, int in, Button b) {
        name = n;
        price = p;
        id = i;
        index = in;
        button = b;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public Button getButton() {
        return button;
    }

    public Button getModify() {
        return modify;
    }

    public int getId() {
        return id;
    }
}
