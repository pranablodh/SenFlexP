package com.orela.senflexp.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.snackbar.Snackbar;
import com.orela.senflexp.R;
import com.orela.senflexp.fileManagement.gZip;
import com.orela.senflexp.inputValidator.inputValidator;
import com.orela.senflexp.network.api;
import com.orela.senflexp.network.networkManager;
import com.orela.senflexp.sharedPreference.sharedPreference;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.orela.senflexp.activities.login.REQUEST_CHECK_SETTINGS;

public class new_test extends AppCompatActivity
{
    private static final String SHOWCASE_ID = "New Test";

    //Image Picker Elements
    private static final int PICK_FROM_CAMERA = 0;
    private String FinalEncodedImage = api.image;

    //UI Elements
    private Button next;
    private ImageView camera;
    private CircleImageView image;
    private CardView cardView;
    private EditText name;
    private EditText test_id;
    private EditText address;
    private EditText dob;
    private Spinner sex;
    private Spinner specimen;
    private EditText mobile;
    private EditText email;
    private EditText last_test_id;

    //Dialog Box Element
    private Dialog otpDialog;
    private EditText otp;
    private TextView resend_text;
    private Button closeButton;
    private Button otpSubmit;

    //Variables for Permission
    private static final int INITIAL_REQUEST = 1337;
    private String[] Permissions = {android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE};

    //Test File Name Variables
    private String senflex_file = "";
    private String ioxy_file = "";

    //Mobile Verification Flag
    private String mobileFlag = "N";

    //Spinner Item
    private String spinnerSelection = "";

