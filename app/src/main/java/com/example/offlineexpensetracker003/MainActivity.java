package com.example.offlineexpensetracker003;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int SMS_PERMISSION_CODE = 101;
    private TextView smsTextView;
    private Set<String> uniqueMessages; // To store unique messages
    private List<String> messagesList;  // To store messages containing 'debited' or 'credited'

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        smsTextView = findViewById(R.id.smsTextView);
        uniqueMessages = new HashSet<>();
        messagesList = new ArrayList<>();

        // Request the READ_SMS permission
        if (checkSmsPermission()) {
            readSmsMessages();
        }
    }

    private boolean checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);
            return false;
        }
        return true;
    }

    private void readSmsMessages() {
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String messageBody = cursor.getString(cursor.getColumnIndexOrThrow("body"));

                    // Check if the message contains 'debited' or 'credited'
                    if (messageBody.toLowerCase().contains("debited") || messageBody.toLowerCase().contains("credited")) {
                        // Check if the message is unique (not already stored)
                        if (uniqueMessages.add(messageBody)) {
                            // Add the message to the list
                            messagesList.add(messageBody);
                            smsTextView.append(messageBody + "\n");
                        }
                    }
                } while (cursor.moveToNext());
            } else {
                smsTextView.setText("No SMS messages found.");
            }
            cursor.close();
        } else {
            smsTextView.setText("Cursor is null. Something went wrong.");
        }
             messagesList.clear();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, read SMS messages
                readSmsMessages();
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "READ_SMS permission denied. Cannot read SMS messages.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
