package com.ab527.watchtank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothList extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_list);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        ArrayList<String> bluetoothDevicesShowList = new ArrayList<String>();
        ArrayAdapter<String> bluetoothDevicesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bluetoothDevicesShowList);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(bluetoothDevicesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int divider = adapterView.getItemAtPosition(i).toString().indexOf("\n");
                String[] deviceInfo = adapterView.getItemAtPosition(i).toString().split("\n");
                Intent data = new Intent();
                data.putExtra("selectedBluetoothName", deviceInfo[0]);
                data.putExtra("selectedBluetoothAddress", deviceInfo[1]);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                bluetoothDevicesShowList.add(device.getName()+"\n"+device.getAddress());
                bluetoothDevicesAdapter.notifyDataSetChanged();
            }
        }
    }
}