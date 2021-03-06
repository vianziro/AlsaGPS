package com.mdc.elementary.alsagps;

/*
   AlsaGPS is a panic button app for Android
   Copyright (C) 2016 Moisés Díaz

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software Foundation,
   Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA

*/

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ThreadAutomaticSMS extends Thread {
    Context context;
    CustomCallback callback;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public ThreadAutomaticSMS(Context cntxt) {
        context=cntxt;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
    }

    public void run() {
        try {
            InternalParams ip = new InternalParams(this.context);
            ip.loadParams();
            Log.e("CREATE", "start SMS");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean result = sendSMS();
                        callback.serviceFinished(result);
                        stopThread();
                    } catch (Exception e){
                        Log.e("CREATE", "Exception in ThreadAutomaticSMS");
                        callback.errorInService("SMS system", e.getMessage());
                    }
                }
            }, ip.getTimeWarn()*60*60*1000);
        }catch(Exception e){
            Log.e("CREATE", "Exception in ThreadAutomaticSMS");
            callback.errorInService("SMS Thread", "Problem in ThreadAutomaticSMS");

        }
    }
    public void stopThread(){
        Log.e("CREATE", "Stop SMS");
        mHandler.removeCallbacks(this);
        mHandler.removeCallbacksAndMessages(null);
    }

    private boolean sendSMS() throws Exception{
        SMSSystem sms = new SMSSystem(this.context);

        Log.e("CREATE", "run SMS");

        return sms.sendAutomaticSMS();
    }
}