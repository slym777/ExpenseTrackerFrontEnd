package com.example.expensetracker.ui.statistics;

import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.ViewChooseTripBinding;
import com.example.expensetracker.databinding.ViewTripBinding;
import com.example.expensetracker.model.Trip;
import com.example.expensetracker.utils.BaseApp;

import java.util.List;

public class ChooseTripAdapter extends RecyclerView.Adapter<ChooseTripAdapter.TripViewHolder> {
    private List<ChooseTripRow> mTrips;
    private OnClickTripListener onClickTripListener;

    public ChooseTripAdapter(List<ChooseTripRow> mTrips, OnClickTripListener onClickHubListener) {
        this.mTrips = mTrips;
        this.onClickTripListener = onClickHubListener;
    }

    public List<ChooseTripRow> getTrips() {
        return mTrips;
    }

    public void updateRecyclerView(List<ChooseTripRow> hubList){
        mTrips.clear();
        mTrips.addAll(hubList);
        notifyDataSetChanged();
    }


    public void setSelectedFriend(Long tripId){
        for( ChooseTripRow trip : mTrips){
            trip.setChecked(false);
            if (trip.getId().equals(tripId))
                trip.setChecked(true);
        }
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    public ChooseTripRow getSelectedTrip(){
        return mTrips.stream().filter(ChooseTripRow::getChecked).findFirst().orElse(null);
    }


    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewChooseTripBinding binding = ViewChooseTripBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TripViewHolder(binding, onClickTripListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        ChooseTripRow trip = mTrips.get(position);

        holder.binding.tripName.setText(trip.getName());

        if (!TextUtils.isEmpty(trip.getAvatarUri())) {
            Glide.with(BaseApp.context)
                    .load(trip.getAvatarUri())
                    .centerCrop()
                    .placeholder(R.drawable.progress_animation)
                    .into(holder.binding.tripAvatar);
        }

        holder.binding.radioButton.setChecked(trip.getChecked());
        holder.binding.radioButton.setOnClickListener(v -> {
            setSelectedFriend(trip.getId());
        });

        holder.binding.root.setOnClickListener(v -> {
            setSelectedFriend(trip.getId());
        });
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    public class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewChooseTripBinding binding;
        OnClickTripListener onHubListener;

        public TripViewHolder(ViewChooseTripBinding binding, OnClickTripListener onHubListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.onHubListener = onHubListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }

    }

    public interface OnClickTripListener {
        void onTripClick(Long hubId, String hubName);
    }

}
