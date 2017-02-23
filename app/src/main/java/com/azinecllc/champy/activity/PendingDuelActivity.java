package com.azinecllc.champy.activity;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.PendingDuelsAdapter;
import com.azinecllc.champy.helper.CHCheckPendingDuels;
import com.azinecllc.champy.utils.SessionManager;

public class PendingDuelActivity extends AppCompatActivity {

    private int size;
    private View spinner;
    private TextView tvNoPendingDuels;
    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_duel);

        new ProgressTask().execute();

        typeface = Typeface.createFromAsset(getResources().getAssets(), "fonts/bebasneue.ttf");
        TextView tvPendingDuels = (TextView) findViewById(R.id.tvChallengeToMySelf);
        tvPendingDuels.setTypeface(typeface);

        CHCheckPendingDuels checker = CHCheckPendingDuels.getInstance();
        checker.getPendingCount(getApplicationContext());

        spinner.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.isFinishing();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private class ProgressTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            spinner = findViewById(R.id.loadingPanel);
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SessionManager sessionManager = SessionManager.getInstance(getApplicationContext());
                    try {
                        size = Integer.parseInt(sessionManager.get_duel_pending());
                    } catch (NumberFormatException e) {
                        size = 0;
                    }
                    PendingDuelsAdapter pagerAdapter = new PendingDuelsAdapter(getSupportFragmentManager());
                    ViewPager viewPager = (ViewPager) findViewById(R.id.pager_pending_duel);
                    pagerAdapter.setCount(size);
                    viewPager.setAdapter(pagerAdapter);
                    viewPager.setOffscreenPageLimit(1);
                    viewPager.setPageMargin(20);
                    viewPager.setClipToPadding(false);
                    viewPager.setPadding(90, 0, 90, 0);

                    tvNoPendingDuels = (TextView) findViewById(R.id.textViewNoPendingDuels);
                    System.out.println("PENDING SIZE: " + size);
                    if (size == 0) {
                        tvNoPendingDuels.setTypeface(typeface);
                        tvNoPendingDuels.setVisibility(View.VISIBLE);
                        System.out.println("tvNoPendingDuels should be visible");
                    } else {
                        tvNoPendingDuels.setVisibility(View.INVISIBLE);
                        System.out.println("tvNoPendingDuels should be invisible");
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }


}
