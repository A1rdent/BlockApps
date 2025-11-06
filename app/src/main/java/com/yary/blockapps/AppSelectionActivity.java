package com.yary.blockapps;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppSelectionActivity extends AppCompatActivity implements AppListAdapter.OnAppClickListener {

    private RecyclerView recyclerView;
    private EditText etSearch;
    private AppListAdapter adapter;
    private List<AppInfo> appList = new ArrayList<>();
    private List<AppInfo> filteredAppList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selection);

        initializeViews();
        setupRecyclerView();
        loadInstalledApps();
        setupSearch();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.rvApps);
        etSearch = findViewById(R.id.etSearch);
        
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppListAdapter(filteredAppList, this);
        recyclerView.setAdapter(adapter);
    }

    private void loadInstalledApps() {
        appList = getInstalledApps();
        filteredAppList.clear();
        filteredAppList.addAll(appList);
        adapter.notifyDataSetChanged();
        
        // Отладочная информация
        android.util.Log.d("AppSelection", "Total apps found: " + appList.size());
        for (AppInfo app : appList) {
            android.util.Log.d("AppSelection", "App: " + app.getAppName() + " (" + app.getPackageName() + ")");
        }
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApps(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private List<AppInfo> getInstalledApps() {
        List<AppInfo> apps = new ArrayList<>();
        PackageManager pm = getPackageManager();
        
        // Получаем ВСЕ установленные приложения
        List<android.content.pm.ApplicationInfo> installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        
        for (android.content.pm.ApplicationInfo appInfo : installedApps) {
            try {
                // Пропускаем только наше приложение
                boolean isOurApp = appInfo.packageName.equals(getPackageName());
                
                if (!isOurApp) {
                    String appName = appInfo.loadLabel(pm).toString();
                    Drawable icon = appInfo.loadIcon(pm);
                    
                    AppInfo app = new AppInfo(appName, appInfo.packageName, icon);
                    apps.add(app);
                }
            } catch (Exception e) {
                android.util.Log.e("AppSelection", "Error loading app: " + appInfo.packageName, e);
            }
        }
        
        // ИСПРАВЛЕННАЯ СОРТИРОВКА
        Collections.sort(apps, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo app1, AppInfo app2) {
                return app1.getAppName().compareToIgnoreCase(app2.getAppName());
            }
        });
        
        return apps;
    }

    private void filterApps(String query) {
        filteredAppList.clear();
        
        if (query.isEmpty()) {
            filteredAppList.addAll(appList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (AppInfo app : appList) {
                if (app.getAppName().toLowerCase().contains(lowerCaseQuery) || 
                    app.getPackageName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredAppList.add(app);
                }
            }
        }
        
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAppClick(AppInfo appInfo) {
        // Возвращаем выбранное приложение в предыдущую активность
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selected_app", appInfo.getPackageName());
        resultIntent.putExtra("selected_app_name", appInfo.getAppName());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}