package com.yary.blockapps;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordSetupActivity extends Activity {
    
    private EditText etNewPassword, etConfirmPassword;
    private Button btnSetPassword;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_setup);
        
        initializeViews();
        setupButtonListener();
    }
    
    private void initializeViews() {
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSetPassword = findViewById(R.id.btnSetPassword);
    }
    
    private void setupButtonListener() {
        btnSetPassword.setOnClickListener(v -> setPassword());
    }
    
    private void setPassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (newPassword.length() < 4) {
            Toast.makeText(this, "Пароль должен содержать минимум 4 символа", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Сохраняем пароль в SharedPreferences
        savePassword(newPassword);
        Toast.makeText(this, "Пароль успешно установлен", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    private void savePassword(String password) {
        SharedPreferences prefs = getSharedPreferences("block_apps_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("parent_password", password);
        editor.apply();
    }
    
    public static String getSavedPassword(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("block_apps_prefs", MODE_PRIVATE);
        return prefs.getString("parent_password", "1234"); // Пароль по умолчанию
    }
}