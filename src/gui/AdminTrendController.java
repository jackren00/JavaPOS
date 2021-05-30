package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;


public class AdminTrendController implements Initializable {

    private DatabaseConn db = new DatabaseConn();
    private Controller cont = new Controller();

    @FXML private ChoiceBox<String> productId = new ChoiceBox<>();

    @FXML private TextField startDate;
    @FXML private TextField endDate;
    @FXML private TextField newPrice;
    @FXML private TextField revenueChange;

    @FXML private LineChart<Number, Number> lineChart;

    private int total;

    public void goBack(ActionEvent event){
        cont.switchScene(event, "AdminMainMenu");
        return;
    }

    public void logout(ActionEvent event){
        StaticInfo.userId = "";
        cont.switchScene(event, "Login");
        return;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] cols = {"name", "id"};
        String query = "SELECT * FROM products";
        ArrayList<String> data = db.queryDatabase(query, cols);

        for(String line : data){
            productId.getItems().add(line.split("--")[1] + " : " + line.split("--")[0]);
        }
    }

    public void reload(ActionEvent e){
        lineChart.getData().clear();

        String start = startDate.getText();
        String end = endDate.getText();
        long startVal = dateToNum(start);
        long endVal = dateToNum(end);

        // Creating the Axes
        final NumberAxis xAxis = new NumberAxis(0, endVal-startVal, 1); // start, end, discrete jump
        final NumberAxis yAxis = new NumberAxis(); // not specified
        xAxis.setLabel("Date");
        yAxis.setLabel("Sales");

        // Creating the chart
        lineChart.setTitle("Sales Trends");
        lineChart.setAnimated(true);

        XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();

        String[] cols = {"date"};
        String query = "SELECT orders.date FROM order_product "+
                "INNER JOIN orders ON order_product.order_id = orders.id "+
                "WHERE orders.date > \'" + start + "\' AND orders.date < \'" + end + "\' " +
                "AND order_product.product_id = " + productId.getValue().split(" : ")[0];
        ArrayList<String> results = db.queryDatabase(query, cols);

        // Setup the counts of the products on the dates
        HashMap<String, Integer> counts = new HashMap<>();
        for(String date : results){
            counts.put(date.split("--")[0], 0);
        }
        for(String date : results){
            counts.put(date.split("--")[0], counts.get(date.split("--")[0]) + 1);
        }
        total = results.size();
        for(String date : results){
            series.getData().add(new XYChart.Data<Number, Number>(endVal - dateToNum(date.split("--")[0]), counts.get(date.split("--")[0])));
        }

        lineChart.getData().add(series);
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);
    }

    private long dateToNum(String date){
        int returnVal = 0;
        String[] vals = date.split("-");
        returnVal += Integer.parseInt(vals[2]);
        returnVal += Integer.parseInt(vals[1])*30;
        returnVal += Integer.parseInt(vals[0])*365;
        return returnVal;
    }

    public void getChangeInRev(){
        if (total == 0) {
            revenueChange.setText("$0.00");
        }
        String[] cols = {"price"};
        String query = "SELECT * FROM products WHERE id = " + productId.getValue().split(" : ")[0];
        ArrayList<String> results = db.queryDatabase(query, cols);

        int oldPrice = Integer.parseInt(results.get(0).split("--")[0]);
        int nwPrice = Integer.parseInt(newPrice.getText());
        int diff = (nwPrice - oldPrice);
        diff *= total;

        String difference = Integer.toString(diff);

        while(difference.length() < 3){
            difference = "0" + difference;
        }
        revenueChange.setText("$" + difference.substring(0, difference.length() - 2) + "." + difference.substring(difference.length() - 2));
    }
}

