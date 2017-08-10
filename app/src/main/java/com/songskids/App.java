package com.songskids;

import android.app.Application;
import android.content.res.Configuration;

import com.songskids.network.core.ApiClient;
import com.songskids.network.core.ApiConfig;


public class App extends Application {
    private static App instance = null;

    @Override
    public void onCreate() {
        super.onCreate();

        ApiConfig apiConfig = ApiConfig.builder(getApplicationContext())
                .baseUrl(getResources().getString(R.string.url_base))
                .build();
        ApiClient.getInstance().init(apiConfig);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }
}
