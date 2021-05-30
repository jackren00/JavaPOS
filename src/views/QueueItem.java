package views;

import gui.DatabaseConn;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Spliterator;

public class QueueItem {
    public enum queueenum {placed, preparing, cooking, rts, finished};

    private String id;
    private String orderId;
    private String tableNum;
    private queueenum state;

    private DatabaseConn db = new DatabaseConn();

    // These array lists should be 1:1, containing the modifications for each order product
    // If there are no modifications, empty string
    private ArrayList<String> orderProducts;
    private ArrayList<String> productMods;

    public QueueItem(String i, String s, String oi, String t, ArrayList<String> op, ArrayList<String> pm) {
        id = i;
        state = queueenum.valueOf(s);
        orderId = oi;
        tableNum = t;
        orderProducts = op;
        productMods = pm;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderInfo() {
        String text = "Table " + tableNum + "\n\n";
        // returns formatted string with Order Info
        for(int i = 0; i < orderProducts.size(); i++) {
            text = text + "- "+ orderProducts.get(i) + "\n";
            if (!productMods.get(i).equals("{}")) {
                text = text + "\t - " + productMods.get(i).substring(1, productMods.get(i).length() - 1) + "\n";
            }
        }

        return text;
    }

    public String getState() {
        String str = "";

        switch(state) {
            case placed:
                str = "PLACED";
                break;
            case preparing:
                str = "PREPARING";
                break;
            case cooking:
                str = "COOKING";
                break;
            case rts:
                str = "RTS";
                break;
            case finished:
                str = "FINISHED";
                break;
        }

        return str;
    }

    public void nextState() {
        String nextState = "";
        switch(state) {
            case placed:
                nextState = "preparing";
                state = queueenum.preparing;
                break;
            case preparing:
                nextState = "cooking";
                state = queueenum.cooking;
                break;
            case cooking:
                nextState = "rts";
                state = queueenum.rts;
                break;
            case rts:
                nextState = "finished";
                state = queueenum.finished;
                break;
        }
        if(!nextState.equals("")) {
            String query = "UPDATE order_queue SET state = \'" + nextState + "\' WHERE id = " + id;
            db.queryDatabase(query, null);
        }
    }

    public void prevState() {
        String prevState = "";
        switch(state) {
            case preparing:
                prevState = "placed";
                state = queueenum.placed;
                break;
            case cooking:
                prevState = "preparing";
                state = queueenum.preparing;
                break;
            case rts:
                prevState = "cooking";
                state = queueenum.cooking;
                break;
            case finished:
                prevState = "rts";
                state = queueenum.rts;
                break;
        }
        if(!prevState.equals("")) {
            String query = "UPDATE order_queue SET state = \'" + prevState + "\' WHERE id = " + id;
            db.queryDatabase(query, null);
        }
    }
}
