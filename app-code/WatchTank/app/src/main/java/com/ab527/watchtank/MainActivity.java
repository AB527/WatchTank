package com.ab527.watchtank;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter BTAdapter;
    private ListView BTListView;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> requestBTEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BTListView = findViewById(R.id.BTList);

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                findBluetooth();
            }
        });

        requestBTEnable = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                findBluetooth();
            }
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            findBluetooth();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
        }

    }

    public void findBluetooth() {

        BTAdapter = BluetoothAdapter.getDefaultAdapter();

        if (BTAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth not found", Toast.LENGTH_SHORT).show();
        } else if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            requestBTEnable.launch(enableBT);
        } else {
            new SearchDevices().execute();
        }
    }

    private class SearchDevices extends AsyncTask<Void, Void, List<BluetoothDevice>> {

        @Override
        protected List<BluetoothDevice> doInBackground(Void... params) {
            Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();
            List<BluetoothDevice> listDevices = new ArrayList<BluetoothDevice>();
            for (BluetoothDevice device : pairedDevices) {
                listDevices.add(device);
            }
            return listDevices;
        }

        @Override
        protected void onPostExecute(List<BluetoothDevice> listDevices) {
            super.onPostExecute(listDevices);
            if (listDevices.size() > 0) {
                BTListAdapter adapter = new BTListAdapter(MainActivity.this, R.string.app_name, R.string.app_name, listDevices);
                adapter.replaceItems(listDevices);
                BTListView.setAdapter(adapter);
                BTListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        connectBluetooth(listDevices.get(i));
                    }
                });
            }
        }

    }

    public void connectBluetooth(BluetoothDevice device) {
        Intent intent = new Intent(getApplicationContext(), Controller.class);
        intent.putExtra("targetDevice", device);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}