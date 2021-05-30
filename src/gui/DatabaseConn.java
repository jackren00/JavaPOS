package gui;

import java.sql.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class DatabaseConn {
    private DatabaseSetup ds = new DatabaseSetup();

    // Function to query database and return an array list of strings
    // Each string is a row where each entry separated by a "--" is specified in String[] cols
    public ArrayList<String> queryDatabase(String query, String[] cols){
        ArrayList<String> returnArr = new ArrayList<String>();
        Connection conn = null;

        // Try connecting
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/db903_group1_project2", ds.username, ds.password);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(1);
        }

        // Try sending the query
        try{
            // Create a statement object and send to database
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);

            if(cols != null){

                // Create and add strings separated by "--" for the arrayList
                while (result.next()) {
                    String temporary = "";
                    for(String s : cols){
                        temporary += result.getString(s) + "--";
                    }
                    if(!temporary.equals("")){ returnArr.add(temporary); }
                }


            }
        }
        catch (Exception e){ }

        // Close the connection
        try { conn.close(); }
        catch(Exception e) { }

        return returnArr;
    }

    // Helper function to determine whether an email is a valid one via regex
    // Source: GeeksForGeeks
    public boolean isValidEmail(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }
}
