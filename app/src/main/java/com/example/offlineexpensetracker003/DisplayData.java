package com.example.offlineexpensetracker003;
import android.os.Bundle;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
public class DisplayData extends AppCompatActivity implements DatabaseHandler.DataExtractionListener {
    private ListView listView;
    private MyAdapter adapter;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.displaydata);

        listView = findViewById(R.id.listView); // Assuming you have a ListView in your layout
        databaseHandler = new DatabaseHandler();

        // Extract data from the database
        databaseHandler.extractDataFromDatabase(this);
    }
    @Override
    public void onDataExtracted(List<DatabaseHandler.ExtractedData> extractedData) {
        adapter = new MyAdapter(this, extractedData);
        listView.setAdapter(adapter);
    }
}
