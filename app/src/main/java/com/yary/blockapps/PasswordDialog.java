package com.yary.blockapps;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

public class PasswordDialog extends Dialog {

    private EditText etPassword, etConfirmPassword;
    private Button btnSubmit;
    private PasswordSetupListener listener;

    public interface PasswordSetupListener {
        void onPasswordSet(String password, String confirmPassword);
    }

    public PasswordDialog(@NonNull Context context, PasswordSetupListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_password);

        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                return;
            }

            if (listener != null) {
                listener.onPasswordSet(password, confirmPassword);
            }
            dismiss();
        });

        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }
}