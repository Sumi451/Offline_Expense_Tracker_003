package com.example.offlineexpensetracker003;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DisplayData extends AppCompatActivity implements DatabaseHandler.DataExtractionListener, DatePickerFragment.DatePickerListener {
    private ListView listView;
    private MyAdapter adapter;
    private DatabaseHandler databaseHandler;
    private FloatingActionButton filterButton;
    private String selectedDate = null; // Store the selected date for filtering

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.displaydata);

        listView = findViewById(R.id.listView);
        databaseHandler = new DatabaseHandler();
        filterButton = findViewById(R.id.filterButton);

        // Extract all data from the database
        databaseHandler.extractDataFromDatabase(this);

        // Set an OnClickListener for the filter button
        filterButton.setOnClickListener(v -> {
            // Show the date picker dialog
            showDatePickerDialog();
        });
    }

    @Override
    public void onDataExtracted(List<DatabaseHandler.ExtractedData> extractedData) {
        // Filter data if a date is selected
        if (selectedDate != null) {
            List<DatabaseHandler.ExtractedData> filteredData = filterDataByDate(extractedData, selectedDate);
            adapter = new MyAdapter(this, filteredData);
        } else {
            // No date selected, show all data
            adapter = new MyAdapter(this, extractedData);
        }

        listView.setAdapter(adapter);
    }

    @Override
    public void onDateSet(int year, int month, int day) {
        // Format the selected date as needed (e.g., "YYYY-MM-DD")
        selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day);

        // Re-extract data from the database with the selected date
        databaseHandler.extractDataFromDatabase(this);
    }

    // Show the date picker dialog
    private void showDatePickerDialog() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setListener(this);
        datePickerFragment.showNow(getSupportFragmentManager(), "datePicker");
    }

    // Filter data by the selected date
    private List<DatabaseHandler.ExtractedData> filterDataByDate(List<DatabaseHandler.ExtractedData> data, String selectedDate) {
        List<DatabaseHandler.ExtractedData> filteredData = new ArrayList<>();
        for (DatabaseHandler.ExtractedData item : data) {
            // Check if the timestamp starts with the selected date
            if (item.getTimestamp().startsWith(selectedDate)) {
                filteredData.add(item);
            }
        }
        return filteredData;
    }
}