    //Specimen Type Array
    private final static String[] specimen_type = {"Nothing", "Nasal Swab", "Throat Swab", "Throat Swab, Nasal Swab"};


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_test);
        networkManager.getInstance(this);

        //Hiding Action Bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //UI Elements
        next = (Button) findViewById(R.id.next);
        camera = (ImageView) findViewById(R.id.camera);
        image = (CircleImageView) findViewById(R.id.image);
        cardView = (CardView) findViewById(R.id.cardView);
        name = (EditText) findViewById(R.id.name);
        test_id = (EditText) findViewById(R.id.test_id);
        last_test_id = (EditText) findViewById(R.id.last_test_id);
        address = (EditText) findViewById(R.id.address);
        dob = (EditText) findViewById(R.id.dob);
        sex = (Spinner) findViewById(R.id.sex);
        specimen = (Spinner) findViewById(R.id.specimen);
        mobile = (EditText) findViewById(R.id.mobile);
        email = (EditText) findViewById(R.id.email);

        show_show_case_view();

        //On Click Listener
        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!validate_name() | !validate_test_id() | !validate_address() | !validate_dob() | !validate_gender(v)
                    | !validate_mobile() | !validate_email() | !validate_image(v) | !validate_specimen(v))
                {
                    return;
                }

                if(!canAccessCamera() | !canReadExternalStorage())
                {
                    requestPermissions(Permissions, INITIAL_REQUEST);
                    return;
                }

                if(!statusCheck())
                {
                    return;
                }

                senflex_file = "SenFlexP_" + test_id.getText().toString() + "_" + System.currentTimeMillis();
                ioxy_file = "iOxy_" + test_id.getText().toString() + "_" + System.currentTimeMillis();
                showOTPDialog();
            }
        });

        dob.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pickDate();
            }
        });

        camera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!canAccessCamera() | !canReadExternalStorage())
                {
                    requestPermissions(Permissions, INITIAL_REQUEST);
                    return;
                }
                pick_image_from_camera();
            }
        });

        specimen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                spinnerSelection = specimen_type[position];
                Log.d("SPECIMEN_TYPE", spinnerSelection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        go_to_landing_page();
    }

    //Input Validator
    private boolean validate_name()
    {
        if(name.getText().length() == 0)
        {
            name.setError("Please Input a Valid Name.");
            return false;
        }

        else
        {
            name.setError(null);
            return true;
        }
    }

    private Boolean validate_test_id()
    {
        if(test_id.getText().toString().length() == 0)
        {
            test_id.setError("Test ID Cannot be Blank.");
            return false;
        }

        else
        {
            test_id.setError(null);
            return true;
        }
    }

    private Boolean validate_address()
    {
        if(address.getText().toString().length() == 0)
        {
            address.setError("Please Input a Valid Address.");
            return false;
        }

        else
        {
            address.setError(null);
            return true;
        }
    }

    private Boolean validate_dob()
    {
        if(!inputValidator.checkDate(dob.getText().toString()))
        {
            dob.setError("Please Input a Valid Date of Birth.");
            return false;
        }

        else
        {
            dob.setError(null);
            return true;
        }
    }

    private Boolean validate_gender(View v)
    {
        if(!inputValidator.checkNoWhiteSpace(sex.getSelectedItem().toString()))
        {
            ((TextView)sex.getSelectedView()).setError("x");
            Snackbar.make(v, "Please Select a Sex from Dropdown.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        else
        {
            ((TextView)sex.getSelectedView()).setError(null);
            return true;
        }
    }

    private Boolean validate_specimen(View v)
    {
        if(specimen.getSelectedItem().toString().equalsIgnoreCase("Select Specimen Type"))
        {
            ((TextView)specimen.getSelectedView()).setError("x");
            Snackbar.make(v, "Please Select a Specimen Type from Dropdown.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        else
        {
            ((TextView)specimen.getSelectedView()).setError(null);
            return true;
        }
    }

    private Boolean validate_mobile()
    {
        if(!inputValidator.checkMobileNumber(mobile.getText().toString()))
        {
            mobile.setError("Invalid Mobile Number.");
            return false;
        }

        else
        {
            mobile.setError(null);
            return true;
        }
    }

    private Boolean validate_email()
    {
        if(!inputValidator.checkEmail(email.getText().toString()))
        {
            email.setError("Invalid Email ID.");
            return false;
        }

        else
        {
            email.setError(null);
            return true;
        }
    }

    private Boolean validate_image(View v)
    {
        if(FinalEncodedImage.isEmpty())
        {
            Snackbar.make(v, "Please Capture an Image of the Person.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        else
        {
            return true;
        }
    }

    //Storing Data into Shared Preference
    private void store_data()
    {
        sharedPreference.deleteTestData(new_test.this);
        sharedPreference.storeTestParameters(name.getText().toString(), test_id.getText().toString(),
                address.getText().toString(), dob.getText().toString(),
                sex.getSelectedItem().toString(), mobile.getText().toString(),
                email.getText().toString(), FinalEncodedImage, senflex_file,
                ioxy_file, getCurrentDate(), last_test_id.getText().toString(), mobileFlag, getCurrentDate(),
                spinnerSelection, new_test.this);
        sharedPreference.storeDeviceID("SenP-0001", new_test.this);
        Log.d("SPECIMEN_TYPE_DATA", spinnerSelection);
        go_to_testing_page();
    }

    private String getCurrentDate()
    {
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String date = df.format(Calendar.getInstance().getTime());
        Log.d("DATE_DATA", date);
        return date;
    }

    //Checking Permission
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canAccessCamera()
    {
        return(hasPermission(Manifest.permission.CAMERA));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canReadExternalStorage()
    {
        return(hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasPermission(String perm)
    {
        return(PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
    }

    private void go_to_landing_page()
    {
        Intent go = new Intent(new_test.this, landing_page.class);
        startActivity(go);
        finish();
    }

    private void go_to_testing_page()
    {
        Intent go = new Intent(new_test.this, testing_screen.class);
        go.putExtra("test_id", test_id.getText().toString());
        go.putExtra("senflex", senflex_file);
        go.putExtra("ioxy", ioxy_file);
        startActivity(go);
        finish();
    }

    private void go_to_submit_result()
    {
        Intent go = new Intent(new_test.this, com.orela.senflexp.activities.submitTestResult.class);
        startActivity(go);
        finish();
    }

    private void showOTPDialog()
    {
        otpDialog = new Dialog(new_test.this);
        otpDialog.setContentView(R.layout.dialog_otp);
        otp = (EditText) otpDialog.findViewById(R.id.otp);
        closeButton = (Button) otpDialog.findViewById(R.id.closeButton);
        resend_text = (TextView) otpDialog.findViewById(R.id.resend_text);
        otpSubmit = (Button) otpDialog.findViewById(R.id.otpSubmit);

        closeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mobileFlag = "N";
                otpDialog.dismiss();
                store_data();
                //go_to_submit_result();
            }
        });

        otpSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mobileFlag = "Y";
                store_data();
                //go_to_testing_page();
                //go_to_submit_result();
            }
        });

        resend_text.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                resend_text.setClickable(false);
                new CountDownTimer(30000, 1000)
                {
                    @Override
                    public void onTick(long millisUntilFinished)
                    {
                        resend_text.setText(String.format("Resend OTP in %ss", String.valueOf(millisUntilFinished / 1000)));
                    }

                    @Override
                    public void onFinish()
                    {
                        resend_text.setText(R.string.resend_otp);
                        resend_text.setClickable(true);
                    }
                }.start();
            }
        });

        otpDialog.setCancelable(false);
        Objects.requireNonNull(otpDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        otpDialog.show();
    }

    //Location Service Checking
    public boolean statusCheck()
    {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        assert manager != null;
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            //buildAlertMessageNoGps();
            displayLocationSettingsRequest(new_test.this);
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
                            status.startResolutionForResult(new_test.this, REQUEST_CHECK_SETTINGS);
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

    //Pick Image From Camera
    private void pick_image_from_camera()
    {
        Intent intentCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intentCamera, PICK_FROM_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK)
        {
            try
            {
                Bitmap mphoto = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                assert mphoto != null;
                image.setImageDrawable(null);
                image.setBackground(null);
                image.setImageBitmap(mphoto);
                FinalEncodedImage = base64Encoding(mphoto);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Encoding Image Into Base64
    private String base64Encoding(Bitmap mphoto)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        assert mphoto != null;
        mphoto.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void show_show_case_view()
    {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(100);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);
        sequence.setConfig(config);
        sequence.addSequenceItem(cardView, "Input All the Details of the Patient.", "Got It");
        sequence.addSequenceItem(camera, "Press Here to Take of Photo of the Patient.", "Got It");
        sequence.addSequenceItem(image, "Photo Taken by Camera Will Be Shown Here.", "Got It");
        sequence.start();
    }

    //Date Picker
    private void pickDate()
    {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(new_test.this, new DatePickerDialog.OnDateSetListener()
        {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday)
            {
                String day = "";
                String month = "";
                selectedmonth = selectedmonth + 1;

                if(selectedday < 10)
                {
                    day = "0" + selectedday;
                }

                else
                {
                    day = String.valueOf(selectedday);
                }

                if(selectedmonth < 10)
                {
                    month = "0" + selectedmonth;
                }

                else
                {
                    month = String.valueOf(selectedmonth);
                }

                String Date = month + "/" + day + "/" + selectedyear;
                dob.setText(Date);

            }
        },mYear, mMonth, mDay);
        mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        mDatePicker.setTitle("Select date");
        mDatePicker.show();
    }
}
