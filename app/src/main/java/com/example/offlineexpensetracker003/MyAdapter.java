package com.example.offlineexpensetracker003;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<DatabaseHandler.ExtractedData> dataList;

    public MyAdapter(Context context, List<DatabaseHandler.ExtractedData> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_layout, parent, false);
        }


        DatabaseHandler.ExtractedData data = dataList.get(position);
        TextView amountTextView = convertView.findViewById(R.id.amountTextView);
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView typeTextView = convertView.findViewById(R.id.typeTextView);
        TextView timestampTextView = convertView.findViewById(R.id.dateTextView);


        amountTextView.setText(data.getAmount());
        nameTextView.setText(data.getName());
        typeTextView.setText(data.getType());
        timestampTextView.setText(data.getTimestamp());

        return convertView;
    }
}

