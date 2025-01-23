package com.example.nostra_java_init;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.glance.pwaSdkIntegrator.GamesConfigResponse;
import com.glance.pwaSdkIntegrator.GlancePwaIntegrationHelper;
import com.glance.pwaSdkIntegrator.model.GameBridgeResult;
import com.glance.pwawebsdk.base.GlancePwaSdk;
import com.glance.pwawebsdk.base.PwaIntegrator;
import com.glance.pwawebsdk.base.PwaSdkConfigBuilder;
import com.glance.pwawebsdk.base.model.PWAInitConfig;

public class MainActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        // Enable edge-to-edge UI
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Apply window insets to handle padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        GlancePwaSdk.getInstance().isInitialized().observe(this, isInitialized -> {
            if (isInitialized) {
                loadGamingFragment();
            } else {
                initSDK();
            }
        });
    }

    private void loadGamingFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("userid", "6a6dc2e8-8203-4fa0-9021-bcda404f9664");
        addFragment(PwaIntegrator.INSTANCE.launchGameCenterFragment(bundle), "gameCenterFragment");
    }

    private void addFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
    }

    private void initSDK() {
        PwaSdkConfigBuilder.Builder sdkBuilder = new PwaSdkConfigBuilder.Builder()
                .setContext(mContext)
                .initConfig(
                        new PWAInitConfig(
                                "", // userId
                                "deviceId", // deviceId
                                "", // apiKey
                                PwaSdkConfigBuilder.platformId, // platformId
                                GlancePwaIntegrationHelper.GAME_CONFIG_ENDPOINT, // configEndpoint
                                "", // zipUrl
                                "", // zipFallBackUrl
                                new GamesConfigResponse(null), // config
                                true // isStagingMode
                        )
                )
                .addAnalyticsTransport(GlancePwaIntegrationHelper.INSTANCE.fetchAnalyticsTransport(mContext))
                .addCallbacks(GlancePwaIntegrationHelper.INSTANCE.fetchGameJsBridgesAndCallback(mContext).getPwaCallbacks())
                .addBridges(GlancePwaIntegrationHelper.INSTANCE.fetchGameJsBridgesAndCallback(mContext).getJsBridgeMap());

        GlancePwaSdk.initialize(sdkBuilder.build());
    }
}
