package com.yary.blockapps;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BlockActivity extends Activity {
    
    private EditText etPassword;
    private Button btnUnlock;
    private TextView tvBlockedApp;
    private TextView tvMessage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        
        // Делаем активность поверх всех окон
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                           WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                           WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                           WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        
        initializeViews();
        setupBlockInfo();
        setupButtonListener();
        
        Log.d("BlockActivity", "Block activity created");
    }
    
    private void initializeViews() {
        etPassword = findViewById(R.id.etPassword);
        btnUnlock = findViewById(R.id.btnUnlock);
        tvBlockedApp = findViewById(R.id.tvBlockedApp);
        tvMessage = findViewById(R.id.tvMessage);
    }
    
    private void setupBlockInfo() {
        Intent intent = getIntent();
        if (intent != null) {
            String blockedPackageName = intent.getStringExtra("blocked_package");
            String blockedAppName = intent.getStringExtra("blocked_app_name");
            long remainingTime = intent.getLongExtra("remaining_time", 0);
            
            if (blockedAppName != null) {
                tvBlockedApp.setText("🚫 " + blockedAppName);
                tvMessage.setText("Время использования истекло!\nВведите пароль родителя для разблокировки");
                Log.d("BlockActivity", "Blocking app: " + blockedAppName + " (" + blockedPackageName + ")");
            } else {
                tvBlockedApp.setText("🚫 Приложение заблокировано");
                tvMessage.setText("Время использования истекло!\nВведите пароль родителя для разблокировки");
                Log.d("BlockActivity", "Blocking unknown app");
            }
        }
    }
    
    private void setupButtonListener() {
        btnUnlock.setOnClickListener(v -> checkPassword());
        
        // Также разблокировка по Enter в поле пароля
        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            checkPassword();
            return true;
        });
    }
    
    private void checkPassword() {
        String input = etPassword.getText().toString().trim();
        if (input.isEmpty()) {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (isPasswordCorrect(input)) {
            // Пароль верный - разблокируем
            Toast.makeText(this, "Приложение разблокировано", Toast.LENGTH_SHORT).show();
            Log.d("BlockActivity", "App unlocked with correct password");
            finish();
        } else {
            Toast.makeText(this, "Неверный пароль", Toast.LENGTH_SHORT).show();
            etPassword.setText("");
            etPassword.requestFocus();
            Log.d("BlockActivity", "Wrong password entered: " + input);
        }
    }
    
    private boolean isPasswordCorrect(String input) {
        SharedPreferences prefs = getSharedPreferences("block_apps_prefs", MODE_PRIVATE);
        String savedPassword = prefs.getString("parent_password", "1234");
        boolean isCorrect = savedPassword.equals(input);
        Log.d("BlockActivity", "Password check: input='" + input + "', saved='" + savedPassword + "', result=" + isCorrect);
        return isCorrect;
    }
    
    @Override
    public void onBackPressed() {
        // Запрещаем выход по кнопке назад
        Toast.makeText(this, "Введите пароль для разблокировки", Toast.LENGTH_SHORT).show();
        Log.d("BlockActivity", "Back button pressed - prevented");
    }
    
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        // Предотвращаем сворачивание активности
        Toast.makeText(this, "Введите пароль для разблокировки", Toast.LENGTH_SHORT).show();
        Log.d("BlockActivity", "User tried to leave app - prevented");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("BlockActivity", "Block activity paused");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("BlockActivity", "Block activity resumed");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("BlockActivity", "Block activity destroyed");
    }
}