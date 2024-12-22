package com.ab527.watchtank;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class Controller extends AppCompatActivity {

    public static ProgressDialog progressDialog;
    private static BluetoothDevice targetDevice;
    private static BluetoothSocket BTSocket;
    private boolean isBTConnected = false;
    private boolean didUserInitialiseDisconnect = false;
    private ReadInput readThread = null;
    private String requestedCommandToArduino = "1";
    private double tankHeight = 0.22;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private int frameNumbers = 150;
    private LottieAnimationView lottieAnimationView;
    private int WLRecord = 1;
    private boolean loopWLCheck = true, WLCheckLoop = true;
    private int loopTime;

    // WL = Water Level; KS = Knob Status
    final static String checkWLCode="1", checkKSCode="2";

    private UUID deviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        Intent intent = getIntent();
        targetDevice = intent.getExtras().getParcelable("targetDevice");

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        new ConnectBT().execute();

        TextView dataTankHeight = findViewById(R.id.dataTH);
        dataTankHeight.setText("Tank Height: " + Double.toString(tankHeight) + " m");

        lottieAnimationView = findViewById(R.id.animationWaterFill);

        Button checkWL = findViewById(R.id.checkWL);
        checkWL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkWaterLevel();
            }
        });

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptsView = layoutInflater.inflate(R.layout.th_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    EditText dialogUserInput = promptsView.findViewById(R.id.THPromptInput);
                    String stringDouble = dialogUserInput.getText().toString();
                    try {
                        tankHeight = Double.parseDouble(stringDouble);
                        dataTankHeight.setText("Tank Height : " + Double.toString(tankHeight) + " m");
                    } catch (NumberFormatException nfe) {
                        Toast.makeText(getApplicationContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

        AlertDialog alertDialog = alertDialogBuilder.create();

        Button setTankHeight = findViewById(R.id.setTankHeight);
        setTankHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
            }
        });

    }

    private void checkWaterLevel() {
        try {
            requestedCommandToArduino = "1";
            BTSocket.getOutputStream().write(checkWLCode.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleResponse(String data) {
        switch(requestedCommandToArduino) {
            case "1":
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        TextView textViewWL = findViewById(R.id.dataWL);
                        double WLPercentage = ((tankHeight*100)-Double.parseDouble(data))/tankHeight;
                        textViewWL.setText(decimalFormat.format(WLPercentage) +" %");

                        TextView textViewWLLC = findViewById(R.id.dataWLLC);
                        LocalDateTime localDateTime = LocalDateTime.now();
                        textViewWLLC.setText("Water Level Last Checked (Date & Time) : " + LocalDateTime.now());

                        String completionTime = "";
                        textViewWLLC.setText("Water Level Last Checked (Date & Time) : " + LocalDateTime.now());

                        int WLRecordTemp = (int)((WLPercentage/100)*frameNumbers);
                        if(WLRecordTemp >= WLRecord) {
                            lottieAnimationView.setMinAndMaxFrame(WLRecord, WLRecordTemp);
                            lottieAnimationView.setSpeed(1);
                        }
                        else {
                            lottieAnimationView.setMinAndMaxFrame(WLRecordTemp, WLRecord);
                            lottieAnimationView.setSpeed(-1);
                        }
                        WLRecord = WLRecordTemp;
                        lottieAnimationView.playAnimation();

                        if(loopWLCheck) {
                            loopTime = LocalDateTime.now().getMinute();
                            checkWaterLevel();
                        }
                    }
                });
                break;
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean connectSuccessful = true;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Controller.this, "Hold on", "Connecting");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (BTSocket == null || !isBTConnected) {
                    try {
                        BTSocket = targetDevice.createInsecureRfcommSocketToServiceRecord(deviceUUID);
                        BTSocket.connect();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }

                }
            } catch (IOException e) {
                connectSuccessful = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!connectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device", Toast.LENGTH_LONG).show();
                finish();
            } else {
                isBTConnected = true;
                readThread = new ReadInput();
            }
            progressDialog.dismiss();
            checkWaterLevel();
        }

    }

    private class DisconnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {}

        @Override
        protected Void doInBackground(Void... params) {

            if (readThread != null) {
                readThread.stop();
                while (readThread.isRunning());
                readThread = null;
            }

            try {
                BTSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            isBTConnected = false;
            if (didUserInitialiseDisconnect) {
                finish();
            }
        }

    }

    private class ReadInput implements Runnable {

        private boolean btStop = false;
        private Thread thread;

        public ReadInput() {
            thread = new Thread(this, "Input Thread");
            thread.start();
        }

        public boolean isRunning() {
            return thread.isAlive();
        }

        @Override
        public void run() {

            InputStream inputStream;

            try {
                inputStream = BTSocket.getInputStream();
                while (!btStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) { }
                        final String strInput = new String(buffer, 0, i);
                        try {
                            handleResponse(strInput);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void stop()
        {
            btStop = true;
        }

    }

    private void destroyThreads() {
        if(readThread != null)
            readThread.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyThreads();
    }

    @Override
    protected void onPause() {
        super.onPause();
        destroyThreads();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        destroyThreads();
    }
}