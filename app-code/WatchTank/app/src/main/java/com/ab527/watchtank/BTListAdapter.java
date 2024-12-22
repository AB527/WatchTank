package com.ab527.watchtank;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BTListAdapter extends ArrayAdapter<BluetoothDevice> {
    private int selectedIndex;
    private Context context;
    private int selectedColor = Color.parseColor("#abcdef");
    private List<BluetoothDevice> myList;
    private View.OnClickListener onClickListener;

    public BTListAdapter(Context context, int resource, int textViewResourceId, List<BluetoothDevice> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        myList = objects;
        selectedIndex = -1;
    }

    public void setSelectedIndex(int position) {
        selectedIndex = position;
        notifyDataSetChanged();
    }

    public BluetoothDevice getSelectedItem() {
        return myList.get(selectedIndex);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public BluetoothDevice getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void replaceItems(List<BluetoothDevice> list) {
        myList = list;
        notifyDataSetChanged();
    }

    public List<BluetoothDevice> getEntireList() {
        return myList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item, null);
        TextView textView = (TextView) view.findViewById(R.id.listContent);

        BluetoothDevice device = myList.get(position);
        textView.setText(device.getName() + "\n " + device.getAddress());
        view.setContentDescription("Test");
        return view;
    }

}