package com.example.ivan.champy_v2.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.debug.hv.ViewServer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ivan.champy_v2.R;
import com.example.ivan.champy_v2.helper.CHCheckPendingDuels;
import com.example.ivan.champy_v2.helper.CHLoadBlurredPhoto;
import com.example.ivan.champy_v2.utils.OfflineMode;
import com.example.ivan.champy_v2.utils.SessionManager;

import java.io.File;
import java.io.FileNotFoundException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.ivan.champy_v2.utils.Constants.path;

public class PrivacyActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private SessionManager sessionManager;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        new LoadText().execute();
        sessionManager = new SessionManager(this);

        TextView drawerUserName = (TextView)findViewById(R.id.textView1); // overloading, be care
        drawerUserName.setVisibility(View.INVISIBLE);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        File file = new File(path, "profile.jpg");
        Uri url = Uri.fromFile(file);
        String name = sessionManager.getUserName();

        final Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/bebasneue.ttf");
        final ImageView drawerUserPhoto = (ImageView) headerLayout.findViewById(R.id.profile_image);
        drawerUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        drawerUserName.setText(name);
        drawerUserName.setTypeface(typeface);

        Glide.with(this).load(url).bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(drawerUserPhoto);

        try {
            ImageView imageView = (ImageView) headerLayout.findViewById(R.id.slide_background);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageDrawable(CHLoadBlurredPhoto.Init(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ViewServer.get(this).addWindow(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.challenges:
                Intent gotoChallenges = new Intent(this, MainActivity.class);
                startActivity(gotoChallenges);
                break;
            case R.id.friends:
                Intent goToFriends = new Intent(this, FriendsActivity.class);
                startActivity(goToFriends);
                break;
            case R.id.pending_duels:
                Intent goToPendingDuel = new Intent(this, PendingDuelActivity.class);
                startActivity(goToPendingDuel);
                break;
            case R.id.history:
                Intent goToHistory = new Intent(this, HistoryActivity.class);
                startActivity(goToHistory);
                break;
            case R.id.settings:
                Intent goToSettings = new Intent(this, SettingsActivity.class);
                startActivity(goToSettings);
                break;
            case R.id.share:
                String message = "Check out Champy - it helps you improve and compete with your friends!";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, "How would you like to share?"));
                break;
            case R.id.nav_logout:
                OfflineMode offlineMode = new OfflineMode();
                if (offlineMode.isConnectedToRemoteAPI(this)) {
                    sessionManager.logout(this);
                }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private class LoadText extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            String text  = "<p>" +
                    "    <strong>PRIVACY POLICY</strong>\n" +
                    "</p>\n" +
                    "<p>\n" +
                    "    Champy (“Champy,” “we,” and “us”) respects the privacy of its users (“you”) and has developed this PrivacyActivity Policy to demonstrate its commitment to\n" +
                    "    protecting your privacy. This PrivacyActivity Policy describes the information we collect, how that information may be used, with whom it may be shared, and your\n" +
                    "    choices about such uses and disclosures. We encourage you to read this PrivacyActivity Policy carefully when using our application or services or transacting\n" +
                    "    business with us. By using our website or application (our “Service”), you are accepting the practices described in this PrivacyActivity Policy.\n" +
                    "</p>\n" +
                    "<p>\n" +
                    "    If you have any questions about our privacy practices, please refer to the end of this PrivacyActivity Policy for information on how to contact us.\n" +
                    "</p>\n" +
                    "<ol>\n" +
                    "    <li>\n" +
                    "        <p>\n" +
                    "            <strong>Information we collect about you</strong>\n" +
                    "        </p>\n" +
                    "        <ul>\n" +
                    "            <li>\n" +
                    "                <p>\n" +
                    "                    In General. We may collect information that can identify you such as your name and email address (\"personal information\") and other\n" +
                    "                    information that does not identify you. We may collect this information through a website or a mobile application. By using the Service,\n" +
                    "                    you are authorizing us to gather, parse and retain data related to the provision of the Service.\n" +
                    "                </p>\n" +
                    "                <ul>\n" +
                    "                    <li>\n" +
                    "                        <p>\n" +
                    "                            <strong>Information you provide. </strong>\n" +
                    "                            In order to register as a user with Champy, you will be asked to sign in using your Facebook login. If you do so, you authorize us\n" +
                    "                            to access certain Facebook account information, such as your public Facebook profile (consistent with your privacy settings in\n" +
                    "                            Facebook), your email address, interests, likes, gender, birthday, education history, relationship interests, current city, photos,\n" +
                    "                            personal description, friend list, and information about and photos of your Facebook friends who might be common Facebook friends\n" +
                    "                            with other Champy users. In addition, we may collect and store any personal information you provide while using our Service or in\n" +
                    "                            some other manner. This may include identifying information, such as your name, address, email address. You may also provide us\n" +
                    "                            photos, a personal description and information about your. If you contact us with a customer service or other inquiry, you provide\n" +
                    "                            us with the content of that communication.\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                </ul>\n" +
                    "            </li>\n" +
                    "            <li>\n" +
                    "                <p>\n" +
                    "                    Use of technologies to collect information. We use various technologies to collect information from your device and about your activities\n" +
                    "                    on our Service.\n" +
                    "                </p>\n" +
                    "                <ul>\n" +
                    "                    <li>\n" +
                    "                        <p>\n" +
                    "                            <strong>Information collected automatically. </strong>\n" +
                    "                            We automatically collect information from your browser or device when you visit our Service. This information could include your IP\n" +
                    "                            address, device ID and type, your browser type and language, the operating system used by your device, access times, and the\n" +
                    "                            referring website address.\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p>\n" +
                    "                            <strong>Cookies and Use of Cookie Data. </strong>\n" +
                    "                            When you visit our Service, we may assign your device one or more cookies to facilitate access to our Service and to personalize\n" +
                    "                            your experience. Through the use of a cookie, we also may automatically collect information about your activity on our Service,\n" +
                    "                            such as the pages you visit, the time and date of your visits and the links you click. If we advertise, we (or third parties) may\n" +
                    "                            use certain data collected on our Service to show you Champy advertisements on other sites or applications.\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p>\n" +
                    "                            <strong>Other Technologies. </strong>\n" +
                    "                            We may use standard Internet technology, such as web beacons and other similar technologies, to track your use of our Service and\n" +
                    "                            to deliver or communicate with cookies. We also may include web beacons in advertisements or email messages to determine whether\n" +
                    "                            messages have been opened and acted upon. The information we obtain in this manner enables us to customize the services we offer\n" +
                    "                            users, to deliver targeted advertisements and to measure the overall effectiveness of our online advertising, content, programming\n" +
                    "                            or other activities.\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                </ul>\n" +
                    "            </li>\n" +
                    "        </ul>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p>\n" +
                    "            <strong>How we use the information we collect</strong>\n" +
                    "        </p>\n" +
                    "        <ul>\n" +
                    "            <li>\n" +
                    "                <p>\n" +
                    "                    In General. We may use information that we collect about you to:\n" +
                    "                </p>\n" +
                    "                <ul>\n" +
                    "                    <li>\n" +
                    "                        <p>\n" +
                    "                            deliver and improve our products and services, and manage our business;\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p>\n" +
                    "                            manage your account and provide you with customer support;\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p>\n" +
                    "                            perform research and analysis about your use of, or interest in, our or others’ products, services, or content;\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p>\n" +
                    "                            communicate with you by email, postal mail, telephone and/or mobile devices about products or services that may be of interest to\n" +
                    "                            you either from us or other third parties;\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p>\n" +
                    "                            develop, display, and track content and advertising tailored to your interests on our Service and other sites, including providing\n" +
                    "                            our advertisements to you when you visit other sites;\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p>\n" +
                    "                            website or mobile application analytics;\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p>\n" +
                    "                            verify your eligibility and deliver prizes in connection with contests and sweepstakes;\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p>\n" +
                    "                            enforce or exercise any rights in our TermsActivity of Use; and\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p>\n" +
                    "                            perform functions or services as otherwise described to you at the time of collection.\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                </ul>\n" +
                    "            </li>\n" +
                    "        </ul>\n" +
                    "    </li>\n" +
                    "</ol>\n" +
                    "<p>\n" +
                    "    In all circumstances, we may perform these functions directly or use a third party vendor to perform these functions on our behalf who will be obligated to\n" +
                    "    use your personal information only to perform services for us. Also, if you access our Service from a third party social platform, such as Facebook, we may\n" +
                    "    share non-personal information with that platform to the extent permitted by your agreement with it and its privacy settings.\n" +
                    "</p>\n" +
                    "<ol start=\"3\">\n" +
                    "    <li>\n" +
                    "        <p>\n" +
                    "            <strong>With whom we share your information</strong>\n" +
                    "        </p>\n" +
                    "        <ul>\n" +
                    "            <li>\n" +
                    "                <p>\n" +
                    "                    Information Shared with Other Users. When you register as a user of Champy, your Champy profile will be viewable by other users of the\n" +
                    "                    Service. Other users will be able to view information you have provided to us directly or through Facebook, such as your Facebook photos,\n" +
                    "                    your first and last name, your age.\n" +
                    "                </p>\n" +
                    "            </li>\n" +
                    "            <li>\n" +
                    "                <p>\n" +
                    "                    Personal information. We do not share your personal information with others except as indicated in this PrivacyActivity Policy or when we inform\n" +
                    "                    you and give you an opportunity to opt out of having your personal information shared. We may share personal information with:\n" +
                    "                </p>\n" +
                    "                <ul>\n" +
                    "                    <li>\n" +
                    "                        <p>\n" +
                    "                            <strong>Service providers: </strong>\n" +
                    "                            We may share information, including personal and financial information, with third parties that perform certain services on our\n" +
                    "                            behalf. These services may include fulfilling orders, providing customer service and marketing assistance, performing business and\n" +
                    "                            sales analysis, ad tracking and analytics, member screenings, supporting our Service functionality, and supporting contests,\n" +
                    "                            sweepstakes, surveys and other features offered through our Service. These service providers may have access to personal\n" +
                    "                            information needed to perform their functions but are not permitted to share or use such information for any other purposes.\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p>\n" +
                    "                            <strong>Other Situations. </strong>\n" +
                    "                            We may disclose your information, including personal information:\n" +
                    "                        </p>\n" +
                    "                        <ul>\n" +
                    "                            <li>\n" +
                    "                                <p>\n" +
                    "                                    In response to a subpoena or similar investigative demand, a court order, or a request for cooperation from a law\n" +
                    "                                    enforcement or other government agency; to establish or exercise our legal rights; to defend against legal claims; or as\n" +
                    "                                    otherwise required by law. In such cases, we may raise or waive any legal objection or right available to us.\n" +
                    "                                </p>\n" +
                    "                            </li>\n" +
                    "                            <li>\n" +
                    "                                <p>\n" +
                    "                                    When we believe disclosure is appropriate in connection with efforts to investigate, prevent, or take other action\n" +
                    "                                    regarding illegal activity, suspected fraud or other wrongdoing; to protect and defend the rights, property or safety of\n" +
                    "                                    our company, our users, our employees, or others; to comply with applicable law or cooperate with law enforcement; or to\n" +
                    "                                    enforce our TermsActivity of Use or other agreements or policies.\n" +
                    "                                </p>\n" +
                    "                            </li>\n" +
                    "                            <li>\n" +
                    "                                <p>\n" +
                    "                                    In connection with a substantial corporate transaction, such as the sale of our business, a divestiture, merger,\n" +
                    "                                    consolidation, or asset sale, or in the unlikely event of bankruptcy.\n" +
                    "                                </p>\n" +
                    "                            </li>\n" +
                    "                        </ul>\n" +
                    "                    </li>\n" +
                    "                </ul>\n" +
                    "            </li>\n" +
                    "            <li>\n" +
                    "                <p>\n" +
                    "                    Aggregated and/or non-personal information. We may use and share non-personal information we collect under any of the above circumstances.\n" +
                    "                    We may also share it with third parties to develop and deliver targeted advertising on our Service and on websites and to analyze and\n" +
                    "                    report on advertising you see. We may combine non-personal information we collect with additional non- personal information collected from\n" +
                    "                    other sources.\n" +
                    "                </p>\n" +
                    "            </li>\n" +
                    "        </ul>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p>\n" +
                    "            <strong>How you can access your information</strong>\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "</ol>\n" +
                    "<p>\n" +
                    "    If you have a Champy account, you have the ability to review and update your personal information within the Service by opening your account and going to\n" +
                    "    settings. More information about how to contact us is provided below. You also may close your account at any time by visiting the \"SettingsActivity\" page for your\n" +
                    "    account. If you close your account, we will retain certain information associated with your account for analytical purposes and recordkeeping integrity, as\n" +
                    "    well as to prevent fraud, enforce our TermsActivity of Use, take actions we deem necessary to protect the integrity of our Service or our users, or take other\n" +
                    "    actions otherwise permitted by law. In addition, if certain information has already been provided to third parties as described in this PrivacyActivity Policy,\n" +
                    "    retention of that information will be subject to those third parties' policies.\n" +
                    "</p>\n" +
                    "<ol start=\"5\">\n" +
                    "    <li>\n" +
                    "        <p>\n" +
                    "            <strong>Your choices about collection and use of your information</strong>\n" +
                    "        </p>\n" +
                    "        <ul>\n" +
                    "            <li>\n" +
                    "                <p>\n" +
                    "                    You can choose not to provide us with certain information, but that may result in you being unable to use certain features of our Service\n" +
                    "                    because such information may be required in order for you to register as user; participate in a contest, promotion, survey, or sweepstakes;\n" +
                    "                    ask a question; or initiate other transactions.\n" +
                    "                </p>\n" +
                    "            </li>\n" +
                    "            <li>\n" +
                    "                <p>\n" +
                    "                    Our Service may also deliver notifications to your phone or mobile device. You can offAndDisconnect these notifications by deleting the relevant\n" +
                    "                    Service or by changing the settings on your mobile device.\n" +
                    "                </p>\n" +
                    "            </li>\n" +
                    "            <li>\n" +
                    "                <p>\n" +
                    "                    You can also control information collected by cookies. You can delete or decline cookies by changing your browser settings. Click “help” in\n" +
                    "                    the toolbar of most browsers for instructions.\n" +
                    "                </p>\n" +
                    "            </li>\n" +
                    "        </ul>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p>\n" +
                    "            <strong>How we protect your personal information</strong>\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "</ol>\n" +
                    "<p>\n" +
                    "    We take security measures to help safeguard your personal information from unauthorized access and disclosure. However, no system can be completely secure.\n" +
                    "    Therefore, although we take steps to secure your information, we do not promise, and you should not expect, that your personal information will always\n" +
                    "    remain secure. Users should also take care with how they handle and disclose their personal information and should avoid sending personal information\n" +
                    "    through insecure email. Please refer to the Federal Trade Commission's website at http://www.ftc.gov/bcp/menus/consumer/data.shtm for information about how\n" +
                    "    to protect yourself against identity theft.\n" +
                    "</p>\n" +
                    "<ol start=\"7\">\n" +
                    "    <li>\n" +
                    "        <p>\n" +
                    "            <strong>Information you provide about yourself while using our Service</strong>\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "</ol>\n" +
                    "<p>\n" +
                    "    We provide areas on our Service where you can post information about yourself and others and communicate with others. Such postings are governed by our\n" +
                    "    TermsActivity of Use. Also, whenever you voluntarily disclose personal information on publicly-viewable pages, that information will be publicly available and can\n" +
                    "    be collected and used by others. For example, if you post your email address, you may receive unsolicited messages. We cannot control who reads your\n" +
                    "    posting or what other users may do with the information you voluntarily post, so we encourage you to exercise discretion and caution with respect to your\n" +
                    "    personal information.\n" +
                    "</p>\n" +
                    "<ol start=\"8\">\n" +
                    "    <li>\n" +
                    "        <p>\n" +
                    "            <strong>Visiting our Service from outside the United States</strong>\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "</ol>\n" +
                    "<p>\n" +
                    "    If you are visiting our Service from outside the United States, please be aware that your information may be transferred to, stored, and processed in the\n" +
                    "    United States and globally where our servers are located and our central database is operated. By using our services, you understand and agree that your\n" +
                    "    information may be transferred to our facilities and those third parties with whom we share it as described in this privacy policy.\n" +
                    "</p>\n" +
                    "<ol start=\"9\">\n" +
                    "    <li>\n" +
                    "        <p>\n" +
                    "            <strong>No Rights of Third Parties</strong>\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "</ol>\n" +
                    "<p>\n" +
                    "    This PrivacyActivity Policy does not create rights enforceable by third parties or require disclosure of any personal information relating to users of the website.\n" +
                    "</p>\n" +
                    "<ol start=\"10\">\n" +
                    "    <li>\n" +
                    "        <p align=\"justify\">\n" +
                    "            <strong>Changes to this PrivacyActivity Policy</strong>\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "</ol>\n" +
                    "<p>\n" +
                    "    <a name=\"_GoBack\"></a>\n" +
                    "    We will occasionally update this PrivacyActivity Policy. When we post changes to this PrivacyActivity Policy, we will revise the \"last updated\" date at the top of this\n" +
                    "    PrivacyActivity Policy. We recommend that you check our Service from time to time to inform yourself of any changes in this PrivacyActivity Policy or any of our other\n" +
                    "    policies.\n" +
                    "</p>\n" +
                    "<ol start=\"11\">\n" +
                    "    <li>\n" +
                    "        <p>\n" +
                    "            <strong>How to contact us</strong>\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "</ol>\n" +
                    "<p>\n" +
                    "    If you have any questions about this PrivacyActivity Policy, please contact us by email or postalmail as follows:\n" +
                    "</p>\n" +
                    "<p>\n" +
                    "    PrivacyActivity Officer\n" +
                    "    <br/>\n" +
                    "    Azinec LLC.\n" +
                    "    <br/>\n" +
                    "    3422 Old Capitol TRL STE 700\n" +
                    "    <br/>\n" +
                    "    Wilmington, Delaware 19808\n" +
                    "    <br/>\n" +
                    "    <u>iam@champyapp.com</u>\n" +
                    "</p>";
            String result = (Html.fromHtml(text)).toString();
            return result;
        }

        protected void onPostExecute(String result) {
            TextView textView = (TextView) findViewById(R.id.textView1);
            textView.setText(result, TextView.BufferType.SPANNABLE);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
            progressBar.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);

            final CHCheckPendingDuels checker = new CHCheckPendingDuels(getApplicationContext(), navigationView);
            int count = checker.getPendingCount();
            if (count == 0) {
                checker.hideItem();
            } else {
                TextView view = (TextView) navigationView.getMenu().findItem(R.id.pending_duels).getActionView();
                view.setText("+" + (count > 0 ? String.valueOf(count) : null));
            }
        }
    }

}
