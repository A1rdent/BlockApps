package com.yary.blockapps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AppListAdapter.OnAppClickListener {

    private RecyclerView recyclerView;
    private ActiveTimersAdapter adapter;
    private final List<ActiveTimer> activeTimers = new ArrayList<>();
    private Button btnAddTimer;
    private Button btnSetupPassword;
    private TextView tvEmptyState;
    private LinearLayout mainLayout; // Будем использовать основной layout

    private String selectedPackageName;
    private String selectedAppName;

    private ActivityResultLauncher<Intent> appSelectionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupRecyclerView();
        setupActivityResultLauncher();
        setupButtonListeners();
        updateEmptyState();
        
        // Добавляем тестовую функциональность
        testBlockFunctionality();
        
        // Запускаем сервис мониторинга при создании активности
        startMonitoringService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем таймеры в сервисе при возвращении в приложение
        updateMonitoringService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Очищаем ресурсы адаптера
        if (adapter != null) {
            adapter.cleanup();
        }
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_view_timers);
        btnAddTimer = findViewById(R.id.btn_add_timer);
        btnSetupPassword = findViewById(R.id.btn_setup_password);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        mainLayout = findViewById(android.R.id.content); // Используем корневой layout
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ActiveTimersAdapter(activeTimers, this::onTimerRemove);
        recyclerView.setAdapter(adapter);
    }

    private void setupActivityResultLauncher() {
        appSelectionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    handleAppSelectionResult(data);
                }
            });
    }

    private void setupButtonListeners() {
        btnAddTimer.setOnClickListener(v -> openAppSelection());
        btnSetupPassword.setOnClickListener(v -> openPasswordSetup());
    }

    private void openAppSelection() {
        Intent intent = new Intent(this, AppSelectionActivity.class);
        appSelectionLauncher.launch(intent);
    }

    private void openPasswordSetup() {
        Intent intent = new Intent(this, PasswordSetupActivity.class);
        startActivity(intent);
    }

    private void handleAppSelectionResult(Intent data) {
        try {
            selectedPackageName = data.getStringExtra("selected_app");
            selectedAppName = data.getStringExtra("selected_app_name");
            
            Log.d("MainActivity", "Selected app: " + selectedAppName + ", package: " + selectedPackageName);
            
            if (selectedPackageName != null && selectedAppName != null) {
                showTimePickerDialog();
            } else {
                Log.e("MainActivity", "Null package or app name");
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error in handleAppSelectionResult: " + e.getMessage(), e);
        }
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog();
        timePickerDialog.setTimePickerListener((hours, minutes, seconds) -> {
            long totalMillis = (hours * 3600L + minutes * 60L + seconds) * 1000L;
            
            if (totalMillis > 0) {
                ActiveTimer newTimer = new ActiveTimer(selectedAppName, selectedPackageName, totalMillis);
                activeTimers.add(newTimer);
                adapter.notifyDataSetChanged();
                updateEmptyState();
                
                // Обновляем сервис мониторинга
                updateMonitoringService();
                
                Log.d("MainActivity", "Timer created: " + hours + "h " + minutes + "m " + seconds + "s");
            } else {
                Log.e("MainActivity", "Invalid time: 0 seconds");
            }
        });
        
        timePickerDialog.show(getSupportFragmentManager(), "TimePickerDialog");
    }

    private void onTimerRemove(ActiveTimer timer) {
        activeTimers.remove(timer);
        adapter.notifyDataSetChanged();
        updateEmptyState();
        
        // Обновляем сервис мониторинга
        updateMonitoringService();
    }

    private void updateEmptyState() {
        if (activeTimers.isEmpty()) {
            tvEmptyState.setVisibility(android.view.View.VISIBLE);
            recyclerView.setVisibility(android.view.View.GONE);
        } else {
            tvEmptyState.setVisibility(android.view.View.GONE);
            recyclerView.setVisibility(android.view.View.VISIBLE);
        }
    }

    private void startMonitoringService() {
        try {
            Intent serviceIntent = new Intent(this, AppMonitoringService.class);
            startService(serviceIntent);
            Log.d("MainActivity", "Monitoring service started");
            
            // Сразу обновляем таймеры
            updateMonitoringService();
        } catch (Exception e) {
            Log.e("MainActivity", "Error starting monitoring service: " + e.getMessage());
        }
    }

    private void updateMonitoringService() {
        try {
            AppMonitoringService.updateTimers(activeTimers);
            Log.d("MainActivity", "Monitoring service updated with " + activeTimers.size() + " timers");
            
            // Логируем информацию о таймерах для отладки
            for (ActiveTimer timer : activeTimers) {
                Log.d("MainActivity", "Timer: " + timer.getAppName() + 
                      ", remaining: " + timer.calculateRemainingTime() + 
                      ", running: " + timer.isRunning());
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error updating monitoring service: " + e.getMessage());
        }
    }

    // ТЕСТОВАЯ ФУНКЦИОНАЛЬНОСТЬ ДЛЯ ПРОВЕРКИ БЛОКИРОВКИ
    private void testBlockFunctionality() {
        // Тестовая кнопка для проверки блокировки
        Button testBlockBtn = new Button(this);
        testBlockBtn.setText("🔒 ТЕСТ: Заблокировать сейчас");
        testBlockBtn.setBackgroundColor(0xFFFF9800); // Оранжевый цвет
        testBlockBtn.setTextColor(0xFFFFFFFF); // Белый текст
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 16, 0, 16);
        testBlockBtn.setLayoutParams(params);
        
        testBlockBtn.setOnClickListener(v -> {
            // Создаем тестовый таймер с истекшим временем
            ActiveTimer testTimer = new ActiveTimer("Тестовое приложение", "com.example.test", 0);
            testTimer.setRunning(true);
            
            // Показываем экран блокировки
            Intent blockIntent = new Intent(this, BlockActivity.class);
            blockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            blockIntent.putExtra("blocked_package", "com.example.test");
            blockIntent.putExtra("blocked_app_name", "Тестовое приложение");
            blockIntent.putExtra("remaining_time", 0);
            
            startActivity(blockIntent);
            
            Toast.makeText(this, "Тест блокировки запущен", Toast.LENGTH_SHORT).show();
            Log.d("MainActivity", "Test block activity started");
        });
        
        // Дополнительная тестовая кнопка для быстрой проверки пароля
        Button testPasswordBtn = new Button(this);
        testPasswordBtn.setText("🔑 ТЕСТ: Проверить пароль (1234)");
        testPasswordBtn.setBackgroundColor(0xFF4CAF50); // Зеленый цвет
        testPasswordBtn.setTextColor(0xFFFFFFFF); // Белый текст
        testPasswordBtn.setLayoutParams(params);
        
        testPasswordBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Текущий пароль: 1234", Toast.LENGTH_LONG).show();
        });
        
        // Добавляем тестовые кнопки в основной layout (перед RecyclerView)
        if (mainLayout != null && mainLayout instanceof LinearLayout) {
            LinearLayout linearLayout = (LinearLayout) mainLayout;
            
            // Создаем контейнер для тестовых кнопок
            LinearLayout testContainer = new LinearLayout(this);
            testContainer.setOrientation(LinearLayout.VERTICAL);
            testContainer.setBackgroundColor(0xFFE0E0E0);
            testContainer.setPadding(16, 16, 16, 16);
            
            LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            containerParams.setMargins(0, 0, 0, 16);
            testContainer.setLayoutParams(containerParams);
            
            // Добавляем заголовок
            TextView testTitle = new TextView(this);
            testTitle.setText("⚡ ТЕСТОВЫЕ ФУНКЦИИ");
            testTitle.setTextSize(14);
            testTitle.setTypeface(null, android.graphics.Typeface.BOLD);
            testTitle.setGravity(android.view.Gravity.CENTER);
            testTitle.setTextColor(0xFFFF0000);
            
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            titleParams.setMargins(0, 0, 0, 8);
            testTitle.setLayoutParams(titleParams);
            
            // Добавляем элементы в контейнер
            testContainer.addView(testTitle);
            testContainer.addView(testBlockBtn);
            testContainer.addView(testPasswordBtn);
            
            // Добавляем контейнер в основной layout (после кнопок, перед RecyclerView)
            linearLayout.addView(testContainer, 3); // Добавляем на 4-ю позицию
        }
    }

    @Override
    public void onAppClick(AppInfo appInfo) {
    }
}