package com.example.offlineexpensetracker003;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    private static final String JDBC_URL = "jdbc:mysql://192.168.0.104:3306/transactions?useSSL=false&allowPublicKeyRetrieval=true";
    // private static final String JDBC_URL = "jdbc:mysql://@localhost:3306/transactions?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USERNAME = "Sammyrun";
    private static final String DB_PASSWORD = "Sammyrun";

    public interface DataExtractionListener {//listener to notify when the data extraction is complete

        void onDataExtracted(List<ExtractedData> extractedData);
    }

    public static void insertMessageIntoDatabase(String message, String receivedTimestamp) {
        new InsertMessageTask().execute(message, receivedTimestamp);
    }

    public void extractDataFromDatabase(DataExtractionListener listener) {
        new ExtractDataTask(listener).execute();
    }

    private static class InsertMessageTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (params.length < 2) {//expects message and timestamp as params
                return null;
            }

            String message = params[0];
            String receivedTimestamp = params[1];
            Connection connection = null;
            PreparedStatement statement = null;

            try {
                // Establish a database connection
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);

                // SQL statement for inserting messages
                String insertQuery = "INSERT INTO trans (messages,timestamp) VALUES (?,?)";

                // Prepare the statement
                statement = connection.prepareStatement(insertQuery);

                // Insert the message into the database
                statement.setString(1, message);
                statement.setString(2, receivedTimestamp);
                statement.executeUpdate();
            } catch (SQLException e) {
                if (e.getSQLState().equals("23000")) {
                    // Duplicate key violation
                    System.out.println("Duplicate message timestamp. Omitting the message.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Close the resources
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    public static class ExtractedData {
        private String amount;
        private String name;
        private String type;
        private String timestamp;

        public ExtractedData(String amount, String name, String type, String timestamp) {
            this.amount = amount;
            this.name = name;
            this.type = type;
            this.timestamp=timestamp;
        }

        public String getAmount() {
            return amount;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
        public String getTimestamp(){return timestamp;}
    }
    private static class ExtractDataTask extends AsyncTask<Void, Void, List<ExtractedData>> {
        private DataExtractionListener listener;

        public ExtractDataTask(DataExtractionListener listener) {
            this.listener = listener;
        }
        @Override
        protected List<ExtractedData> doInBackground(Void... voids) {
            List<ExtractedData> extractedData = new ArrayList<>();
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try {
                // Establish a database connection
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);

                // SQL statement for extracting data (replace with your query)
                String extractQuery = "SELECT amount,name,type,timestamp FROM trans_det";
                statement = connection.prepareStatement(extractQuery);
                resultSet = statement.executeQuery();

                // Process the query result and populate the extractedData list
                while (resultSet.next()) {
                    String amount = resultSet.getString("amount");
                    String name = resultSet.getString("name");
                    String type = resultSet.getString("type");
                    String timestamp=resultSet.getString("timestamp");
                    extractedData.add(new ExtractedData(amount, name, type,timestamp));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Close the resources
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return extractedData;
        }
        @Override
        protected void onPostExecute(List<ExtractedData> extractedData) {
            if (listener != null) {
                listener.onDataExtracted(extractedData);
            }
        }
    }

}
