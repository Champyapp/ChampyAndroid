package com.azinecllc.champy.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.azinecllc.champy.controller.DailyRemindController;
import com.azinecllc.champy.controller.DailyWakeUpController;

/**
 * Created by SashaKhyzhun on 2/22/17.
 */
public class ResetService extends IntentService {

    public ResetService() {
        super("ResetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("CHAMPY RESET SERVICE", "onHandleIntent");
        DailyRemindController drc = new DailyRemindController(getApplicationContext());
        drc.enableDailyNotificationReminder();

        /**
         * создати в бд табличку wake-up, зберігати туда всі "години, мінути, реквест коди і ID-шки"
         * челендів, а потім робити dwc.enable(a, b, c, d); задопомогою курсота (так як достаються
         * данні в SelfImprovementActivity.class, наприклад);
         *
         * ps. не забути поміняти DB_VERSION при Update-і!
         */

        //DailyWakeUpController dwc = new DailyWakeUpController(getApplicationContext());
        //dwc.enableDailyWakeUp();

    }

}
