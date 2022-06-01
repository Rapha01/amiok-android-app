package com.domain.amiok;


import android.content.Context;
import android.util.Log;

import java.io.Serializable;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Entry implements Serializable {
    public String name;
    public int id;
    public int interval;
    public String email;
    public String message;
    public String lastUpdate;
    public int amiok;
    public static final long HOUR = 3600*1000;

    public Entry(String entryName, int entryId) {
        name = entryName;
        id = entryId;
    }


    public Entry(int entryId, String entryName, int entryInterval, String entryEmail, String entryMessage, String entryLastUpdate) {
        name = entryName;
        id = entryId;
        email = entryEmail;
        message = entryMessage;
        interval = entryInterval;
        lastUpdate = entryLastUpdate;
        amiok = 0;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date lastUpdateDate = sdf.parse(lastUpdate);
            Date limit = new Date(new Date().getTime() - HOUR * interval);

            if(interval == 0)
                amiok = 0;
             else if (lastUpdateDate.before(limit))
                amiok = -1;
             else
                amiok = 1;

        } catch (ParseException e) {
            Log.d("AAA","Parse Exception " + e);
        }

         //  1, 0 ,-1
    }

    public Entry(int entryId, Context context) {
        id = entryId;
    }

    public void addToTracked() {
        // TODO dbcall
    }

    public void removeFromTracked() {
        // TODO dbcall
    }

    public boolean isTracked() {
        // TODO dbcall
        return false;
    }


}