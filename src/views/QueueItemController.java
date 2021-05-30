package views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class QueueItemController {

    @FXML
    private Label orderId;

    @FXML
    private TextArea orderInfo;

    @FXML
    private Button prev;

    @FXML
    private Button next;

    @FXML
    private Label currentState;

    private QueueItem queueItem;

    public void setData(QueueItem qI) {
        queueItem = qI;
        orderId.setText(queueItem.getOrderId());
        orderInfo.setText(queueItem.getOrderInfo());
        currentState.setText(queueItem.getState());
    }

    public void setNext(ActionEvent event) {
        queueItem.nextState();
        currentState.setText(queueItem.getState());
    }

    public void setPrev(ActionEvent event) {
        queueItem.prevState();
        currentState.setText(queueItem.getState());
    }
}