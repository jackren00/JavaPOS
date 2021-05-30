package gui;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class OrderStatusController implements Initializable {
    private Controller cont = new Controller();
    private DatabaseConn db = new DatabaseConn();

    @FXML
    ProgressBar orderState = new ProgressBar(0);

    // query database based on orderId in Static Info
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        final Service thread = new Service<Integer>() {
            @Override
            public Task createTask() {
                return new Task<Integer>() {
                    @Override
                    public Integer call() throws InterruptedException {
                        String state = "";

                        // loop until state is finished
                        while(!state.equals("finished--")) {

                            //query database for current state
                            String[] order = {"state"};
                            String query = "SELECT state FROM order_queue WHERE order_id = " + StaticInfo.orderId;
                            ArrayList<String> data = db.queryDatabase(query, order);
                            state = data.get(0);
                            System.out.println(state);
                            System.out.println(orderState.getProgress());
                            // updates progress bar based on current order state
                            switch(state) {
                                case "placed--":
                                    updateProgress(20,100);
                                    break;
                                case "preparing--":
                                    updateProgress(40,100);
                                    break;
                                case "cooking--":
                                    updateProgress(60,100);
                                    break;
                                case "rts--":
                                    updateProgress(80,100);
                                    break;
                            }

                            // stop program for 5 seconds
                            Thread.sleep(5000);
                        }

                        updateProgress(100,100);
                        return 100;
                    }
                };
            }
        };

        orderState.progressProperty().bind(thread.progressProperty());
        thread.start();
    }


}
