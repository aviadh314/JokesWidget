package com.my.jokeswidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;

public class MyWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.my.jokeswidget.MyWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    Button btDarkMode, btLightMode, btAddWidget;
    int widgetMode =  R.drawable.rs_dark;

    View.OnClickListener darkClickListener = view -> {
         widgetMode =  R.drawable.rs_dark;
         btDarkMode.setForeground(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.trans_yellow)));
         btLightMode.setForeground(new ColorDrawable(Color.TRANSPARENT));
    };

    View.OnClickListener lightClickListener = view -> {
        widgetMode =  R.drawable.rs_light;
        btLightMode.setForeground(new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.trans_yellow)));
        btDarkMode.setForeground(new ColorDrawable(Color.TRANSPARENT));
    };

    View.OnClickListener addWidgetClickListener = v -> {
        final Context context = MyWidgetConfigureActivity.this;

        // When the button is clicked, store the background locally
        saveTitlePref(context, mAppWidgetId, widgetMode);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        MyWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    };

    public MyWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, int modeId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId, modeId);
        prefs.apply();
    }


    static Integer loadModePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int modeId = prefs.getInt(PREF_PREFIX_KEY + appWidgetId, 0);
        if (modeId == 0) {
            return R.drawable.rs_dark;
        } else {
            return modeId;
        }
    }

    static void deleteModePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.my_widget_configure);

        btDarkMode = findViewById(R.id.btDarkMode);
        btLightMode = findViewById(R.id.btLightMode);
        btAddWidget = findViewById(R.id.btAddWidget);

        btDarkMode.setOnClickListener(darkClickListener);
        btLightMode.setOnClickListener(lightClickListener);
        btAddWidget.setOnClickListener(addWidgetClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }
}