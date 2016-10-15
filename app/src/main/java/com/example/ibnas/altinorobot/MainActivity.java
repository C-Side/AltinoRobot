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

    //text view for something
    private TextView cds, ir1, ir2, ir3, ir4, ir5, ir6;

    //fields for the sensor values
    private int value_s1, value_s2;

    //byte buffer
    public static byte[] sendBuf_byte = new byte[28];
    private byte[] readBuf = new byte[31];
    private byte[] readBuf_byte = new byte[31];
    private byte[] readBuf_byte2 = new byte[31];
    private int cnt = 0;


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
        sendBuf_byte[21] = 10;
        bt_loop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            final Handler handler = new Handler();
            boolean bt_loop_check = false;
            boolean driving = false;
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    if (bt_loop_check) {
                        if (value_s2 < 50 && !driving) {
                            sendBuf_byte[4] = 1;
                            sendBuf_byte[5] = 2;
                            sendBuf_byte[6] = 0;
                            sendBuf_byte[7] = (byte) (240 / 256);
                            sendBuf_byte[8] = (byte) (240 % 256);
                            sendBuf_byte[9] = 0;
                            sendBuf_byte[10] = (byte) (240 / 256);
                            sendBuf_byte[11] = (byte) (240 % 256);
                            sendBuf_byte[23] = (byte) (0x03);
                            sendByte(sendBuf_byte);
                            driving = true;
                        } else if (value_s2 >= 50 && driving) {
                            sendBuf_byte[4] = 1;
                            sendBuf_byte[5] = 2;
                            sendBuf_byte[6] = 0;
                            sendBuf_byte[7] = (byte) ((32768 + 240) / 256);
                            sendBuf_byte[8] = (byte) ((32768 + 240) % 256);
                            sendBuf_byte[9] = 0;
                            sendBuf_byte[10] = (byte) ((32768 + 240) / 256);
                            sendBuf_byte[11] = (byte) ((32768 + 240) % 256);
                            sendBuf_byte[23] = (byte) (0xC0);
                            sendByte(sendBuf_byte);
                            
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
                            driving = false;
                        }
                        handler.postDelayed(this,10);
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
                    driving = false;
                    bt_stop.performClick();
                }
            }
        });

        cds = (TextView) findViewById(R.id.s_cds);
        ir1 = (TextView) findViewById(R.id.ir1);
        ir2 = (TextView) findViewById(R.id.ir2);
        ir3 = (TextView) findViewById(R.id.ir3);
        ir4 = (TextView) findViewById(R.id.ir4);
        ir5 = (TextView) findViewById(R.id.ir5);
        ir6 = (TextView) findViewById(R.id.ir6);
    }

    /**
     * checks the data
     */
    public void checkData() {
        int rx_check_sum;
        if ((readBuf[0] == 2) && (readBuf[30] == 3) && (readBuf[1] == 31)) {
            rx_check_sum = readBuf[0];
            rx_check_sum = rx_check_sum + readBuf[1];
            rx_check_sum = rx_check_sum + readBuf[3];
            rx_check_sum = rx_check_sum + readBuf[4];
            rx_check_sum = rx_check_sum + readBuf[5];
            rx_check_sum = rx_check_sum + readBuf[6];
            rx_check_sum = rx_check_sum + readBuf[7];
            rx_check_sum = rx_check_sum + readBuf[8];
            rx_check_sum = rx_check_sum + readBuf[9];
            rx_check_sum = rx_check_sum + readBuf[10];
            rx_check_sum = rx_check_sum + readBuf[11];
            rx_check_sum = rx_check_sum + readBuf[12];
            rx_check_sum = rx_check_sum + readBuf[13];
            rx_check_sum = rx_check_sum + readBuf[14];
            rx_check_sum = rx_check_sum + readBuf[15];
            rx_check_sum = rx_check_sum + readBuf[16];
            rx_check_sum = rx_check_sum + readBuf[17];
            rx_check_sum = rx_check_sum + readBuf[18];
            rx_check_sum = rx_check_sum + readBuf[19];
            rx_check_sum = rx_check_sum + readBuf[20];
            rx_check_sum = rx_check_sum + readBuf[21];
            rx_check_sum = rx_check_sum + readBuf[22];
            rx_check_sum = rx_check_sum + readBuf[23];
            rx_check_sum = rx_check_sum + readBuf[24];
            rx_check_sum = rx_check_sum + readBuf[25];
            rx_check_sum = rx_check_sum + readBuf[26];
            rx_check_sum = rx_check_sum + readBuf[27];
            rx_check_sum = rx_check_sum + readBuf[28];
            rx_check_sum = rx_check_sum + readBuf[29];
            rx_check_sum = rx_check_sum + readBuf[30];
            rx_check_sum = rx_check_sum % 256;
            if (rx_check_sum == readBuf[2]) {
                if(readBuf[4]==1)
                {
                    for(int i=7; i<28; i++) {
                        readBuf_byte[i] = readBuf[i];
                    }
                }
                else
                {
                    for(int i=7; i<26; i++) {
                        readBuf_byte2[i] = readBuf[i];
                    }
                }
                cds.setText(""+((readBuf_byte[23] & 0xff)*256+(readBuf_byte[24] & 0xff)));
                value_s1 = (readBuf_byte[7] & 0xff)*256+(readBuf_byte[8] & 0xff);
                ir1.setText(""+(value_s1));
                value_s2 = (readBuf_byte[9] & 0xff)*256+(readBuf_byte[10] & 0xff);
                ir2.setText(""+(value_s2));
                ir3.setText(""+((readBuf_byte[11] & 0xff)*256+(readBuf_byte[12] & 0xff)));
                ir4.setText(""+((readBuf_byte[13] & 0xff)*256+(readBuf_byte[14] & 0xff)));
                ir5.setText(""+((readBuf_byte[15] & 0xff)*256+(readBuf_byte[16] & 0xff)));
                ir6.setText(""+((readBuf_byte[17] & 0xff)*256+(readBuf_byte[18] & 0xff)));
            }
        }
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
                            sendBuf_byte[4] = 1;
                            sendByte(sendBuf_byte);
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
                    readBuf = (byte[]) msg.obj;
                    checkData();
                    if(cnt%2==0) {
                        sendBuf_byte[4] = 2;
                    } else {
                        sendBuf_byte[4] = 1;
                    }
                    sendByte(sendBuf_byte);
                    cnt++;
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
