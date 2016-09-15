package com.example.ivan.champy_v2.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateValidator {

    public boolean isThisDateWithin3MonthsRange(String dateToValidate, String dateFormat) {

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {

            // if not valid, it will throw ParseException
            Date date = sdf.parse(dateToValidate);

            // current date after 3 months
            Calendar currentDateAfter3Months = Calendar.getInstance();
            currentDateAfter3Months.add(Calendar.MONTH, 3);

            // current date before 3 months
            Calendar currentDateBefore3Months = Calendar.getInstance();
            currentDateBefore3Months.add(Calendar.MONTH, -3);

            /*************** verbose ***********************/
            System.out.println("\n\ncurrentDate : "          + Calendar.getInstance().getTime());
            System.out.println("currentDateAfter3Months : "  + currentDateAfter3Months.getTime());
            System.out.println("currentDateBefore3Months : " + currentDateBefore3Months.getTime());
            System.out.println("dateToValidate : " + dateToValidate);
            /************************************************/

            if (date.before(currentDateAfter3Months.getTime()) && date.after(currentDateBefore3Months.getTime())) {
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
