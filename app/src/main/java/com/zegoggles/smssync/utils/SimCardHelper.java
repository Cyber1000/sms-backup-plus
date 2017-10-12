package com.zegoggles.smssync.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.SubscriptionManager;

public class SimCardHelper {
    public static int getSimCardCount(Context context) {
        return Build.VERSION.SDK_INT >=22 ? getSimCardCountInternal(context) : 1;
    }

    @RequiresApi(22)
    private static int getSimCardCountInternal(Context context)
    {
        SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
        return subscriptionManager.getActiveSubscriptionInfoCountMax();
    }
}
