package com.sony.smarteyeglass.extension.ARexp01;

/**
 * Created by CGI on 5/4/2016.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * The extension receiver receives the app startup intents and starts the
 * extension service when they arrive.
 */
public final class SampleExtensionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(Constants.LOG_TAG, ""+ intent.getAction());
        intent.setClass(context, SampleExtensionService.class);
        context.startService(intent);
    }
}
