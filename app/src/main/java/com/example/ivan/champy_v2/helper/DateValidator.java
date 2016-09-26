package com.example.ivan.champy_v2.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateValidator {

    public boolean isThisDateWithin3DaysRange(String dateToValidate, String dateFormat) {

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {

            // if not valid, it will throw ParseException
            Date date = sdf.parse(dateToValidate);

            // current date after 3 months
            Calendar currentDateAfterOneDays = Calendar.getInstance();
            currentDateAfterOneDays.add(Calendar.DAY_OF_MONTH, 1);

            // current date before 3 months
            Calendar currentDateBeforeOneDays = Calendar.getInstance();
            currentDateBeforeOneDays.add(Calendar.DAY_OF_MONTH, -1);

            /**************************************************************************************/
            System.out.println("\n\ncurrentDate : "       + Calendar.getInstance().getTime());
            System.out.println("currentDateAfter1Day : "  + currentDateAfterOneDays.getTime());
            System.out.println("currentDateBefore1Day : " + currentDateBeforeOneDays.getTime());
            System.out.println("dateToValidate : "        + dateToValidate);
            /**************************************************************************************/

            if (date.before(currentDateAfterOneDays.getTime()) && date.after(currentDateBeforeOneDays.getTime())) {
                //ok everything is fine, date in range
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

    }


}
