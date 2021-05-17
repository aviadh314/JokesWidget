package com.my.jokeswidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.widget.RemoteViews;

import com.my.jokeswidget.httpclient.JokesClient;


public class MyWidget extends AppWidgetProvider {

    private static final String RANDOM_JOKE_CLICKED  = "widgetRandomJokeClick";
    private static final String PUNCHLINE_CLICKED  = "widgetJokePunchlineClick";
    private static Pair<String, String> randJoke;
    private static final JokesClient jokesClient = new JokesClient();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
        int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget);
        CharSequence loadingText = context.getString(R.string.loading);
        views.setTextViewText(R.id.tvJokeSet, loadingText);

        // Widget drawable background by user selection
        int modeId = MyWidgetConfigureActivity.loadModePref(context, appWidgetId);
        views.setInt(R.id.rlWidget, "setBackgroundResource", modeId);

        // Click intents
        views.setOnClickPendingIntent(R.id.tvRandomJoke,
                getPendingSelfIntent(context, RANDOM_JOKE_CLICKED));
        views.setOnClickPendingIntent(R.id.tvJokePunch,
                getPendingSelfIntent(context, PUNCHLINE_CLICKED));

        // Set a new random joke
        ComponentName jokeWidget = new ComponentName(context, MyWidget.class);
        setRandomJoke(views, context, appWidgetManager, jokeWidget);
    }

    protected static PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, MyWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (RANDOM_JOKE_CLICKED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.my_widget);
            ComponentName jokeWidget = new ComponentName(context, MyWidget.class);
            setRandomJoke(remoteViews, context, appWidgetManager, jokeWidget);
        } else if (PUNCHLINE_CLICKED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.my_widget);
            ComponentName jokeWidget = new ComponentName(context, MyWidget.class);
            remoteViews.setTextViewText(R.id.tvJokePunch, randJoke.second);
            appWidgetManager.updateAppWidget(jokeWidget, remoteViews);
        }
    }

    private static void setRandomJoke(RemoteViews remoteViews, Context context,
        AppWidgetManager appWidgetManager, ComponentName jokeWidget){
        jokesClient.fetchJokeFromServer(context, isSuccess -> {
            if (!isSuccess){
                randJoke = new Pair<>("Loading...", "");
            } else {
                randJoke = jokesClient.joke();
            }
            remoteViews.setTextViewText(R.id.tvJokeSet, randJoke.first);
            remoteViews.setTextViewText(R.id.tvJokePunch, context.getString(R.string.click_for_punchline));
            appWidgetManager.updateAppWidget(jokeWidget, remoteViews);
        });
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            MyWidgetConfigureActivity.deleteModePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}