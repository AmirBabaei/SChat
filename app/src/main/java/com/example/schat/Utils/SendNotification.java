package com.example.schat.Utils;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class SendNotification {

    public SendNotification(String message, String heading, String notificationKey){
        notificationKey = "41d32a37-c696-4117-881b-6ab3efc86742";
        try {
            JSONObject notificationContent = new JSONObject("{'contents': {'en' :'" + message + "'},"
                    +"'include_player_ids':['" + notificationKey + "'],"
                    +"'headings':{'en': '" + heading + "'}}");
            OneSignal.postNotification(notificationContent, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
