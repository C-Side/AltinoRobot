package com.example.ibnas.altinorobot;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.bluetooth.BluetoothAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainController";
    private static final boolean D = true;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static String mConnectedDeviceName = null;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private BluetoothAdapter mBluetoothAdapter = null;
    public static BluetoothService mAltinoAppService = null;
    private TextView mStatus;
    private ProgressBar connectload;
    private Button bluetooth_search;
    private Button EXIT;

    //Buttons for the motor
    private Button bt_forward;
    private Button bt_stop;
    private Button bt_backward, bt_led_on, bt_led_off, bt_buzz_on, bt_buzz_off;
    private ToggleButton bt_loop;

    public static byte[] sendBuf_byte = new byte[28];


    /**
     * gets called when the object is created
     * @param savedInstanceState I got no idea what it does
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mStatus = (TextView) findViewById(R.id.txtStatus);
        connectload = (ProgressBar) findViewById(R.id.connectload);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        bluetooth_search = (Button) findViewById(R.id.bt_bluetooth);
        bluetooth_search.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        });

        //defines the exit button action
        EXIT = (Button) findViewById(R.id.bt_exit);
        EXIT.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                exitDialog();
            }
        });

        // Defines the action of the forward button
        bt_forward = (Button) findViewById(R.id.bt_forward);
        bt_forward.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                sendBuf_byte[4] = 1;
                sendBuf_byte[5] = 2;
                sendBuf_byte[6] = 0;
                sendBuf_byte[7] = (byte) (300 / 256);
                sendBuf_byte[8] = (byte) (300 % 256);
                sendBuf_byte[9] = 0;
                sendBuf_byte[10] = (byte) (300 / 256);
                sendBuf_byte[11] = (byte) (300 % 256);
                sendBuf_byte[23] = (byte) (0x03);
                sendByte(sendBuf_byte);
            }
        });

        // Defines the action of the stop button
        bt_stop = (Button) findViewById(R.id.bt_stop);
        bt_stop.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                sendBuf_byte[4] = 1;
                sendBuf_byte[5] = 2;
                sendBuf_byte[6] = 0;
                sendBuf_byte[7] = 0;
                sendBuf_byte[8] = 0;
                sendBuf_byte[9] = 0;
                sendBuf_byte[10] = 0;
                sendBuf_byte[11] = 0;
                sendBuf_byte[23] = 0;
                sendByte(sendBuf_byte);
            }
        });

        // Defines the action of the backward button
        bt_backward = (Button) findViewById(R.id.bt_backward);
        bt_backward.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                sendBuf_byte[4] = 1;
                sendBuf_byte[5] = 2;
                sendBuf_byte[6] = 0;
                sendBuf_byte[7] = (byte) ((32768 + 300) / 256);
                sendBuf_byte[8] = (byte) ((32768 + 300) % 256);
                sendBuf_byte[9] = 0;
                sendBuf_byte[10] = (byte) ((32768 + 300) / 256);
                sendBuf_byte[11] = (byte) ((32768 + 300) % 256);
                sendBuf_byte[23] = (byte) (0xC0);
                sendByte(sendBuf_byte);
            }
        });

        // Defines the action of turning on LED
        bt_led_on = (Button) findViewById(R.id.bt_led_on);
        bt_led_on.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                sendBuf_byte[4] = 1;
                sendBuf_byte[23] = (byte) (0xFF);
                sendByte(sendBuf_byte);
            }
        });

        // Defines the action of turning off LED
        bt_led_off = (Button) findViewById(R.id.bt_led_off);
        bt_led_off.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                sendBuf_byte[4] = 1;
                sendBuf_byte[23] = (byte) (0);
                sendByte(sendBuf_byte);
            }
        });

        // Defines the action of turning on Buzzer
        bt_buzz_on = (Button) findViewById(R.id.bt_buzz_on);
        bt_buzz_on.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                sendBuf_byte[4] = 1;
                sendBuf_byte[22] = 37;
                sendByte(sendBuf_byte);
            }
        });

        // Defines the action of turning on Buzzer
        bt_buzz_off = (Button) findViewById(R.id.bt_buzz_off);
        bt_buzz_off.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                sendBuf_byte[4] = 1;
                sendBuf_byte[22] = 0;
                sendByte(sendBuf_byte);
            }
        });

        // Defines the action of turning on Buzzer
        bt_loop = (ToggleButton) findViewById(R.id.bt_loop);
        bt_loop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            final Handler handler = new Handler();
            boolean bt_loop_check = false;
            int i = 0;
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    if (bt_loop_check) {
                        int array[] = {37,39,41,42,44,46,48,49};
                        int arrayLED[] = {0x20,0x02,0x01,0x10,0x40,0x04,0x08,0x80};

                        int arrayDot_1[] = {0xFF,0x00,0xFF,0x99,0x66,0x00,0xFF,0x40,0x20,0x10,0x08,0x04,0x02,0x01,0xFF,0x00,0xFF,0x01,0x01,0xFF,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
                        int arrayDot_2[] = {0x00,0xFF,0x00,0xFF,0x99,0x66,0x00,0xFF,0x40,0x20,0x10,0x08,0x04,0x02,0x01,0xFF,0x00,0xFF,0x01,0x01,0xFF,0x00,0x00,0x00,0x00,0x00,0x00};
                        int arrayDot_3[] = {0x00,0x00,0xFF,0x00,0xFF,0x99,0x66,0x00,0xFF,0x40,0x20,0x10,0x08,0x04,0x02,0x01,0xFF,0x00,0xFF,0x01,0x01,0xFF,0x00,0x00,0x00,0x00,0x00};
                        int arrayDot_4[] = {0x00,0x00,0x00,0xFF,0x00,0xFF,0x99,0x66,0x00,0xFF,0x40,0x20,0x10,0x08,0x04,0x02,0x01,0xFF,0x00,0xFF,0x01,0x01,0xFF,0x00,0x00,0x00,0x00};
                        int arrayDot_5[] = {0x00,0x00,0x00,0x00,0xFF,0x00,0xFF,0x99,0x66,0x00,0xFF,0x40,0x20,0x10,0x08,0x04,0x02,0x01,0xFF,0x00,0xFF,0x01,0x01,0xFF,0x00,0x00,0x00};
                        int arrayDot_6[] = {0x00,0x00,0x00,0x00,0x00,0xFF,0x00,0xFF,0x99,0x66,0x00,0xFF,0x40,0x20,0x10,0x08,0x04,0x02,0x01,0xFF,0x00,0xFF,0x01,0x01,0xFF,0x00,0x00};
                        int arrayDot_7[] = {0x00,0x00,0x00,0x00,0x00,0x00,0xFF,0x00,0xFF,0x99,0x66,0x00,0xFF,0x40,0x20,0x10,0x08,0x04,0x02,0x01,0xFF,0x00,0xFF,0x01,0x01,0xFF,0x00};
                        int arrayDot_8[] = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0xFF,0x00,0xFF,0x99,0x66,0x00,0xFF,0x40,0x20,0x10,0x08,0x04,0x02,0x01,0xFF,0x00,0xFF,0x01,0x01,0xFF};

                        sendBuf_byte[4] = 1;
                        sendBuf_byte[13] = (byte) arrayDot_1[i];
                        sendBuf_byte[14] = (byte) arrayDot_2[i];
                        sendBuf_byte[15] = (byte) arrayDot_3[i];
                        sendBuf_byte[16] = (byte) arrayDot_4[i];
                        sendBuf_byte[17] = (byte) arrayDot_5[i];
                        sendBuf_byte[18] = (byte) arrayDot_6[i];
                        sendBuf_byte[19] = (byte) arrayDot_7[i];
                        sendBuf_byte[20] = (byte) arrayDot_8[i];
                        sendBuf_byte[22] = 0;
                        //sendBuf_byte[23] = (byte) arrayLED[i];
                        sendByte(sendBuf_byte);
                        i++;
                        if (i == arrayDot_1.length) i = 0;
                        handler.postDelayed(this,500);
                    }
                }
            };

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bt_loop_check = true;
                    handler.post(run);
                } else {
                    handler.removeCallbacks(run);
                    bt_loop_check = false;
                    sendBuf_byte[4] = 1;
                    sendBuf_byte[13] = 0;
                    sendBuf_byte[14] = 0;
                    sendBuf_byte[15] = 0;
                    sendBuf_byte[16] = 0;
                    sendBuf_byte[17] = 0;
                    sendBuf_byte[18] = 0;
                    sendBuf_byte[19] = 0;
                    sendBuf_byte[20] = 0;
                    sendBuf_byte[22] = 0;
                    sendBuf_byte[23] = 0;
                    sendByte(sendBuf_byte);
                    i = 0;
                }
            }
        });
    }

    public void onStart() {
        super.onStart();
        if (D)
            Log.e(TAG, "++ ON START ++");
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mAltinoAppService == null)
                mAltinoAppService = new BluetoothService(this, mHandler);
        }
    }
    public synchronized void onResume() {
        super.onResume();
        if (D)
            Log.e(TAG, "+ ON RESUME +");
        if (mAltinoAppService != null) {
            if (mAltinoAppService.getState() == BluetoothService.STATE_NONE) {
                mAltinoAppService.start();
            }
        }
    }
    public synchronized void onPause() {
        super.onPause();
        if (D)
            Log.e(TAG, "- ON PAUSE -");
    }
    public void onStop() {
        super.onStop();
        if (D)
            Log.e(TAG, "-- ON STOP --");
    }
    public void onDestroy() {
        super.onDestroy();
        if (mAltinoAppService != null)
            mAltinoAppService.stop();
        if (D)
            Log.e(TAG, "--- ON DESTROY ---");
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D)
            Log.d(TAG, "onActivityResult " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(
                            DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = mBluetoothAdapter
                            .getRemoteDevice(address);
                    mAltinoAppService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    if (mAltinoAppService == null)
                        mAltinoAppService = new BluetoothService(this, mHandler);
                } else {
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }
    public final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            mStatus.setText(R.string.status_connected_to);
                            mStatus.append(mConnectedDeviceName);
                            connectload.setVisibility(View.VISIBLE);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            mStatus.setText(R.string.status_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            mStatus.setText(R.string.status_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    break;
                case MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),
                            "Connected to " + mConnectedDeviceName,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    };
    public void sendByte(byte[] Sendbuf) {
        if (mAltinoAppService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mAltinoAppService.Sendbyte(Sendbuf);
    }
    private void exitDialog() {
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
        exitDialog
                .setMessage("Altino Aplication Real Exit?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mAltinoAppService.stop();
                                System.exit(0);
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog exitalert = exitDialog.create();
        exitalert.setTitle("AltinoApp");
        exitalert.setIcon(R.mipmap.ic_launcher);
        exitalert.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.bt_bluetooth) {
            bluetooth_search.performClick();
        }

        return super.onOptionsItemSelected(item);
    }
}
