package com.fazal.insafcabs.rideshare.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fazal.insafcabs.rideshare.R;
import com.fazal.insafcabs.rideshare.Utilities.GeneralUtils;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RideDetails extends AppCompatActivity {
    Bundle args;
    Button btnCall, btnSMS, btnRoute, btnBookSeat, btnContinue;
    TextView tvTime, tvPickup, tvDropOff, tvTotalSeats, carOwnerName;
    MaterialSpinner spNoOfSeats;
    Dialog dialog;
    RequestQueue mRequestQueue;
    SweetAlertDialog pDialog;


    String strMobileNo, strNoOfSeats, strSeatsBooked="1", strUserId, strRideOfferId,
            strUrl = GeneralUtils.MAIN_URL+"book_seat.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_rides_detailed);

        tvTime = (TextView) findViewById(R.id.tv_ride_time);
        tvPickup = (TextView) findViewById(R.id.tv_pickup_location);
        tvDropOff = (TextView) findViewById(R.id.tv_drop_location);
        tvTotalSeats = (TextView) findViewById(R.id.tv_seats_available);
        carOwnerName = (TextView) findViewById(R.id.tv_name);

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        pDialog = new SweetAlertDialog(RideDetails.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#179e99"));
        pDialog.setTitleText("booking your seat");
        pDialog.setCancelable(false);

        args = getIntent().getExtras();

        strMobileNo = args.getString("mobile_no");
        strRideOfferId = args.getString("ride_offer_id");
        strUserId = args.getString("user_id");

        strNoOfSeats = args.getString("max_seats");
        tvTime.setText(args.getString("time"));
        tvPickup.setText(args.getString("pick_point"));
        tvDropOff.setText(args.getString("drop_point"));
        tvTotalSeats.setText(strNoOfSeats + " Seats Available");
        carOwnerName.setText(args.getString("driver_name"));

        btnCall = (Button) findViewById(R.id.btn_call);
        btnSMS = (Button) findViewById(R.id.btn_sms);
        btnRoute = (Button) findViewById(R.id.btn_route);
        btnBookSeat = (Button) findViewById(R.id.btn_book_seat);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                getApplicationContext().checkSelfPermission(Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED
                ) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.SEND_SMS}, 1);
        }

        btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", strMobileNo, null)));
             /* Intent sms = new Intent(Intent.ACTION_VIEW);
                sms.putExtra("address", strMobileNo);
                sms.putExtra("sms_body", "text");
                sms.setType("vnd.android-dir/mms-sms");
                startActivity(sms);*/
        }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + strMobileNo));
                if (ActivityCompat.checkSelfPermission
                        (getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            }
        });

        btnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=34.029339, 71.474926"); // Attach your destination lat lng here
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                else
                {
                    Toast.makeText(RideDetails.this, "Google Map is not installed in your phone", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBookSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(RideDetails.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.book_seat_design);
                dialog.setCancelable(true);
                spNoOfSeats = (MaterialSpinner) dialog.findViewById(R.id.sp_no_of_seats);

                if (strNoOfSeats.equals("1"))
                {
                    spNoOfSeats.setItems("1");
                } else if (strNoOfSeats.equals("2"))
                {
                    spNoOfSeats.setItems("1", "2");
                }  else if (strNoOfSeats.equals("3"))
                {
                    spNoOfSeats.setItems("1", "2", "3");
                }  else if (strNoOfSeats.equals("4"))
                {
                    spNoOfSeats.setItems("1", "2", "3", "4");
                }
                else {
                    spNoOfSeats.setItems("1", "2", "3", "4");
                }
                spNoOfSeats.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                        // Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                        strSeatsBooked = String.valueOf(item);
                        spNoOfSeats.setText(strSeatsBooked);
                        Log.d("zma spinner id", strNoOfSeats);
                    }
                });
                btnContinue = (Button) dialog.findViewById(R.id.btn_confirm_book_seat);
                btnContinue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bookSeatApi();
                    }
                });

                dialog.show();
            }
        });
    }

    private void bookSeatApi()
    {
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, strUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("zma response", String.valueOf(response));

                        try {
                            JSONObject json = new JSONObject(response);

                            Boolean status = json.getBoolean("status");
                            String message =  json.getString("message");

                            if (message.equals("seat booked successfully"))
                            {
                                pDialog.dismiss();

                                new SweetAlertDialog(RideDetails.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Good Job...")
                                        .setContentText("Thanks for Registering.\nPlease in touch with car owner for a better trip!")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                Intent intent = new Intent(RideDetails.this, RideShareHome.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .show();
                            }
                            else if (message.equals("seat not booked"))
                            {
                                pDialog.dismiss();
                                new SweetAlertDialog(RideDetails.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Please connect to internet and then Try agian!")
                                        .show();
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
                        Toast.makeText(getApplicationContext(), "Error in Network", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();

                    }
                }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("no_of_seats",strSeatsBooked);
                params.put("user_id", strUserId);
                params.put("ride_offer_id", strRideOfferId);
                return params;
            }};

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }
}
