package com.example.expensetracker.ui.addtrip;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.ViewTripSelectedUserBinding;
import com.example.expensetracker.model.User;
import com.example.expensetracker.ui.trips.OnClickRemoveSelectedUserListener;
import com.example.expensetracker.utils.BaseApp;

import java.util.List;

public class SelectedUsersAdapter extends RecyclerView.Adapter<SelectedUsersAdapter.SelectedUserViewHolder> {
    private List<User> mUsers;
    private OnClickRemoveSelectedUserListener onClickRemoveSelectedUserListener;

    public SelectedUsersAdapter(List<User> mUsers, OnClickRemoveSelectedUserListener onClickRemoveSelectedUserListener) {
        this.mUsers = mUsers;
        this.onClickRemoveSelectedUserListener = onClickRemoveSelectedUserListener;
    }

    public List<User> getUsers() {
        return mUsers;
    }

    public void updateRecyclerView(List<User> userList){
        mUsers.clear();
        mUsers.addAll(userList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SelectedUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewTripSelectedUserBinding binding = ViewTripSelectedUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SelectedUserViewHolder(binding, onClickRemoveSelectedUserListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedUserViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class SelectedUserViewHolder extends RecyclerView.ViewHolder {
        public ViewTripSelectedUserBinding binding;
        public OnClickRemoveSelectedUserListener onClickRemoveSelectedUserListener;

        public SelectedUserViewHolder(ViewTripSelectedUserBinding binding, OnClickRemoveSelectedUserListener onClickRemoveSelectedUserListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.onClickRemoveSelectedUserListener = onClickRemoveSelectedUserListener;
        }

        public void bind(final User user) {
            binding.userName.setText(user.getFullName());

            binding.closeImageView.setOnClickListener(l -> {
                onClickRemoveSelectedUserListener.removeUser(user);
            });

            if (!TextUtils.isEmpty(user.getAvatarUri())) {
                Glide.with(BaseApp.context)
                        .load(user.getAvatarUri())
                        .centerCrop()
                        .placeholder(R.drawable.progress_animation)
                        .into(binding.userAvatar);
            }
        }
    }
}
