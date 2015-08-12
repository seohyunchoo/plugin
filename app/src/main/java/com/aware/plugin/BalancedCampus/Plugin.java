package com.aware.plugin.BalancedCampus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.utils.Aware_Plugin;

import java.util.Calendar;

public class Plugin extends Aware_Plugin {

    private AlarmManager alarmManager;
    private PendingIntent morningIntent = null;

    @Override
    public void onCreate() {
        super.onCreate();

        TAG = "AWARE::"+getResources().getString(R.string.app_name);
        DEBUG = Aware.getSetting(this, Aware_Preferences.DEBUG_FLAG).equals("true");

        //Initialize our plugin's settings
        if( Aware.getSetting(this, Settings.STATUS_PLUGIN_TEMPLATE).length() == 0 ) {
            Aware.setSetting(this, Settings.STATUS_PLUGIN_TEMPLATE, true);
        }

        //Activate programmatically any sensors/plugins you need here
        //e.g., Aware.setSetting(this, Aware_Preferences.STATUS_ACCELEROMETER,true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ESM, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_SCREEN, true);
        if (DEBUG) Log.d(TAG, "Good Morning plugin running");
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        //To sync data to the server, you'll need to set this variables from your ContentProvider
        //DATABASE_TABLES = Provider.DATABASE_TABLES
        //TABLES_FIELDS = Provider.TABLES_FIELDS
        //CONTEXT_URIS = new Uri[]{ Provider.Table_Data.CONTENT_URI }

        //Activate plugin
        Aware.startPlugin(this, getPackageName());

        //Apply settings in AWARE
        sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
        scheduleQuestionnaireTest();
    }

    //This function gets called every 5 minutes by AWARE to make sure this plugin is still running.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Check if the user has toggled the debug messages
        DEBUG = Aware.getSetting(this, Aware_Preferences.DEBUG_FLAG).equals("true");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Aware.setSetting(this, Settings.STATUS_PLUGIN_TEMPLATE, false);

        //Deactivate any sensors/plugins you activated here
        //e.g., Aware.setSetting(this, Aware_Preferences.STATUS_ACCELEROMETER, false);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ESM, false);


        //Stop plugin
        Aware.stopPlugin(this, getPackageName());
    }

    public void scheduleQuestionnaire() {
        Intent alarmIntent = new Intent(this,AlarmReceiver.class);
        Calendar cal = Calendar.getInstance();
        if(cal.get(Calendar.HOUR_OF_DAY) < 9){
            cal.add(Calendar.DAY_OF_YEAR, 0);
            cal.set(Calendar.HOUR_OF_DAY, 9);
            cal.set(Calendar.MINUTE, 00);
            cal.set(Calendar.SECOND, 00);
        } else if (cal.get(Calendar.HOUR_OF_DAY) < 13){
            cal.add(Calendar.DAY_OF_YEAR, 0);
            cal.set(Calendar.HOUR_OF_DAY, 13);
            cal.set(Calendar.MINUTE, 00);
            cal.set(Calendar.SECOND, 00);
        } else if (cal.get(Calendar.HOUR_OF_DAY) < 17){
            cal.add(Calendar.DAY_OF_YEAR, 0);
            cal.set(Calendar.HOUR_OF_DAY, 17);
            cal.set(Calendar.MINUTE, 00);
            cal.set(Calendar.SECOND, 00);
        } else if (cal.get(Calendar.HOUR_OF_DAY) < 21){
            cal.add(Calendar.DAY_OF_YEAR, 0);
            cal.set(Calendar.HOUR_OF_DAY, 21);
            cal.set(Calendar.MINUTE, 00);
            cal.set(Calendar.SECOND, 00);
        } else {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            cal.set(Calendar.HOUR_OF_DAY, 9);
            cal.set(Calendar.MINUTE, 00);
            cal.set(Calendar.SECOND, 00);
        }
        morningIntent = PendingIntent.getBroadcast(getApplicationContext(), 123123,
                alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), morningIntent);
        Log.d(TAG, "Alarm 1 :" + cal.getTimeInMillis());
    }

    public void scheduleQuestionnaireTest() {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 2);
        cal.set(Calendar.SECOND, 00);
        morningIntent = PendingIntent.getBroadcast(getApplicationContext(), 123122,
                alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), morningIntent);
        Log.d(TAG, "Alarm 1, Test :" + cal.getTimeInMillis());
    }

}
