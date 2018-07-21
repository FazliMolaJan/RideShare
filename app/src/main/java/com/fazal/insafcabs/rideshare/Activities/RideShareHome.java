package com.fazal.insafcabs.rideshare.Activities;

import android.content.Intent;
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
import com.fazal.insafcabs.rideshare.R;
import com.fazal.insafcabs.rideshare.Adapters.SearchRidesResultAdapter;
import com.fazal.insafcabs.rideshare.Utilities.CheckNetwork;
import com.fazal.insafcabs.rideshare.Utilities.GeneralUtils;
import com.fazal.insafcabs.rideshare.Utilities.SearchRidesResult_ModelClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RideShareHome extends AppCompatActivity {

    Button btnOfferRide, btnBookedRides, btnOfferedRides;

    // 016a65  color code of insaf cabs
    JSONArray mjson_array;

    RecyclerView rvSearchRidesResult;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    LinearLayoutManager llm;
    SweetAlertDialog pDialog;


    String strUrl = "search_ride_insaf.php";
    TextView tvNoData;

    SearchRidesResult_ModelClass searchRidesResult_modelClass;
    List<SearchRidesResult_ModelClass> data = new ArrayList<>();
    RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvNoData = (TextView) findViewById(R.id.no_data_available);
        tvNoData.setVisibility(View.INVISIBLE);

        btnBookedRides = (Button) findViewById(R.id.booked_rides);
        btnOfferRide = (Button) findViewById(R.id.offer_ride);
        btnOfferedRides = (Button) findViewById(R.id.offered_rides);

        btnOfferRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RideShareHome.this, OfferRide.class);
                startActivity(intent);
            }
        });
        btnBookedRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CheckNetwork.isInternetAvailable(getApplicationContext()))
                {
                    Intent intent = new Intent(RideShareHome.this, RideShareMyBookings.class);
                    startActivity(intent);
                   // MainActivity.this.finish();
                }
                else
                {
                    Toast.makeText(RideShareHome.this, "No Internet connection", Toast.LENGTH_SHORT).show();
                }


            }
        });

        btnOfferedRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CheckNetwork.isInternetAvailable(getApplicationContext()))
                {
                    Intent intent = new Intent(RideShareHome.this, RideShareOfferedRides.class);
                    startActivity(intent);
                    // MainActivity.this.finish();
                }
                else
                {
                    Toast.makeText(RideShareHome.this, "No Internet connection", Toast.LENGTH_SHORT).show();
                }


            }
        });
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        pDialog = new SweetAlertDialog(RideShareHome.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#179e99"));
        pDialog.setTitleText("Please Wait a while");
        pDialog.setCancelable(false);

        rvSearchRidesResult = (RecyclerView)findViewById(R.id.rv);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvSearchRidesResult.setLayoutManager(llm);
        rvSearchRidesResult.setHasFixedSize(true);
        mAdapter = new SearchRidesResultAdapter(getApplicationContext(), data);
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
                    Toast.makeText(RideShareHome.this, "Internet is not available", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(RideShareHome.this, "Internet is not available", Toast.LENGTH_SHORT).show();
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

                    searchRidesResult_modelClass = new SearchRidesResult_ModelClass();
                  /*      searchRidesResult_modelClass.driver_name = c.getString("name");
                        searchRidesResult_modelClass.mobile_no = c.getString("mobile_no");
                        searchRidesResult_modelClass.gender = c.getString("gender");*/
                    searchRidesResult_modelClass.ride_offer_id = c.getString("ride_offer_id");
                    searchRidesResult_modelClass.id = c.getString("id");
                    searchRidesResult_modelClass.user_id = c.getString("user_id");
                    searchRidesResult_modelClass.from_location = c.getString("from_location");
                    searchRidesResult_modelClass.to_location = c.getString("to_location");
                    searchRidesResult_modelClass.arrival_time = c.getString("arrival_time");
                    //            searchRidesResult_modelClass.pickup_time = c.getString("pick_time");
                    searchRidesResult_modelClass.wait_time = c.getString("wait_time");
                    searchRidesResult_modelClass.pick_point = c.getString("pick_point");
                    searchRidesResult_modelClass.drop_point = c.getString("drop_point");
                    searchRidesResult_modelClass.date = c.getString("date");
                    searchRidesResult_modelClass.time = c.getString("time");
                    searchRidesResult_modelClass.max_seats = c.getString("max_seats");
                    searchRidesResult_modelClass.charges_per_seat = c.getString("charges_per_seat");
                    searchRidesResult_modelClass.ac_availability = c.getString("ac_availability");
                    searchRidesResult_modelClass.heater_availability = c.getString("heater_availability");
                    searchRidesResult_modelClass.description = c.getString("description");
                    searchRidesResult_modelClass.driver_name = c.getString("name");
                    searchRidesResult_modelClass.mobile_no = c.getString("mobile_no");
                    searchRidesResult_modelClass.gender = c.getString("gender");
                    searchRidesResult_modelClass.email = c.getString("email");
                    searchRidesResult_modelClass.profile_img = c.getString("profile_img");
                    searchRidesResult_modelClass.biography = c.getString("biography");
                    searchRidesResult_modelClass.car_name = c.getString("car_name");
                    searchRidesResult_modelClass.car_model = c.getString("car_model");
                    searchRidesResult_modelClass.car_number = c.getString("car_number");
                    searchRidesResult_modelClass.car_color = c.getString("car_color");
                    searchRidesResult_modelClass.car_image = c.getString("car_image");

                    data.add(searchRidesResult_modelClass);
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
