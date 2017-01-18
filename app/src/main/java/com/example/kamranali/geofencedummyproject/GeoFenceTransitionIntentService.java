package com.example.kamranali.geofencedummyproject;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by kamranali on 04/01/2017.
 */

public class GeoFenceTransitionIntentService extends IntentService {
    DatabaseReference firebaseDatabase;
    Handler handler = new Handler();
    boolean isExit;

    int geofenceTransition;


    @Override
    public void onCreate() {
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        super.onCreate();
    }

    private static final String TAG = "GeoFenceTransitionIntentService.Service";

    public GeoFenceTransitionIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Getting Data form Intent
        final GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        //Checking if theres any error
        if (geofencingEvent.hasError()) {
            String errorCode = GeofenceErrorMessages.getErrorString(this, geofencingEvent.getErrorCode());
            Log.d(TAG, " " + geofencingEvent.getErrorCode());
            return;
        }
        //Getting type of Transition Occur
        geofenceTransition = geofencingEvent.getGeofenceTransition();

        //Checking geofenceTranstion type
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                //Getting List of the geofences that triggered this geofence transition alert
                sendNotification("Enter Detected");
                if (!isExit) {
                    List<Geofence> triggerdGeoFencesList = geofencingEvent.getTriggeringGeofences();

                    //Getting the location that triggered the geofence transition.
                    String geofenceTransitionDetails = getGeofenceTransitionDetails(this, geofenceTransition, triggerdGeoFencesList);
                    sendNotification(geofenceTransitionDetails);
                    Log.d(TAG, geofenceTransitionDetails);
                    Date date = new Date(System.currentTimeMillis());
                    SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, ''yy , hh:mm:ss", Locale.ENGLISH);
                    String st_date = format.format(date);
                    Map<String, String> timestamp = ServerValue.TIMESTAMP;
                    Log.d(TAG, " keySet " + timestamp.keySet() + " entrySet " + timestamp.entrySet() + " toString " + timestamp.toString() + " values " + timestamp.values() + " size " + timestamp.size());


                    HashMap<String, Object> value = new HashMap<>();
                    value.put("longitude", geofencingEvent.getTriggeringLocation().getLongitude());
                    value.put("lttitde", geofencingEvent.getTriggeringLocation().getLatitude());
                    value.put("place", geofenceTransitionDetails);
                    value.put("trigered-Time", st_date);
                    value.put("server-Time", ServerValue.TIMESTAMP);

                    firebaseDatabase.child("Kamran").push().setValue(value);
                } else {
                    isExit = false;
                }
            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                sendNotification("Exit Detected");
                isExit = true;
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        geofenceTransition = 0;
                        if (isExit) {
                            int geofenceTransitions = geofencingEvent.getGeofenceTransition();
                            if (geofenceTransitions == Geofence.GEOFENCE_TRANSITION_ENTER) {
                                Toast.makeText(GeoFenceTransitionIntentService.this, "u r enter again in office", Toast.LENGTH_SHORT).show();
                            } else if (geofenceTransitions == Geofence.GEOFENCE_TRANSITION_EXIT) {

                                Log.d("EXIT", "You are exit");
                                Toast.makeText(GeoFenceTransitionIntentService.this, "u r exit", Toast.LENGTH_SHORT).show();
                                List<Geofence> triggerdGeoFencesList = geofencingEvent.getTriggeringGeofences();

                                //Getting the location that triggered the geofence transition.
                                String geofenceTransitionDetails = getGeofenceTransitionDetails(GeoFenceTransitionIntentService.this, geofenceTransitions, triggerdGeoFencesList);

                                sendNotification(geofenceTransitionDetails);
                                Log.d(TAG, geofenceTransitionDetails);
                                Date date = new Date(System.currentTimeMillis());
                                SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, ''yy , hh:mm:ss", Locale.ENGLISH);
                                String st_date = format.format(date);
                                Map<String, String> timestamp = ServerValue.TIMESTAMP;
                                Log.d(TAG, " keySet " + timestamp.keySet() + " entrySet " + timestamp.entrySet() + " toString " + timestamp.toString() + " values " + timestamp.values() + " size " + timestamp.size());


                                HashMap<String, Object> value = new HashMap<>();
                                value.put("longitude", geofencingEvent.getTriggeringLocation().getLongitude());
                                value.put("lttitde", geofencingEvent.getTriggeringLocation().getLatitude());
                                value.put("place", geofenceTransitionDetails);
                                value.put("trigered-Time", st_date);
                                value.put("server-Time", ServerValue.TIMESTAMP);

                                firebaseDatabase.child("Kamran").push().setValue(value);

                            }
                            isExit = true;
                        }
                    }
                }, 1000 * 2 * 60);

            }
        }

    }

    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofencesList) {
        String geofenceTransitionString = getTransitionString(geofenceTransition);

        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofencesList) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(int transitionType) {

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(String notificationDetails) {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setColor(Color.RED)
                .setContentText("Click notification to return to app")
                .setContentTitle(notificationDetails)
                .setContentIntent(notificationPendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

    }
}
