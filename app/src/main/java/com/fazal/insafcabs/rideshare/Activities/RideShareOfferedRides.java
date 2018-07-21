package com.fazal.insafcabs.rideshare.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fazal.insafcabs.rideshare.Adapters.OfferedRidesAdapter;
import com.fazal.insafcabs.rideshare.R;
import com.fazal.insafcabs.rideshare.Utilities.CheckNetwork;
import com.fazal.insafcabs.rideshare.Utilities.GeneralUtils;
import com.fazal.insafcabs.rideshare.Utilities.OfferedRidesModelClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RideShareOfferedRides extends AppCompatActivity {

    Button btnOfferRide, btnBookRide;

    // 016a65  color code of insaf cabs
    JSONArray mjson_array;

    RecyclerView rvSearchRidesResult;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    LinearLayoutManager llm;
    SweetAlertDialog pDialog;


    String strUrl = "search_ride_insaf.php";
    TextView tvNoData;

    com.fazal.insafcabs.rideshare.Utilities.OfferedRidesModelClass OfferedRidesModelClass;
    List<OfferedRidesModelClass> data = new ArrayList<>();
    RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_offered_rides);

        tvNoData = (TextView) findViewById(R.id.no_data_available);
        tvNoData.setVisibility(View.INVISIBLE);

        btnBookRide = (Button) findViewById(R.id.book_ride);
       // btnOfferRide = (Button) findViewById(R.id.offer_ride);

       /* btnOfferRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyBookings.this, OfferRide.class);
                startActivity(intent);
            }
        });*/
        btnBookRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CheckNetwork.isInternetAvailable(getApplicationContext()))
                {
                   /* Intent intent = new Intent(MyBookings.this, MainActivity.class);
                    startActivity(intent);*/
                    RideShareOfferedRides.this.finish();
                }
                else
                {
                    Toast.makeText(RideShareOfferedRides.this, "No Internet connection", Toast.LENGTH_SHORT).show();
                }


            }
        });
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        pDialog = new SweetAlertDialog(RideShareOfferedRides.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#179e99"));
        pDialog.setTitleText("Please Wait a while");
        pDialog.setCancelable(false);

        rvSearchRidesResult = (RecyclerView)findViewById(R.id.rv);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvSearchRidesResult.setLayoutManager(llm);
        rvSearchRidesResult.setHasFixedSize(true);
        mAdapter = new OfferedRidesAdapter(RideShareOfferedRides.this, data);
        rvSearchRidesResult.setAdapter(mAdapter);

        final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                if (CheckNetwork.isInternetAvailable(getApplicationContext()))
                {
                    data.clear();
                    mAdapter.notifyDataSetChanged();
                    apiCall();
                }
                else
                {
                    Toast.makeText(RideShareOfferedRides.this, "Internet is not available", Toast.LENGTH_SHORT).show();
                }
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if (CheckNetwork.isInternetAvailable(getApplicationContext()))
        {
            data.clear();
            mAdapter.notifyDataSetChanged();
            apiCall();
        }
        else
        {
            Toast.makeText(RideShareOfferedRides.this, "Internet is not available", Toast.LENGTH_SHORT).show();
        }

    }
    private void apiCall()
    {
        pDialog.show();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GeneralUtils.MAIN_URL+"search_ride_insaf.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //   Toast.makeText(mContext,response,Toast.LENGTH_LONG).show();
                        try {
                            pDialog.dismiss();
                            showJSON(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                // pass current lat lng here to show nearby rides in 10 km around, it will be handled from server side
              /*  params.put("lat","value");
                params.put("lng", "value");*/
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response) throws JSONException {

        // parsing server response.
        JSONObject json = new JSONObject(response);
        String message =  json.getString("message");

        if (json.has("data")) {


            if (message.equals("Data Available"))
            {

                mjson_array = json.getJSONArray("data");

                for (int i = 0; i < mjson_array.length(); i++) {
                    JSONObject c = mjson_array.getJSONObject(i);

                    OfferedRidesModelClass = new OfferedRidesModelClass();
                  /*      OfferedRidesModelClass.driver_name = c.getString("name");
                        OfferedRidesModelClass.mobile_no = c.getString("mobile_no");
                        OfferedRidesModelClass.gender = c.getString("gender");*/
                    OfferedRidesModelClass.ride_offer_id = c.getString("ride_offer_id");
                    OfferedRidesModelClass.id = c.getString("id");
                    OfferedRidesModelClass.user_id = c.getString("user_id");
                    OfferedRidesModelClass.from_location = c.getString("from_location");
                    OfferedRidesModelClass.to_location = c.getString("to_location");
                    OfferedRidesModelClass.arrival_time = c.getString("arrival_time");
                    //            OfferedRidesModelClass.pickup_time = c.getString("pick_time");
                    OfferedRidesModelClass.wait_time = c.getString("wait_time");
                    OfferedRidesModelClass.pick_point = c.getString("pick_point");
                    OfferedRidesModelClass.drop_point = c.getString("drop_point");
                    OfferedRidesModelClass.date = c.getString("date");
                    OfferedRidesModelClass.time = c.getString("time");
                    OfferedRidesModelClass.max_seats = c.getString("max_seats");
                    OfferedRidesModelClass.charges_per_seat = c.getString("charges_per_seat");
                    OfferedRidesModelClass.ac_availability = c.getString("ac_availability");
                    OfferedRidesModelClass.heater_availability = c.getString("heater_availability");
                    OfferedRidesModelClass.description = c.getString("description");
                    OfferedRidesModelClass.driver_name = c.getString("name");
                    OfferedRidesModelClass.mobile_no = c.getString("mobile_no");
                    OfferedRidesModelClass.gender = c.getString("gender");
                    OfferedRidesModelClass.email = c.getString("email");
                    OfferedRidesModelClass.profile_img = c.getString("profile_img");
                    OfferedRidesModelClass.biography = c.getString("biography");
                    OfferedRidesModelClass.car_name = c.getString("car_name");
                    OfferedRidesModelClass.car_model = c.getString("car_model");
                    OfferedRidesModelClass.car_number = c.getString("car_number");
                    OfferedRidesModelClass.car_color = c.getString("car_color");
                    OfferedRidesModelClass.car_image = c.getString("car_image");

                    data.add(OfferedRidesModelClass);
                }
            }
            else{
                tvNoData.setText("No rides available");
                tvNoData.setVisibility(View.VISIBLE);
                //  Toast.makeText(getActivity(), "Data not available", Toast.LENGTH_SHORT).show();
            }
            mAdapter.notifyDataSetChanged();
        }
    }
}
