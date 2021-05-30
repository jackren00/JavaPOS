package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import views.QueueItem;
import views.QueueItemController;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class OrderQueueController implements Initializable {
    private DatabaseConn db = new DatabaseConn();
    private Controller cont = new Controller();

    @FXML private Button customerMainMenuSignOut;
    @FXML private ScrollPane scroll;
    @FXML private GridPane grid;

    private ArrayList<QueueItem> queueItems = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int col = 0;
        int row = 1;

        getOrders();

        try {
            for(QueueItem qI:queueItems) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("../views/QueueItem.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                QueueItemController queueItemController = fxmlLoader.getController();
                queueItemController.setData(qI);

                if(col == 2) {
                    col = 0;
                    row++;
                }

                grid.add(anchorPane, col++, row);

                // grid dimensions
                grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                grid.setMaxHeight(Region.USE_PREF_SIZE);

                grid.setMinWidth(Region.USE_COMPUTED_SIZE);
                grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                grid.setMaxWidth(Region.USE_PREF_SIZE);

                GridPane.setMargin(anchorPane, new Insets(10));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getOrders() {
        // query database for all active orders (state != finished)
        String[] cols = {"id", "state", "order_id", "table_num"};
        String query = "SELECT * FROM order_queue WHERE state != \'finished\' ORDER BY id desc";
        ArrayList<String> data = db.queryDatabase(query, cols);

        ArrayList<String> orderProducts = new ArrayList<>();
        ArrayList<String> productMods = new ArrayList<>();

        // loop through data, creating a QueueItem for each order, appending it to array list
        for (int i = 0; i < data.size(); i++) {
            String[] row = data.get(i).split("--");

            // query database for each product name and modifications using order_id
            String[] colsOrderProd = {"name", "product_mods"};
            query = "SELECT products.name, product_mods FROM order_product INNER JOIN products ON order_product.product_id = products.id WHERE order_product.order_id = " + row[2];
            ArrayList<String> prodData = db.queryDatabase(query, colsOrderProd);

            for(String str:prodData) {
                String[] splitOrderProd = str.split("--");
                orderProducts.add(splitOrderProd[0]);
                productMods.add(splitOrderProd[1]);
            }

            QueueItem queueItem = new QueueItem(row[0], row[1], row[2], row[3], orderProducts, productMods);
            queueItems.add(queueItem);
            System.out.println(data.get(i));
        }
    }

    @FXML
    void refresh(ActionEvent event) {
        cont.switchScene(event, "OrderQueue");
    }

    @FXML
    void logout(ActionEvent event) {
        StaticInfo.userId = "";
        cont.switchScene(event, "Login");
        return;
    }

}