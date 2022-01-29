package com.example.expensetracker.ui.auth;

import static android.content.Context.WINDOW_SERVICE;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.Navigation;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.DialogResetPasswordBinding;
import com.example.expensetracker.utils.AuthenticationFirebase;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ResetPasswordDialog extends DialogFragment {
    private DialogResetPasswordBinding binding;

    public static ResetPasswordDialog newInstance() {
        Bundle args = new Bundle();
        ResetPasswordDialog fragment = new ResetPasswordDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DialogResetPasswordBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.drpResetPasswordButton.setOnClickListener(v -> {
            String email = binding.drpEmailEditText.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getContext(), "No email provided.", Toast.LENGTH_SHORT).show();
            } else {
                AuthenticationFirebase.resetPassword(email).subscribe(bool -> {
                    if (bool) {
                        requireActivity().onBackPressed();
                    }
                }, error -> {
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle("Error")
                            .setMessage(error.getMessage())
                            .setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            WindowManager manager = (WindowManager) requireActivity().getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;

            this.getDialog().getWindow().setLayout((int) (width * 0.8), (int) (height * 0.45));
//
        }
    }
}
