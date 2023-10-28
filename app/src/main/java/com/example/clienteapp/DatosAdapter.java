package com.example.clienteapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class DatosAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> datosList;

    public DatosAdapter(Context context, ArrayList<String> datosList) {
        this.context = context;
        this.datosList = datosList;
    }

    @Override
    public int getCount() {
        return datosList.size();
    }

    @Override
    public Object getItem(int position) {
        return datosList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_layout, null);
        }

        // Configura la vista con los datos
        String data = datosList.get(position);
        TextView textView = convertView.findViewById(R.id.textData);
        textView.setText(data);

        return convertView;
    }
}
