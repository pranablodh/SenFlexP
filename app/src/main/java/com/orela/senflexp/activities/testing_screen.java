package com.orela.senflexp.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.orela.senflexp.R;
import com.orela.senflexp.bleDataParsing.bleDataParser;
import com.orela.senflexp.fileManagement.fileWriter;
import com.orela.senflexp.plotting.mpPlotClass;
import com.orela.senflexp.service.beepSound;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.orela.senflexp.activities.login.REQUEST_CHECK_SETTINGS;

public class testing_screen extends AppCompatActivity
{

    private TextView timer;
    private TextView instruction;
    private TextView temperature;
    private TextView connection_status_senflex;
    private TextView connection_status_pulse;
    private TextView oxy_sat;
    private TextView heart_rate;
    private TextView perfusion_index;
    private TextView test_id;
    private ImageView batteryImage;
    private CardView cardView;
    private Button start;
    private LineChart chart;

    private static final String SHOWCASE_ID = "Test Screen";

    //Plotting Flag
    private boolean plotData = true;

    //Sound Service Flag
    private Boolean soundService = true;

    //Graph X-Axis
    private double x = 0.0;

    //BLE Scan Handler
    private Handler handler = new Handler();

    //Device Names
    private static final String pulse_oximeter_name = "BPL iOxy";
    private static final String senflex_device_name = "SenFlexT";

    //Necessary UUIDs
    private static final String senflex_service_uuid = "79077f9e-6afb-437e-8bf8-3292ed8ea3a1";
    private static final String senflex_characteristic_uuid = "dd0bef0c-6e8b-49cc-a82c-304b64384ec1";
    private static final String senflex_characteristic_battery_uuid = "bddd70d8-0108-43f3-87d4-92ca46997af2";
    private static final String senflex_characteristic_pcb_temp_uuid = "01b06036-a13c-4173-a37c-354b07000a63";
    private static final String senflex_characteristic_led_uuid = "aa707a3f-1f7a-4369-adc6-b70fcd7ee504";
    private static final String pulse_oximeter_service_uuid = "cdeacb80-5235-4c07-8846-93a37ee6b86d";
    private static final String pulse_oximeter_characteristic_uuid = "cdeacb81-5235-4c07-8846-93a37ee6b86d";

    //BLE Status Checking Variable
    private boolean mScanning;
    private boolean isSenFlexSensorAvailable = false;
    private boolean isPulseoximeterAvailable = false;

    //BLE GATT Server
    private BluetoothGatt SenFlexGatt;
    private BluetoothGatt PulseoximeterGatt;

    //Bluetooth Adapter
    private BluetoothAdapter bluetoothAdapter;

