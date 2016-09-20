package com.example.ivan.champy_v2.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateValidator {

    public boolean isThisDateWithin1DayRange(String dateToValidate, String dateFormat) {

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {

            // if not valid, it will throw ParseException
            Date date = sdf.parse(dateToValidate);

            // current date after 1 day
            Calendar currentDateAfter1Day = Calendar.getInstance();
            currentDateAfter1Day.add(Calendar.DAY_OF_YEAR, 1);

            // current date before 1 day
            Calendar currentDateBefore1Day = Calendar.getInstance();
            currentDateBefore1Day.add(Calendar.DAY_OF_YEAR, -1);

            /*************** verbose ***********************/
            System.out.println("\n\ncurrentDate : "          + Calendar.getInstance().getTime());
            System.out.println("currentDateAfter1Day : "  + currentDateAfter1Day.getTime());
            System.out.println("currentDateBefore3Day : " + currentDateBefore1Day.getTime());
            System.out.println("dateToValidate : " + dateToValidate);
            /************************************************/

            if (date.before(currentDateAfter1Day.getTime()) && date.after(currentDateBefore1Day.getTime())) {
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
