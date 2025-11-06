package com.yary.blockapps; // Исправлен пакет

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordActivity extends Activity {
    
    private EditText etPassword;
    private Button btnSubmit;
    private TextView tvMessage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        
        initializeViews();
        setupButtonListener();
        setMessageFromIntent();
    }
    
    private void initializeViews() {
        etPassword = findViewById(R.id.etPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvMessage = findViewById(R.id.tvMessage);
    }
    
    private void setupButtonListener() {
        btnSubmit.setOnClickListener(v -> checkPassword());
    }
    
    private void setMessageFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String packageName = intent.getStringExtra("package_name");
            String appName = intent.getStringExtra("app_name");
            if (appName != null) {
                tvMessage.setText("Введите пароль для разблокировки " + appName);
            }
        }
    }
    
    private void checkPassword() {
        String input = etPassword.getText().toString().trim();
        if (isPasswordCorrect(input)) {
            // Пароль верный - закрываем активность
            finish();
        } else {
            Toast.makeText(this, "Неверный пароль", Toast.LENGTH_SHORT).show();
            etPassword.setText("");
        }
    }
    
    private boolean isPasswordCorrect(String input) {
        // Заглушка - замените на реальную проверку пароля
        // Например, проверка из SharedPreferences или базы данных
        return "1234".equals(input);
    }
    
    @Override
    public void onBackPressed() {
        // Запрещаем выход по кнопке назад при блокировке
        // super.onBackPressed(); // Раскомментируйте если нужно разрешить выход
        Toast.makeText(this, "Введите пароль для выхода", Toast.LENGTH_SHORT).show();
    }
}