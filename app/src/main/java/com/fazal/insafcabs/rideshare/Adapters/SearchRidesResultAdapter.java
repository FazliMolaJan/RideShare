package com.fazal.insafcabs.rideshare.Adapters;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fazal.insafcabs.rideshare.Activities.RideDetails;
import com.fazal.insafcabs.rideshare.R;
import com.fazal.insafcabs.rideshare.Utilities.SearchRidesResult_ModelClass;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SearchRidesResultAdapter extends RecyclerView.Adapter<SearchRidesResultAdapter.ViewHolder> {
    List<SearchRidesResult_ModelClass> recentSearchesModelClasses = new ArrayList<>();
    Context context;
    Fragment fragment;
    String dropPoint;
    Bundle bundle = new Bundle();

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        CardView cv;
        TextView tvPickupLocation, tvDropOffLocation,tvDriverName;


        public ViewHolder(View v) {
            super(v);
            cv = (CardView)itemView.findViewById(R.id.cv_search_ride_result);
            tvPickupLocation = (TextView)itemView.findViewById(R.id.cv_pickup_location);
            tvDropOffLocation = (TextView)itemView.findViewById(R.id.cv_drop_location);
            tvDriverName = (TextView)itemView.findViewById(R.id.cv_name);

    /*        cv = (CardView)itemView.findViewById(R.id.cv_recent_searches);
            personName = (TextView)itemView.findViewById(R.id.tv_recent_searches_from);
            personAge = (TextView)itemView.findViewById(R.id.tv_recent_searches_to);*/
        }
    }



    // Provide a suitable constructor (depends on the kind of dataset)
    public SearchRidesResultAdapter(Context context, List<SearchRidesResult_ModelClass> recentSearchesModelClasses) {
        this.context = context;
        this.recentSearchesModelClasses =  recentSearchesModelClasses;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_ride_result_design, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        String string = recentSearchesModelClasses.get(position).from_location;

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // passing the values from server response to next activity
                Intent intent = new Intent(context, RideDetails.class);
                bundle.putString("driver_name", recentSearchesModelClasses.get(position).driver_name);
                bundle.putString("mobile_no",recentSearchesModelClasses.get(position).mobile_no);
                bundle.putString("from_location", recentSearchesModelClasses.get(position).from_location);
                bundle.putString("to_location", recentSearchesModelClasses.get(position).to_location);
                bundle.putString("date", recentSearchesModelClasses.get(position).date);

                // split date and time
                String time = recentSearchesModelClasses.get(position).time;
                StringTokenizer tokens = new StringTokenizer(time, ":");
                String time1 = tokens.nextToken();
                String time2 = tokens.nextToken();
                String combinedDate = time1 + ":" + time2;
                bundle.putString("time", combinedDate);

                bundle.putString("pick_point", recentSearchesModelClasses.get(position).pick_point);
                bundle.putString("drop_point", recentSearchesModelClasses.get(position).drop_point);
                bundle.putString("max_seats", recentSearchesModelClasses.get(position).max_seats);
                bundle.putString("fare_per_seat", recentSearchesModelClasses.get(position).charges_per_seat);
                bundle.putString("wait_time", recentSearchesModelClasses.get(position).wait_time);
                bundle.putString("profile_image", recentSearchesModelClasses.get(position).profile_img);
                bundle.putString("car_model", recentSearchesModelClasses.get(position).car_model);
                bundle.putString("car_color", recentSearchesModelClasses.get(position).car_color);
                bundle.putString("car_number", recentSearchesModelClasses.get(position).car_number);
                bundle.putString("car_image", recentSearchesModelClasses.get(position).car_image);
                bundle.putString("ride_offer_id", recentSearchesModelClasses.get(position).ride_offer_id);
                Log.e("ride_offer_id",recentSearchesModelClasses.get(position).ride_offer_id);
                bundle.putString("user_id", recentSearchesModelClasses.get(position).user_id);
                bundle.putString("description", recentSearchesModelClasses.get(position).description);
                bundle.putString("biography", recentSearchesModelClasses.get(position).biography);

                intent.putExtras(bundle);
                context.startActivity(intent);

            }
        });
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.tvDriverName.setText(recentSearchesModelClasses.get(position).driver_name);
        holder.tvPickupLocation.setText(recentSearchesModelClasses.get(position).pick_point);
        holder.tvDropOffLocation.setText(recentSearchesModelClasses.get(position).drop_point);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return recentSearchesModelClasses.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}