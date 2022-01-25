package com.example.expensetracker.ui.trips;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.expensetracker.R;
import com.example.expensetracker.model.Trip;
import com.example.expensetracker.utils.BaseApp;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.HubViewHolder> {
    private List<Trip> mTrips;
    private OnClickTripListener onClickHubListener;

    public TripAdapter(List<Trip> mTrips, OnClickTripListener onClickHubListener) {
        this.mTrips = mTrips;
        this.onClickHubListener = onClickHubListener;
    }

    public List<Trip> getTrips() {
        return mTrips;
    }

    public void updateRecyclerView(List<Trip> hubList){
        mTrips.clear();
        mTrips.addAll(hubList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_trip, parent, false);
        return new HubViewHolder(view, onClickHubListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HubViewHolder holder, int position) {
        Trip trip = mTrips.get(position);
        holder.groupName.setText(trip.getName());
        holder.nrHubParticipants.setText(trip.getGroupSize().toString());
        if ("".equals(trip.getAvatarUri())) {
            Glide.with(BaseApp.context)
                    .load(trip.getAvatarUri())
                    .centerCrop()
                    .placeholder(R.drawable.progress_animation)
                    .into(holder.hubAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    public class HubViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView groupName;
        public TextView nrHubParticipants;
        OnClickTripListener onHubListener;
        public ImageView hubAvatar;

        public HubViewHolder(@NonNull View itemView, OnClickTripListener onHubListener) {
            super(itemView);
            this.hubAvatar = itemView.findViewById(R.id.vh_imageView);
            this.groupName = itemView.findViewById(R.id.textView_hubName);
            this.nrHubParticipants = itemView.findViewById(R.id.textView_nrHubParticipants);
            this.onHubListener = onHubListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Trip trip = mTrips.get(getAdapterPosition());
            onHubListener.onHubClick(trip.getId(), trip.getName());
        }

    }

    public interface OnClickTripListener {
        void onHubClick(Long hubId, String hubName);
    }

}
