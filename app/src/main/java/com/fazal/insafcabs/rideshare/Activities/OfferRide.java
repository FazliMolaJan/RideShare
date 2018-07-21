package com.fazal.insafcabs.rideshare.Activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fazal.insafcabs.rideshare.R;
import com.fazal.insafcabs.rideshare.Utilities.GeneralUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OfferRide extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();
    String strMobileNo;
    View view;
    Button btnOfferRide;
    EditText setTime;
    CheckBox checkBox;
    EditText setDate;
    /*ImageView btnAddSeat;
    ImageView btnMinusSeat;*/
    LatLng latLngStart;
    EditText etPickUpPoint, etDropPoint, etTotalSeats;
    String strTotalSeats, strTime, strWaitTime, strPickUp, strDropOff,
             strUrl= GeneralUtils.MAIN_URL+"ride_offer_new.php"; // Need to change it to actual
    int finalSeats =4;

    RequestQueue mRequestQueue;
    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride);

        // methods to handle the code cleanly.
        dataInitialization();
        //totalSeatsSetting();
        btnClicksHandling();

    }

    private void dataInitialization()
    {
        setTime = (EditText )  findViewById(R.id.btn_select_time);
     /*   btnAddSeat = (ImageView)  findViewById(R.id.add_seat);
        btnMinusSeat = (ImageView)  findViewById(R.id.minus_seat);*/
        etTotalSeats = (EditText)  findViewById(R.id.no_of_seats);
        etPickUpPoint = (EditText)  findViewById(R.id.pick_up_location);
        etDropPoint = (EditText)  findViewById(R.id.drop_off_location);
        btnOfferRide = (Button)  findViewById(R.id.btn_offer_ride);
        checkBox = (CheckBox) findViewById(R.id.checkbox);

        setTime.setFocusable(false);
        setTime.setClickable(true);

        etPickUpPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findPlace(view);
            }
        });

        pDialog = new SweetAlertDialog(OfferRide.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#179e99"));
        pDialog.setTitleText("updating your ride information");
        pDialog.setCancelable(false);

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        // Initialize the TextView value for total seats available
        strTotalSeats = finalSeats +"";
        etTotalSeats.setText(String.valueOf(finalSeats));
    }
    private void btnClicksHandling()
    {
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(OfferRide.this, AlertDialog.THEME_HOLO_LIGHT, onTimeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),false).show();
            }
        });
        btnOfferRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked())
                {
                    formValidation();
                }
                else
                {
                    Toast.makeText(OfferRide.this, "Please Accept terms and conditions", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void formValidation()
    {
        strTotalSeats = String.valueOf(finalSeats);
        strTime = setTime.getText().toString().trim();
        strPickUp = etPickUpPoint.getText().toString().trim();
        strDropOff = etDropPoint.getText().toString().trim();

        if (strPickUp.equals(""))
        {
            etPickUpPoint.requestFocus();
            Toast.makeText(getApplicationContext(), "Please enter your Pick up location", Toast.LENGTH_SHORT).show();
        } else if (strDropOff.equals(""))
        {
            etDropPoint.requestFocus();
            Toast.makeText(getApplicationContext(), "Please enter your destination polling station ", Toast.LENGTH_SHORT).show();
        } else if (strTime.equals(""))
        {
            setTime.requestFocus();
            Toast.makeText(getApplicationContext(), "Please enter your travelling time", Toast.LENGTH_SHORT).show();
        }
        else if (etTotalSeats.getText().toString().equals(""))
        {
            setTime.requestFocus();
            Toast.makeText(getApplicationContext(), "Please enter total seats", Toast.LENGTH_SHORT).show();
        }
        else if (Integer.parseInt(etTotalSeats.getText().toString())>14)
        {
            Toast.makeText(OfferRide.this, "You can offer max 14 seats", Toast.LENGTH_SHORT).show();
        }else
        {
            pDialog.show();
            api_call();
        }
    }
    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            setTime.setText(hourOfDay + ":" + minute);
        }
    };

  /*  private void totalSeatsSetting()
    {
        btnAddSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMinusSeat.setEnabled(true);
                    finalSeats += 1;
                    strTotalSeats = finalSeats + " Seats Available";
                    etTotalSeats.setText(String.valueOf(finalSeats));
            }
        });
        btnMinusSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnAddSeat.setEnabled(true);
                if (String.valueOf(finalSeats).equals("1"))
                {
                    btnMinusSeat.setEnabled(false);
                }
                else
                {
                    finalSeats -=1;
                    strTotalSeats = finalSeats + " Seats Available";
                    etTotalSeats.setText(String.valueOf(finalSeats));
                }
            }
        });
    }*/
    private void api_call() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GeneralUtils.MAIN_URL+"ride_offer_new.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("zma response", String.valueOf(response));
                        pDialog.dismiss();
                        try {
                            JSONObject json = new JSONObject(response);

                            Boolean status = json.getBoolean("status");
                            String message =  json.getString("message");
                            Log.e("response", response.toString()+" url "+GeneralUtils.MAIN_URL+"ride_offer_new.php");


                            if (message.equals("ride offered successful"))
                            {
                                new SweetAlertDialog(OfferRide.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Good Job...")
                                        .setContentText("Ride offered successfully!")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                Intent intent = new Intent(getApplicationContext(), RideShareHome.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .show();
                            } else if (message.equals("ride not offered"))
                            {
                                Toast.makeText(OfferRide.this, "Ride Not Offered", Toast.LENGTH_SHORT).show();
                            } else if (message.equals("ride already offered"))
                            {
                                Toast.makeText(OfferRide.this, "You have already offered a ride on Election day", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(OfferRide.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                            // Toast.makeText(MainActivity.this, "Status: "+status+"\nMessage: "+message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("fail", "Failed\n"+String.valueOf(error));
                        pDialog.dismiss();

                    }
                }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pickup_location", strPickUp);
                params.put("drop_location", strDropOff);
                params.put("pickup_lat_lng", latLngStart.toString()+"");
                params.put("time", strTime);
                params.put("no_of_seats",strTotalSeats);
                params.put("mobile_no", "0300123456");  // please insert user mobile no variable here


                // For testing purpose
                Log.e("testing",strTime+strTotalSeats+" "+latLngStart.toString());
                return params;
            }};

        /*stringRequest.setRetryPolicy(new DefaultRetryPolicy(200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);*/
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    public void findPlace(View view) {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .setCountry("PK")
                    .build();

            Intent intent =
                    new PlaceAutocomplete
                            .IntentBuilder(PlaceAutocomplete.RESULT_ERROR)
                            .setBoundsBias(new LatLngBounds(
                                    new LatLng(24.3539, 61.74681),
                                    new LatLng(35.91869 , 75.16683)))
                            // .setFilter(typeFilter)
                            .build(OfferRide.this);
            startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }
    // A place has been received; use requestCode to track the request.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // retrive the data by using getPlace() method.
                Place place = PlaceAutocomplete.getPlace(getApplicationContext(), data);
                latLngStart = place.getLatLng();
                GeneralUtils.lat_lng = latLngStart.toString();
                //  Log.e("Tag", "Place: " + place.getAddress() + "" +place.getPhoneNumber()  + "" + end);
                etPickUpPoint.setText(place.getName().toString());
                // use place.getName() to only picking the name

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getApplicationContext(), data);
                // TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