    //Permission Management Variables
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int INITIAL_REQUEST = 1337;
    private String[] Permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                    android.Manifest.permission.READ_PHONE_STATE};


    //Defining Maximum Scanning Period
    private static final long SCAN_PERIOD = 10000;

    //Test File Name Variables
    private String senflex_file = "";
    private String ioxy_file = "";

    //Time Tracking Variable
    private long senflex_start_time = 0;
    private long ioxy_start_time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_screen);

        //Hiding Action Bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //UI Elements
        timer = (TextView) findViewById(R.id.timer);
        instruction = (TextView) findViewById(R.id.instruction);
        temperature = (TextView) findViewById(R.id.temperature);
        connection_status_senflex = (TextView) findViewById(R.id.connection_status_senflex);
        connection_status_pulse = (TextView) findViewById(R.id.connection_status_pulse);
        oxy_sat = (TextView) findViewById(R.id.oxy_sat);
        heart_rate = (TextView) findViewById(R.id.heart_rate);
        perfusion_index = (TextView) findViewById(R.id.perfusion_index);
        test_id = (TextView) findViewById(R.id.test_id);
        start = (Button) findViewById(R.id.start);
        chart = (LineChart) findViewById(R.id.chart);
        batteryImage = (ImageView) findViewById(R.id.batteryImage);
        cardView = (CardView) findViewById(R.id.cardView);

        show_show_case_view();
        checkingPermission();
        enableBluetooth();

        Intent intent = getIntent();
        test_id.setText(String.format("Test ID: %s", intent.getStringExtra("test_id")));
        senflex_file = intent.getStringExtra("senflex");
        ioxy_file = intent.getStringExtra("ioxy");

        //Chart Elements
        mpPlotClass.initializeChart(chart, Color.WHITE);

        //On Click Listener
        start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!canWriteExternalStorage() | !canAccessFineLocation()
                  | !canReadExternalStorage() | !canAccessPhoneState()
                  | !enableBluetooth() | !statusCheck())
                {
                    Toast.makeText(testing_screen.this, "Please Allow the Necessary Hardware Access.", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    scan_for_senflex();
                    scan_for_bpl_ioxy();
                }
            }
        });

        batteryImage.setImageResource(R.drawable.battery_0);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        go_to_new_test_page();
    }

    //Screen Lock Disabled
    @Override
    protected void onPause()
    {
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onResume();
    }

    //Checking Permission
    private void checkingPermission()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(!canWriteExternalStorage() || !canAccessFineLocation() ||
               !canReadExternalStorage() || !canAccessPhoneState())
            {
                requestPermissions(Permissions, INITIAL_REQUEST);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canAccessFineLocation()
    {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canWriteExternalStorage()
    {
        return(hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canReadExternalStorage()
    {
        return(hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canAccessPhoneState()
    {
        return(hasPermission(Manifest.permission.READ_PHONE_STATE));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasPermission(String perm)
    {
        return(PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
    }

    private boolean enableBluetooth()
    {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Toast.makeText(this, "Bluetooth LE not supported", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }
        else
        {
            return true;
        }
    }

    //Location Service Checking
    public boolean statusCheck()
    {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        assert manager != null;
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            displayLocationSettingsRequest(testing_screen.this);
            return false;
        }

        else
        {
            return true;
        }
    }

    //Dialog to Open Location Settings
    private void displayLocationSettingsRequest(Context context)
    {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>()
        {
            @Override
            public void onResult(LocationSettingsResult result)
            {
                final Status status = result.getStatus();
                switch (status.getStatusCode())
                {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d("Location", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d("Location", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try
                        {
                            status.startResolutionForResult(testing_screen.this, REQUEST_CHECK_SETTINGS);
                        }

                        catch (IntentSender.SendIntentException e)
                        {
                            Log.d("Location", "PendingIntent unable to execute request.");
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d("Location", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    private void go_to_new_test_page()
    {
        Intent go = new Intent(testing_screen.this, new_test.class);
        startActivity(go);
        finish();
    }

    private void show_show_case_view()
    {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(100);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);
        sequence.setConfig(config);
        sequence.addSequenceItem(temperature, "SelFlexP Temperature Indicator.", "Got It");
        sequence.addSequenceItem(batteryImage, "SelFlexP Battery Level Indicator.", "Got It");
        sequence.addSequenceItem(connection_status_senflex, "SelFlexP Connection Status Indicator.", "Got It");
        sequence.addSequenceItem(chart, "Test Data Plotting Area.", "Got It");
        sequence.addSequenceItem(timer, "Test Duration Indicator.", "Got It");
        sequence.addSequenceItem(instruction, "Test Guidance Will Appear Here.", "Got It");
        sequence.addSequenceItem(cardView, "Test Parameter Will Be Shown Here.", "Got It");
        sequence.addSequenceItem(connection_status_pulse, "PulseOximeter Connection Status Indicator.", "Got It");
        sequence.addSequenceItem(start, "Press Here to Begin The Test.", "Got It");
        sequence.start();
    }

    //Scanning SenFlexP BLE Device Starts Here
    private void scan_for_senflex()
    {
        if (bluetoothAdapter == null)
        {
            return;
        }

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                connection_status_senflex.setText(R.string.scanning);
                connection_status_senflex.setTextColor(ContextCompat.getColor(testing_screen.this, R.color.red));
            }
        });

        List<ScanFilter> filters = new ArrayList<>();
        ScanFilter filter = new ScanFilter.Builder().setDeviceName(senflex_device_name).build();
        filters.add(filter);
        ScanSettings setting = new ScanSettings.Builder().build();

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mScanning = false;
                bluetoothAdapter.getBluetoothLeScanner().stopScan(SenflexLeScanCallback);
            }
        }, SCAN_PERIOD);

        mScanning = true;
        bluetoothAdapter.getBluetoothLeScanner().startScan(filters, setting, SenflexLeScanCallback);
    }

    private ScanCallback SenflexLeScanCallback = new ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {
            final ScanResult res = result;
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    //Device Name Showing
                    if(connection_status_senflex != null)
                    {
                        connection_status_senflex.setText(res.getDevice().getName() == null ? "Null" : res.getDevice().getName());
                        connection_status_senflex.setTextColor(ContextCompat.getColor(testing_screen.this, R.color.lightBlue));
                    }
                }
            });

            //Connect to Device
            SenFlexGatt = res.getDevice().connectGatt(testing_screen.this, false, SenFlexGattCallback);
            Log.d("BLE - Result", res.toString());
            super.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results)
        {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    connection_status_senflex.setText(R.string.scanning_failed);
                    connection_status_senflex.setTextColor(ContextCompat.getColor(testing_screen.this, R.color.red));
                }
            });
            Log.d("Scan failed", String.valueOf(errorCode));
            super.onScanFailed(errorCode);
        }

        private BluetoothGattCallback SenFlexGattCallback = new BluetoothGattCallback()
        {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
            {
                final int state = newState;
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //Change Connection Status Text
                        if(connection_status_senflex != null)
                        {
                            if(state == BluetoothProfile.STATE_CONNECTED)
                            {
                                connection_status_senflex.setText(R.string.connected);
                                connection_status_senflex.setTextColor(ContextCompat.getColor(testing_screen.this, R.color.lightGreen));
                                senflex_start_time = System.currentTimeMillis();
                                countdown_timer();
                            }

                            else
                            {
                                connection_status_senflex.setText(R.string.disconnected);
                                connection_status_senflex.setTextColor(ContextCompat.getColor(testing_screen.this, R.color.red));
                            }
                        }
                    }
                });

                if(state == BluetoothProfile.STATE_CONNECTED)
                {
                    gatt.discoverServices();
                }
                super.onConnectionStateChange(gatt, status, newState);
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status)
            {
                if(status == BluetoothGatt.GATT_SUCCESS)
                {
                    for(BluetoothGattService servc :gatt.getServices())
                    {
                        if(senflex_service_uuid.equals(servc.getUuid().toString()))
                        {
                            for(BluetoothGattCharacteristic item : servc.getCharacteristics())
                            {
                                if(item.getUuid().toString().equals(senflex_characteristic_uuid))
                                {
                                    setCharacteristicNotification(gatt, item, true);
                                }

                                else if(item.getUuid().toString().equals(senflex_characteristic_battery_uuid))
                                {
                                    setCharacteristicNotification(gatt, item, true);
                                }

                                else if(item.getUuid().toString().equals(senflex_characteristic_pcb_temp_uuid))
                                {
                                    setCharacteristicNotification(gatt, item, true);
                                }
                            }
                        }

                    }
                }
                super.onServicesDiscovered(gatt, status);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
            {
                super.onCharacteristicRead(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
            {
                super.onCharacteristicWrite(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
            {
                final BluetoothGattCharacteristic character = characteristic;
                if(characteristic.getUuid().toString().equals(senflex_characteristic_uuid))
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            plotData = true;
                            mpPlotClass.addEntry((float) (((double) bleDataParser.rawBleDataConversion(character.getValue())) * 0.003),
                            chart, 100,"Sensor Data",Color.RED, false);
                            fileWriter.write(senflex_file, String.valueOf(bleDataParser.rawBleDataConversion(character.getValue())),
                            senflex_start_time, testing_screen.this);
                            plotData = false;
                        }
                    });
                }

                else if(characteristic.getUuid().toString().equals(senflex_characteristic_battery_uuid))
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                           batteryImage.setImageResource(bleDataParser.battery_level(character.getValue()));
                        }
                    });
                }

                else if(characteristic.getUuid().toString().equals(senflex_characteristic_pcb_temp_uuid))
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            temperature.setText(String.format("%s°C", String.valueOf(bleDataParser.rawBleDataConversion(character.getValue()))));
                        }
                    });
                }
                super.onCharacteristicChanged(gatt, characteristic);
            }
        };
    };

    //Scanning BPL iOxy Device Starts Here
    private void scan_for_bpl_ioxy()
    {
        if (bluetoothAdapter == null)
        {
            return;
        }

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                connection_status_pulse.setText(R.string.scanning);
                connection_status_pulse.setTextColor(ContextCompat.getColor(testing_screen.this, R.color.red));
            }
        });

        List<ScanFilter> filters = new ArrayList<>();
        ScanFilter filter = new ScanFilter.Builder().setDeviceName(pulse_oximeter_name).build();
        filters.add(filter);
        ScanSettings setting = new ScanSettings.Builder().build();

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mScanning = false;
                bluetoothAdapter.getBluetoothLeScanner().stopScan(PulseoximeterLeScanCallback);
            }
        }, SCAN_PERIOD);

        mScanning = true;
        bluetoothAdapter.getBluetoothLeScanner().startScan(filters, setting, PulseoximeterLeScanCallback);
    }

    private ScanCallback PulseoximeterLeScanCallback = new ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {
            final ScanResult res = result;
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    //Device Name Showing
                    if(connection_status_pulse != null)
                    {
                        connection_status_pulse.setText(res.getDevice().getName() == null ? "Null" : res.getDevice().getName());
                        connection_status_pulse.setTextColor(ContextCompat.getColor(testing_screen.this, R.color.lightBlue));
                    }
                }
            });
            //Connect to Device
            PulseoximeterGatt = res.getDevice().connectGatt(testing_screen.this, false, PulseoximeterGattCallback);
            super.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results)
        {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    connection_status_pulse.setText(R.string.scanning_failed);
                    connection_status_pulse.setTextColor(ContextCompat.getColor(testing_screen.this, R.color.red));
                }
            });
            super.onScanFailed(errorCode);
        }

        private BluetoothGattCallback PulseoximeterGattCallback = new BluetoothGattCallback()
        {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
            {
                final int state = newState;
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //Change Connection Status Text
                        if(connection_status_pulse != null)
                        {
                            if(state == BluetoothProfile.STATE_CONNECTED)
                            {
                                connection_status_pulse.setText(R.string.connected);
                                connection_status_pulse.setTextColor(ContextCompat.getColor(testing_screen.this, R.color.lightGreen));
                                ioxy_start_time = System.currentTimeMillis();
                            }

                            else
                            {
                                connection_status_pulse.setText(R.string.disconnected);
                                connection_status_pulse.setTextColor(ContextCompat.getColor(testing_screen.this, R.color.red));
                            }
                        }
                    }
                });

                if(state == BluetoothProfile.STATE_CONNECTED)
                {
                    gatt.discoverServices();
                }
                super.onConnectionStateChange(gatt, status, newState);
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status)
            {
                if(status == BluetoothGatt.GATT_SUCCESS)
                {
                    for(BluetoothGattService servc :gatt.getServices())
                    {
                        if(pulse_oximeter_service_uuid.equals(servc.getUuid().toString()))
                        {
                            for(BluetoothGattCharacteristic item : servc.getCharacteristics())
                            {
                                if(item.getUuid().toString().equals(pulse_oximeter_characteristic_uuid))
                                {
                                    setCharacteristicNotification(gatt, item, true);
                                }
                            }
                        }
                    }
                }
                super.onServicesDiscovered(gatt, status);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
            {
                super.onCharacteristicRead(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
            {
                super.onCharacteristicWrite(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
            {
                final BluetoothGattCharacteristic character = characteristic;
                if(characteristic.getUuid().toString().equals(pulse_oximeter_characteristic_uuid))
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            int[] data = bleDataParser.pulse_oxi_converter(character.getValue());

                            if(data[0] != -10 || data[1] != -10 || data[2] != -10)
                            {
                                heart_rate.setText(String.format("%s BPM", String.valueOf(data[0])));
                                oxy_sat.setText(String.format("%s%%", String.valueOf(data[1])));
                                perfusion_index.setText(String.format("%s%%", String.valueOf(data[2] / 10.0)));
                                String text = data[0] + "," + data[1] + "," + data[2];
                                fileWriter.write(ioxy_file, text, ioxy_start_time, testing_screen.this);
                            }
                        }
                    });
                }
                super.onCharacteristicChanged(gatt, characteristic);
            }
        };
    };

    //Set BLE Notification Enabled
    private void setCharacteristicNotification(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic characteristic, boolean enable)
    {
        bluetoothGatt.setCharacteristicNotification(characteristic, enable);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
    }

    private void countdown_timer()
    {
        new CountDownTimer(300000, 1000)
        {
            @Override
            public void onTick(final long millisUntilFinished)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        timer.setText(String.format("%s", String.valueOf(millisUntilFinished / 1000) + 's'));
                        if(millisUntilFinished / 1000 >= 280)
                        {
                            instruction.setText(R.string.pour_drops_of_water);
                            instruction.setTextColor(ContextCompat.getColor(testing_screen.this, R.color.lightGreen));
                            if(soundService)
                            {
                                soundService = false;
                                startService(new Intent(testing_screen.this, beepSound.class));
                            }
                        }

                        else if(millisUntilFinished / 1000 < 280 && millisUntilFinished / 1000 >= 180)
                        {
                            instruction.setText(R.string.do_nothing);
                            instruction.setTextColor(ContextCompat.getColor(testing_screen.this, R.color.red));
                            if(!soundService)
                            {
                                soundService = true;
                                stopService(new Intent(testing_screen.this, beepSound.class));
                            }
                        }

                        else if(millisUntilFinished / 1000 < 180 && millisUntilFinished / 1000 >= 160)
                        {
                            instruction.setText(R.string.pour_drops_of_sample);
                            instruction.setTextColor(ContextCompat.getColor(testing_screen.this, R.color.lightGreen));
                            if(soundService)
                            {
                                soundService = false;
                                startService(new Intent(testing_screen.this, beepSound.class));
                            }
                        }

                        else
                        {
                            instruction.setText(R.string.do_nothing);
                            instruction.setTextColor(ContextCompat.getColor(testing_screen.this, R.color.red));
                            if(!soundService)
                            {
                                soundService = true;
                                stopService(new Intent(testing_screen.this, beepSound.class));
                            }
                        }
                    }
                });
            }

            @Override
            public void onFinish()
            {
                timer.setText("0s.");
                if(SenFlexGatt != null && bluetoothAdapter != null)
                {
                    SenFlexGatt.close();
                    SenFlexGatt = null;
                    connection_status_senflex.setText(R.string.disconnected);
                    connection_status_senflex.setTextColor(ContextCompat.getColor(testing_screen.this, R.color.red));
                }

                if(PulseoximeterGatt != null && bluetoothAdapter != null)
                {
                    PulseoximeterGatt.close();
                    PulseoximeterGatt = null;
                    connection_status_pulse.setText(R.string.disconnected);
                    connection_status_pulse.setTextColor(ContextCompat.getColor(testing_screen.this, R.color.red));
                }
            }
        }.start();
    }
}