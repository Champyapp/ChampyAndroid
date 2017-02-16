package com.azinecllc.champy.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.azinecllc.champy.R;

/**
 * Created by SashaKhyzhun on 2/8/17.
 */
public class TermsFragment extends Fragment {

    private TextView tvTerms;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewTerms = inflater.inflate(R.layout.fragment_terms, container, false);

        tvTerms = (TextView) viewTerms.findViewById(R.id.textView_terms);
        tvTerms.setVisibility(View.INVISIBLE);

        new LoadTermsText().execute();

        ProgressBar progressBar = (ProgressBar) viewTerms.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        return viewTerms;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().isFinishing();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
    }

    private class LoadTermsText extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            String text = "<p lang=\"en-US\">\n" +
                    "    By accessing the Champy application or its website found at <a href=\"champyapp.com\">champyapp.com</a>, whether through a mobile device, mobile application\n" +
                    "    or computer (collectively, the “<strong>Service</strong>”) you agree to be bound by these Terms of Use (this “<strong>Agreement</strong>”), whether or not\n" +
                    "    you create a Champy account. If you wish to create a Champy account and make use of the Service, please read these Terms of Use.\n" +
                    "</p>\n" +
                    "<p lang=\"en-US\">\n" +
                    "    You should also read the Champy Privacy Policy, which is incorporated by reference into this Agreement and available in the Service. If you do not accept\n" +
                    "    and agree to be bound by all of the terms of this Agreement, including the Champy Privacy Policy, do not use the Service. Please contact us with any\n" +
                    "    questions regarding this Agreement.\n" +
                    "</p>\n" +
                    "<ol>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            <strong>Acceptance of Terms of Use Agreement.</strong>\n" +
                    "        </p>\n" +
                    "        <ol type=\"a\">\n" +
                    "            <li>\n" +
                    "                <p lang=\"en-US\">\n" +
                    "                    This Agreement is an electronic contract that establishes the legally binding terms you must accept to use the Service. This Agreement\n" +
                    "                    includes the Company’s (i) Privacy Policy, (ii) and terms disclosed and agreed to by you if you purchase or accept additional features,\n" +
                    "                    products or services we offer on the Service, such as terms governing features, billing, free trials, discounts and promotions.\n" +
                    "                </p>\n" +
                    "            </li>\n" +
                    "            <li>\n" +
                    "                <p lang=\"en-US\">\n" +
                    "                    By accessing or using the Service, you accept this Agreement and agree to the terms, conditions and notices contained or referenced herein\n" +
                    "                    and consent to have this Agreement and all notices provided to you in electronic form. To withdraw this consent, you must cease using the\n" +
                    "                    Service and terminate your account. Please print a copy of this Agreement for your records. To receive a non- electronic copy of this\n" +
                    "                    Agreement, please contact us at <a href=\"mailto:iam@champyapp.com\">iam@champyapp.com</a> . This Agreement may be modified by the Company\n" +
                    "                    from time to time, such modifications to be effective upon posting by the Company in the Service.\n" +
                    "                </p>\n" +
                    "            </li>\n" +
                    "        </ol>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            <strong>Creating an Account.</strong>\n" +
                    "            In order to use Champy, you must sign in using your Facebook login. If you do so, you authorize us to access and use certain Facebook account\n" +
                    "            information, including but not limited to your public Facebook profile and information about Facebook friends you might share in common with other\n" +
                    "            Champy users. For more information regarding the information we collect from you and how we use it, please consult our Privacy Policy.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            <strong>Term and Termination.</strong>\n" +
                    "            This Agreement will remain in full force and effect while you use the Service and/or have a Champy account. You may offAndDisconnect your account at any\n" +
                    "            time, for any reason, by following the instructions in “settings” in the Service. The Company may terminate or suspend your account at any time\n" +
                    "            without notice if the Company believes that you have breached this Agreement, or for any other reason, with or without cause, in its sole\n" +
                    "            discretion. Upon such termination or suspension, you will not be entitled to any refund of unused fees for in app purchases. The Company is not\n" +
                    "            required to disclose, and may be prohibited by law from disclosing, the reason for the termination or suspension of your account. After your\n" +
                    "            account is terminated for any reason, all terms of this Agreement survive such termination, and continue in full force and effect, except for any\n" +
                    "            terms that by their nature expire or are fully satisfied.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            <strong>Non-commercial Use by Users.</strong>\n" +
                    "            The Service is for personal use only. Users may not use the Service or any content contained in the Service (including, but not limited to, content\n" +
                    "            of other users, designs, text, graphics, images, video, information, logos, software, audio files and computer code) in connection with any\n" +
                    "            commercial endeavors, such as (i) advertising or soliciting any user to buy or sell any products or services not offered by the Company or (ii)\n" +
                    "            soliciting others to attend parties or other social functions, or networking, for commercial purposes. Users of the Service may not use any\n" +
                    "            information obtained from the Service to contact, advertise to, solicit, or sell to any other user without his or her prior explicit consent.\n" +
                    "            Organizations, companies, and/or businesses may not use the Service or the Service for any purpose. The Company may investigate and take any\n" +
                    "            available legal action in response to illegal and/or unauthorized uses of the Service, including collecting usernames and/or email addresses of\n" +
                    "            users by electronic or other means for the purpose of sending unsolicited email and unauthorized framing of or linking to the Service.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            <strong>Account Security.</strong>\n" +
                    "            You are responsible for maintaining the confidentiality of the username and password you designate during the registration process, and you are\n" +
                    "            solely responsible for all activities that occur under your username and password. You agree to immediately notify the Company of any disclosure or\n" +
                    "            unauthorized use of your username or password or any other breach of security at <a href=\"mailto:iam@champyapp.com\">iam@champyapp.com</a> and\n" +
                    "            ensure that you log out from your account at the end of each session.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            Your Interactions with Other Users.\n" +
                    "        </p>\n" +
                    "        <ol type=\"a\">\n" +
                    "            <li>\n" +
                    "                <p lang=\"en-US\">\n" +
                    "YOU ARE SOLELY RESPONSIBLE FOR YOUR INTERACTIONS WITH OTHER USERS. YOU UNDERSTAND THAT THE COMPANY CURRENTLY<strong> </strong><u>DOES NOT</u><strong> </strong>CONDUCT CRIMINAL BACKGROUND CHECKS OR SCREENINGS ON ITS USERS. THE COMPANY ALSO <u>DOES NOT</u>                    <strong> </strong>INQUIRE INTO THE BACKGROUNDS OF ALL OF ITS USERS OR ATTEMPT TO VERIFY THE STATEMENTS OF ITS USERS. THE COMPANY MAKES NO\n" +
                    "                    REPRESENTATIONS OR WARRANTIES AS TO THE CONDUCT OF USERS OR THEIR COMPATIBILITY WITH ANY CURRENT OR FUTURE USERS. THE COMPANY RESERVES THE\n" +
                    "                    RIGHT TO CONDUCT ANY CRIMINAL BACKGROUND CHECK OR OTHER SCREENINGS, AT ANY TIME AND USING AVAILABLE PUBLIC RECORDS.\n" +
                    "                </p>\n" +
                    "            </li>\n" +
                    "            <li>\n" +
                    "                <p lang=\"en-US\">\n" +
                    "                    The Company is not responsible for the conduct of any user. As noted in and without limiting Sections 15 and 17 below, in no event shall\n" +
                    "                    the Company, its affiliates or its partners be liable (directly or indirectly) for any losses or damages whatsoever, whether direct,\n" +
                    "                    indirect, general, special, compensatory, consequential, and/or incidental, arising out of or relating to the conduct of you or anyone else\n" +
                    "                    in connection with the use of the Service including, without limitation, death, bodily injury, emotional distress, and/or any other damages\n" +
                    "                    resulting from communications or meetings with other users or persons you meet through the Service. You agree to take all necessary\n" +
                    "                    precautions in all interactions with other users, particularly if you decide to communicate off the Service or meet in person, or if you\n" +
                    "                    decide to send money to another user. You understand that the Company makes no guarantees, either express or implied, regarding your\n" +
                    "                    ultimate compatibility with individuals you meet through the Service. You should not provide your financial information (for example, your\n" +
                    "                    credit card or bank account information), or wire or otherwise send money, to other users.\n" +
                    "                </p>\n" +
                    "            </li>\n" +
                    "        </ol>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            <strong>Proprietary Rights.</strong>\n" +
                    "            The Company owns and retains all proprietary rights in the Service, and in all content, trademarks, trade names, service marks and other\n" +
                    "            intellectual property rights related thereto. The Service contains the copyrighted material, trademarks, and other proprietary information of the\n" +
                    "            Company and its licensors. You agree to not copy, modify, transmit, create any derivative works from, make use of, or reproduce in any way any\n" +
                    "            copyrighted material, trademarks, trade names, service marks, or other intellectual property or proprietary information accessible through the\n" +
                    "            Service, without first obtaining the prior written consent of the Company or, if such property is not owned by the Company, the owner of such\n" +
                    "            intellectual property or proprietary rights. You agree to not remove, obscure or otherwise alter any proprietary notices appearing on any content,\n" +
                    "            including copyright, trademark and other intellectual property notices.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            <strong>Content Posted by You in the Service.</strong>\n" +
                    "        </p>\n" +
                    "        <ol type=\"a\">\n" +
                    "            <li>\n" +
                    "                <p lang=\"en-US\">\n" +
                    "                    You are solely responsible for the content and information that you post, upload, publish, link to, transmit, record, display or otherwise\n" +
                    "                    make available (hereinafter, <strong><u>“post”</u></strong>) on the Service or transmit to other users, including text messages, chat,\n" +
                    "videos (including streaming videos), photographs, or profile text, whether publicly posted or privately transmitted (collectively,                    <strong><u>“Content”</u></strong>). You may not post as part of the Service, or transmit to the Company or any other user (either on or off\n" +
                    "                    the Service), any offensive, inaccurate, incomplete, abusive, obscene, profane, threatening, intimidating, harassing, racially offensive,\n" +
                    "                    or illegal material, or any material that infringes or violates another person’s rights (including intellectual property rights, and rights\n" +
                    "                    of privacy and publicity). You represent and warrant that (i) all information that you submit upon creation of your account, including\n" +
                    "                    information submitted from your Facebook account, is accurate and truthful and that you will promptly update any information provided by\n" +
                    "                    you that subsequently becomes inaccurate, incomplete, misleading or false and (ii) you have the right to post the Content on the Service\n" +
                    "                    and grant the licenses set forth below.\n" +
                    "                </p>\n" +
                    "            </li>\n" +
                    "            <li>\n" +
                    "                <p lang=\"en-US\">\n" +
                    "                    You understand and agree that the Company may, but is not obligated to, monitor or review any Content you post as part of a Service. The\n" +
                    "                    Company may delete any Content, in whole or in part, that in the sole judgment of the Company violates this Agreement or may harm the\n" +
                    "                    reputation of the Service or the Company.\n" +
                    "                </p>\n" +
                    "            </li>\n" +
                    "            <li>\n" +
                    "                <p lang=\"en-US\">\n" +
                    "                    By posting Content as part of the Service, you automatically grant to the Company, its affiliates, licensees and successors, an\n" +
                    "                    irrevocable, perpetual, non- exclusive, transferable, sub-licensable, fully paid-up, worldwide right and license to (i) use, copy, store,\n" +
                    "                    perform, display, reproduce, record, play, adapt, modify and distribute the Content, (ii) prepare derivative works of the Content or\n" +
                    "                    incorporate the Content into other works, and (iii) grant and authorize sublicenses of the foregoing in any media now known or hereafter\n" +
                    "                    created. You represent and warrant that any posting and use of your Content by the Company will not infringe or violate the rights of any\n" +
                    "                    third party.\n" +
                    "                </p>\n" +
                    "            </li>\n" +
                    "            <li>\n" +
                    "                <p lang=\"en-US\">\n" +
                    "                    In addition to the types of Content described in Section 8(a) above, the following is a partial list of the kind of Content that is\n" +
                    "                    prohibited in the Service. You may not post, upload, display or otherwise make available Content that:\n" +
                    "                </p>\n" +
                    "                <ul>\n" +
                    "                    <li>\n" +
                    "                        <p lang=\"en-US\">\n" +
                    "                            that promotes racism, bigotry, hatred or physical harm of any kind against any group or individual;\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p lang=\"en-US\">\n" +
                    "                            advocates harassment or intimidation of another person;\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p lang=\"en-US\">\n" +
                    "                            requests money from, or is intended to otherwise defraud, other users of the Service;\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p lang=\"en-US\">\n" +
                    "                            involves the transmission of “junk mail”, “chain letters,” or unsolicited mass mailing or “spamming” (or “spimming”, “phishing”,\n" +
                    "                            “trolling” or similar activities);\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p lang=\"en-US\">\n" +
                    "                            promotes information that is false or misleading, or promotes illegal activities or conduct that is defamatory, libelous or\n" +
                    "                            otherwise objectionable;\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p lang=\"en-US\">\n" +
                    "                            promotes an illegal or unauthorized copy of another person’s copyrighted work, such as providing pirated computer programs or links\n" +
                    "                            to them, providing information to circumvent manufacture- installed copy-protect devices, or providing pirated images, audio or\n" +
                    "                            video, or links to pirated images, audio or video files;\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p lang=\"en-US\">\n" +
                    "                            contains video, audio photographs, or images of another person without his or her permission (or in the case of a minor, the\n" +
                    "                            minor’s legal guardian);\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p lang=\"en-US\">\n" +
                    "                            contains restricted or password only access pages, or hidden pages or images (those not linked to or from another accessible page);\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p lang=\"en-US\">\n" +
                    "                            provides material that exploits people in a sexual, violent or other illegal manner, or solicits personal information from anyone\n" +
                    "                            under the age of 18;\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p lang=\"en-US\">\n" +
                    "                            provides instructional information about illegal activities such as making or buying illegal weapons or drugs, violating someone’s\n" +
                    "                            privacy, or providing, disseminating or creating computer viruses;\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p lang=\"en-US\">\n" +
                    "                            contains viruses, time bombs, trojan horses, cancelbots, worms or other harmful, or disruptive codes, components or devices;\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p lang=\"en-US\">\n" +
                    "                            impersonates, or otherwise misrepresents affiliation, connection or association with, any person or entity;\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p lang=\"en-US\">\n" +
                    "                            provides information or data you do not have a right to make available under law or under contractual or fiduciary relationships\n" +
                    "                            (such as inside information, proprietary and confidential information);\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p lang=\"en-US\">\n" +
                    "                            disrupts the normal flow of dialogue, causes a screen to “scroll” faster than other users are able to type, or otherwise negatively\n" +
                    "                            affects other users’ ability to engage in real time exchanges;\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p lang=\"en-US\">\n" +
                    "                            solicits passwords or personal identifying information for commercial or unlawful purposes from other users or disseminates another\n" +
                    "                            person’s personal information without his or her permission; and\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                    <li>\n" +
                    "                        <p lang=\"en-US\">\n" +
                    "                            publicizes or promotes commercial activities and/or sales without our prior written consent such as contests, sweepstakes, barter,\n" +
                    "                            advertising, and pyramid schemes.\n" +
                    "                        </p>\n" +
                    "                    </li>\n" +
                    "                </ul>\n" +
                    "            </li>\n" +
                    "        </ol>\n" +
                    "    </li>\n" +
                    "</ol>\n" +
                    "<p lang=\"en-US\">\n" +
                    "    The Company reserves the right, in its sole discretion, to investigate and take any legal action against anyone who violates this provision, including\n" +
                    "    removing the offending communication from the Service and terminating or suspending the account of such violators.\n" +
                    "</p>\n" +
                    "<ol>\n" +
                    "    <ol type=\"a\">\n" +
                    "        <li>\n" +
                    "            <p lang=\"en-US\">\n" +
                    "                Your use of the Service, including all Content you post through the Service, must comply with all applicable laws and regulations. You agree\n" +
                    "                that the Company may access, preserve and disclose your account information and Content if required to do so by law or in a good faith belief\n" +
                    "                that such access, preservation or disclosure is reasonably necessary, such as to: (i) comply with legal process; (ii) enforce this Agreement;\n" +
                    "                (iii) respond to claims that any Content violates the rights of third parties; (iv) respond to your requests for customer service or allow you\n" +
                    "                to use the Service in the future; or (v) protect the rights, property or personal safety of the Company or any other person.\n" +
                    "            </p>\n" +
                    "        </li>\n" +
                    "        <li>\n" +
                    "            <p lang=\"en-US\">\n" +
                    "                You agree that any Content you place on the Service may be viewed by other users and may be viewed by any person visiting or participating in\n" +
                    "                the Service.\n" +
                    "            </p>\n" +
                    "        </li>\n" +
                    "    </ol>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            <strong>Prohibited Activities.</strong>\n" +
                    "            The Company reserves the right to investigate, suspend and/or terminate your account if you have misused the Service or behaved in a way the\n" +
                    "            Company regards as inappropriate or unlawful, including actions or communications the occur off the Service but involve users you meet through the\n" +
                    "            Service. The following is a partial list of the type of actions that you may not engage in with respect to the Service. You will not:\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "</ol>\n" +
                    "<ul>\n" +
                    "</ul>\n" +
                    "<ol type=\"a\">\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            impersonate any person or entity.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            solicit money from any users.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            post any Content that is prohibited by Section 9.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            “stalk” or otherwise harass any person.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            express or imply that any statements you make are endorsed by the Company without our specific prior written consent.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            use the Service in an illegal manner or to commit an illegal act;\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            access the Service in a jurisdiction in which it is illegal or unauthorized;\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            ask or use users to conceal the identity, source, or destination of any illegally gained money or products.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            use any robot, spider, site search/retrieval application, or other manual or automatic device or process to retrieve, index, “data mine”, or in any\n" +
                    "            way reproduce or circumvent the navigational structure or presentation of the Service or its contents.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            collect usernames and/or email addresses of users by electronic or other means for the purpose of sending unsolicited email or unauthorized framing\n" +
                    "            of or linking to the Service.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            interfere with or disrupt the Service or the servers or networks connected to the Service.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            email or otherwise transmit any material that contains software viruses or any other computer code, files or programs designed to interrupt,\n" +
                    "            destroy or limit the functionality of any computer software or hardware or telecommunications equipment.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            forge headers or otherwise manipulate identifiers in order to disguise the origin of any information transmitted to or through the Service (either\n" +
                    "            directly or indirectly through use of third party software).\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            “frame” or “mirror” any part of the Service, without the Company's prior written authorization.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            use meta tags or code or other devices containing any reference to the Company or the Service (or any trademark, trade name, service mark, logo or\n" +
                    "            slogan of the Company) to direct any person to any other website for any purpose.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            modify, adapt, sublicense, translate, sell, reverse engineer, decipher, decompile or otherwise disassemble any portion of the Service any software\n" +
                    "            used on or for the Service, or cause others to do so.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            post, use, transmit or distribute, directly or indirectly, (e.g. screen scrape) in any manner or media any content or information obtained from the\n" +
                    "            Service other than solely in connection with your use of the Service in accordance with this Agreement.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "</ol>\n" +
                    "<li>\n" +
                    "    <p lang=\"en-US\">\n" +
                    "        <strong>Customer Service.</strong>\n" +
                    "        The Company provides assistance and guidance through its customer care representatives. When communicating with our customer care representatives, you\n" +
                    "        agree to not be abusive, obscene, profane, offensive, sexist, threatening, harassing, racially offensive, or to not otherwise behave inappropriately.\n" +
                    "        If we feel that your behavior towards any of our customer care representatives or other employees is at any time threatening or offensive, we reserve\n" +
                    "        the right to immediately terminate your account.\n" +
                    "    </p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "    <p lang=\"en-US\">\n" +
                    "        <strong>In App Purchases.</strong>\n" +
                    "        From time to time, Champy may offer additional products and services for purchase through the App Store ℠, Google Play or other application platforms\n" +
                    "        (“in app purchases”). If you choose to make an in app purchase, you will be prompted to enter details for your account with the mobile platform you are\n" +
                    "        using (e.g., Apple, Android, etc.) (“your IAP Account”), and your IAP Account will be charged for the in app purchase in accordance with the terms\n" +
                    "        disclosed to you at the time of purchase as well as the general terms for in app purchases that apply to your IAP Account. In app purchases may include\n" +
                    "        a free trial period. At the end of the free trial period, you will be charged the price of the subscription and will continue to be charged until you\n" +
                    "        cancel your subscription. To avoid any charges, you must cancel before the end of the trial period. If you purchase an auto-recurring periodic\n" +
                    "        subscription through an in app purchase, your IAP Account will be billed continuously for the subscription until you cancel in accordance with the\n" +
                    "        platform terms. In call cases, please refer to the terms of your application platform which apply to your in app purchases.\n" +
                    "    </p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "    <p lang=\"en-US\">\n" +
                    "        <strong>Modifications to Service.</strong>\n" +
                    "        The Company reserves the right at any time to modify or discontinue, temporarily or permanently, the Service (or any part thereof) with or without\n" +
                    "        notice. You agree that the Company shall not be liable to you or to any third party for any modification, suspension or discontinuance of the Service.\n" +
                    "        To protect the integrity of the Service, the Company reserves the right at any time in its sole discretion to block users from certain IP addresses\n" +
                    "        from accessing the Service.\n" +
                    "    </p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "    <p lang=\"en-US\">\n" +
                    "        <strong>Disclaimers.</strong>\n" +
                    "    </p>\n" +
                    "</li>\n" +
                    "<ol type=\"a\">\n" +
                    "    <ol type=\"a\">\n" +
                    "        <li>\n" +
                    "            <p lang=\"en-US\">\n" +
                    "                You acknowledge and agree that neither the Company nor its affiliates and third party partners are responsible for and shall not have any\n" +
                    "                liability, directly or indirectly, for any loss or damage, including personal injury or death, as a result of or alleged to be the result of\n" +
                    "                (i) any incorrect or inaccurate Content posted in the Service, whether caused by users or any of the equipment or programming associated with\n" +
                    "                or utilized in the Service; (ii) the timeliness, deletion or removal, incorrect delivery or failure to store any Content, communications or\n" +
                    "                personalization settings; (iii) the conduct, whether online or offline, of any user; (iv) any error, omission or defect in, interruption,\n" +
                    "                deletion, alteration, delay in operation or transmission, theft or destruction of, or unauthorized access to, any user or user communications;\n" +
                    "                or (v) any problems, failure or technical malfunction of any telephone network or lines, computer online systems, servers or providers,\n" +
                    "                computer equipment, software, failure of email or players on account of technical problems or traffic congestion on the Internet or at any\n" +
                    "                website or combination thereof, including injury or damage to users or to any other person’s computer or device related to or resulting from\n" +
                    "                participating or downloading materials in connection with the Internet and/or in connection with the Service.\n" +
                    "                <strong>\n" +
                    "                    TO THE MAXIMUM EXTENT ALLOWED BY APPLICABLE LAW, THE COMPANY PROVIDES THE SERVICE ON AN “AS IS” AND “AS AVAILABLE” BASIS AND GRANTS NO\n" +
                    "                    WARRANTIES OF ANY KIND, WHETHER EXPRESS, IMPLIED, STATUTORY OR OTHERWISE WITH RESPECT TO THE SERVICE (INCLUDING ALL CONTENT CONTAINED\n" +
                    "                    THEREIN), INCLUDING (WITHOUT LIMITATION) ANY IMPLIED WARRANTIES OF SATISFACTORY QUALITY, MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE\n" +
                    "                    OR NON-INFRINGEMENT. THE COMPANY DOES NOT REPRESENT OR WARRANT THAT THE SERVICE WILL BE UNINTERRUPTED OR ERROR FREE, SECURE OR THAT ANY\n" +
                    "                    DEFECTS OR ERRORS IN THE SERVICE WILL BE CORRECTED.\n" +
                    "                </strong>\n" +
                    "            </p>\n" +
                    "        </li>\n" +
                    "        <li>\n" +
                    "            <p lang=\"en-US\">\n" +
                    "                ANY MATERIAL DOWNLOADED OR OTHERWISE OBTAINED THROUGH THE USE OF THE SERVICE IS ACCESSED AT YOUR OWN DISCRETION AND RISK, AND YOU WILL BE\n" +
                    "                SOLELY RESPONSIBLE FOR AND HEREBY WAIVE ANY AND ALL CLAIMS AND CAUSES OF ACTION WITH RESPECT TO ANY DAMAGE TO YOUR DEVICE, COMPUTER SYSTEM,\n" +
                    "                INTERNET ACCESS, DOWNLOAD OR DISPLAY DEVICE, OR LOSS OR CORRUPTION OF DATA THAT RESULTS OR MAY RESULT FROM THE DOWNLOAD OF ANY SUCH MATERIAL.\n" +
                    "                IF YOU DO NOT ACCEPT THIS LIMITATION OF LIABILITY, YOU ARE NOT AUTHORIZED TO DOWNLOAD OR OBTAIN ANY MATERIAL THROUGH THE SERVICE.\n" +
                    "            </p>\n" +
                    "        </li>\n" +
                    "        <li>\n" +
                    "            <p lang=\"en-US\">\n" +
                    "                From time to time, the Company may make third party opinions, advice, statements, offers, or other third party information or content available\n" +
                    "                through the Service. All third party content is the responsibility of the respective authors thereof and should not necessarily be relied upon.\n" +
                    "                Such third party authors are solely responsible for such content. THE COMPANY DOES NOT: (I) GUARANTEE THE ACCURACY, COMPLETENESS, OR USEFULNESS\n" +
                    "                OF ANY THIRD PARTY CONTENT PROVIDED THROUGH THE SERVICE, OR (II) ADOPT, ENDORSE OR ACCEPT RESPONSIBILITY FOR THE ACCURACY OR RELIABILITY OF ANY\n" +
                    "                OPINION, ADVICE, OR STATEMENT MADE BY ANY PARTY THAT APPEARS IN THE SERVICE. UNDER NO CIRCUMSTANCES WILL THE COMPANY OR ITS AFFILIATES BE\n" +
                    "                RESPONSIBLE OR LIABLE FOR ANY LOSS OR DAMAGE RESULTING FROM YOUR RELIANCE ON INFORMATION OR OTHER CONTENT POSTED IN THE SERVICE, OR TRANSMITTED\n" +
                    "                TO OR BY ANY USERS.\n" +
                    "            </p>\n" +
                    "        </li>\n" +
                    "        <li>\n" +
                    "            <p lang=\"en-US\">\n" +
                    "                In addition to the preceding paragraph and other provisions of this Agreement, any advice that may be posted in the Service is for\n" +
                    "                informational and entertainment purposes only and is not intended to replace or substitute for any professional financial, medical, legal, or\n" +
                    "                other advice. The Company makes no representations or warranties and expressly disclaims any and all liability concerning any treatment, action\n" +
                    "                by, or effect on any person following the information offered or provided within or through the Service. If you have specific concerns or a\n" +
                    "                situation arises in which you require professional or medical advice, you should consult with an appropriately trained and qualified\n" +
                    "                specialist.\n" +
                    "            </p>\n" +
                    "        </li>\n" +
                    "    </ol>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            <strong>Links.</strong>\n" +
                    "            The Service may contain, and the Service or third parties may provide, advertisements and promotions offered by third parties and links to other\n" +
                    "            web sites or resources. You acknowledge and agree that the Company is not responsible for the availability of such external websites or resources,\n" +
                    "            and does not endorse and is not responsible or liable for any content, information, statements, advertising, goods or services, or other materials\n" +
                    "            on or available from such websites or resources. Your correspondence or business dealings with, or participation in promotions of, third parties\n" +
                    "            found in or through the Service, including payment and delivery of related goods or services, and any other terms, conditions, warranties or\n" +
                    "            representations associated with such dealings, are solely between you and such third party. You further acknowledge and agree that the Company\n" +
                    "            shall not be responsible or liable, directly or indirectly, for any damage or loss caused or alleged to be caused by or in connection with the use\n" +
                    "            of, or reliance upon, any such content, information, statements, advertising, goods or services or other materials available on or through any such\n" +
                    "            website or resource.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            <strong>\n" +
                    "                Limitation on Liability. TO THE FULLEST EXTENT ALLOWED BY APPLICABLE LAW, IN NO EVENT WILL THE COMPANY, ITS AFFILIATES, BUSINESS PARTNERS,\n" +
                    "                LICENSORS OR SERVICE PROVIDERS BE LIABLE TO YOU OR ANY THIRD PERSON FOR ANY INDIRECT, RELIANCE, CONSEQUENTIAL, EXEMPLARY, INCIDENTAL, SPECIAL\n" +
                    "                OR PUNITIVE DAMAGES, INCLUDING, WITHOUT LIMITATION, LOSS OF PROFITS, LOSS OF GOODWILL, DAMAGES FOR LOSS, CORRUPTION OR BREACHES OF DATA OR\n" +
                    "                PROGRAMS, SERVICE INTERRUPTIONS AND PROCUREMENT OF SUBSTITUTE SERVICES, EVEN IF THE COMPANY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH\n" +
                    "                DAMAGES. NOTWITHSTANDING ANYTHING TO THE CONTRARY CONTAINED HEREIN, THE COMPANY'S LIABILITY TO YOU FOR ANY CAUSE WHATSOEVER, AND REGARDLESS OF\n" +
                    "                THE FORM OF THE ACTION, WILL AT ALL TIMES BE LIMITED TO THE AMOUNT PAID, IF ANY, BY YOU TO THE COMPANY FOR THE SERVICE WHILE YOU HAVE AN\n" +
                    "                ACCOUNT. YOU AGREE THAT REGARDLESS OF ANY STATUTE OR LAW TO THE CONTRARY, ANY CLAIM OR CAUSE OF ACTION ARISING OUT OF OR RELATED TO USE OF THE\n" +
                    "                SERVICE OR THE TERMS OF THIS AGREEMENT MUST BE FILED WITHIN ONE YEAR AFTER SUCH CLAIM OR CAUSE OF ACTION AROSE OR BE FOREVER BARRED.\n" +
                    "            </strong>\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            <strong>Arbitration and Governing Law.</strong>\n" +
                    "        </p>\n" +
                    "        <ol type=\"a\">\n" +
                    "            <li>\n" +
                    "                <p lang=\"en-US\">\n" +
                    "                    The exclusive means of resolving any dispute or claim arising out of or relating to this Agreement (including any alleged breach thereof)\n" +
                    "                    or the Service shall be <strong>BINDING ARBITRATION</strong> administered by the American Arbitration Association. The one exception to the\n" +
                    "                    exclusivity of arbitration is that you have the right to bring an individual claim against the Company in a small-claims court of competent\n" +
                    "                    jurisdiction. But whether you choose arbitration or small-claims court, you may not under any circumstances commence or maintain against\n" +
                    "                    the Company any class action, class arbitration, or other representative action or proceeding.\n" +
                    "                </p>\n" +
                    "            </li>\n" +
                    "            <li>\n" +
                    "                <p lang=\"en-US\">\n" +
                    "By using the Service in any manner, you agree to the above arbitration agreement. In doing so,                    <strong>YOU GIVE UP YOUR RIGHT TO GO TO COURT</strong> to assert or defend any claims between you and the Company (except for matters that\n" +
                    "may be taken to small-claims court).                    <strong>YOU ALSO GIVE UP YOUR RIGHT TO PARTICIPATE IN A CLASS ACTION OR OTHER CLASS PROCEEDING.</strong> Your rights will be determined by\n" +
                    "                    a <strong>NEUTRAL ARBITRATOR, NOT A JUDGE OR JURY.</strong> You are entitled to a fair hearing before the arbitrator. The arbitrator can\n" +
                    "                    grant any relief that a court can, but you should note that arbitration proceedings are usually simpler and more streamlined than trials\n" +
                    "                    and other judicial proceedings. Decisions by the arbitrator are enforceable in court and may be overturned by a court only for very limited\n" +
                    "                    reasons. For details on the arbitration process, see our Arbitration Procedures.\n" +
                    "                </p>\n" +
                    "            </li>\n" +
                    "            <li>\n" +
                    "                <p lang=\"en-US\">\n" +
                    "                    Any proceeding to enforce this arbitration agreement, including any proceeding to confirm, modify, or vacate an arbitration award, may be\n" +
                    "                    commenced in any court of competent jurisdiction. In the event that this arbitration agreement is for any reason held to be unenforceable,\n" +
                    "                    any litigation against the Company (except for small-claims court actions) may be commenced only in the federal or state courts located in\n" +
                    "                    Dallas County, Texas. You hereby irrevocably consent to the jurisdiction of those courts for such purposes.\n" +
                    "                </p>\n" +
                    "            </li>\n" +
                    "            <li>\n" +
                    "                <p lang=\"en-US\">\n" +
                    "                    This Agreement, and any dispute between you and the Company, shall be governed by the laws of the state of Texas without regard to\n" +
                    "                    principles of conflicts of law, provided that this arbitration agreement shall be governed by the Federal Arbitration Act.\n" +
                    "                </p>\n" +
                    "            </li>\n" +
                    "        </ol>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            <a name=\"_GoBack\"></a>\n" +
                    "            <strong>Indemnity by You.</strong>\n" +
                    "            You agree to indemnify and hold the Company, its subsidiaries, and affiliates, and its and their officers, agents, partners and employees, harmless\n" +
                    "            from any loss, liability, claim, or demand, including reasonable attorney's fees, made by any third party due to or arising out of your breach of\n" +
                    "            or failure to comply with this Agreement (including any breach of your representations and warranties contained herein), any postings or Content\n" +
                    "            you post in the Service, and the violation of any law or regulation by you. The Company reserves the right to assume the exclusive defense and\n" +
                    "            control of any matter otherwise subject to indemnification by you, in which event you will fully cooperate with the Company in connection\n" +
                    "            therewith.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            <strong>Notice.</strong>\n" +
                    "            The Company may provide you with notices, including those regarding changes to this Agreement, using any reasonable means now known or hereafter\n" +
                    "            developed, including by email, regular mail, SMS, MMS, text message or postings in the Service. Such notices may not be received if you violate\n" +
                    "            this Agreement by accessing the Service in an unauthorized manner. You agree that you are deemed to have received any and all notices that would\n" +
                    "            have been delivered had you accessed the Service in an authorized manner.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            <strong>Entire Agreement; Other.</strong>\n" +
                    "            This Agreement, with the Privacy Policy and any specific guidelines or rules that are separately posted for particular services or offers in the\n" +
                    "            Service, contains the entire agreement between you and the Company regarding the use of the Service. If any provision of this Agreement is held\n" +
                    "            invalid, the remainder of this Agreement shall continue in full force and effect. The failure of the Company to exercise or enforce any right or\n" +
                    "            provision of this Agreement shall not constitute a waiver of such right or provision. You agree that your online account is non-transferable and\n" +
                    "            all of your rights to your profile or contents within your account terminate upon your death. No agency, partnership, joint venture or employment\n" +
                    "            is created as a result of this Agreement and you may not make any representations or bind the Company in any manner.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "    <li>\n" +
                    "        <p lang=\"en-US\">\n" +
                    "            <strong>Amendment.</strong>\n" +
                    "            This Agreement is subject to change by the Company at any time.\n" +
                    "        </p>\n" +
                    "    </li>\n" +
                    "</ol>";
            return (Html.fromHtml(text)).toString();
        }

        protected void onPostExecute(String result) {
            // Do your staff here to save image
            tvTerms.setText(result, TextView.BufferType.SPANNABLE);
            tvTerms.setVisibility(View.VISIBLE);
        }
    }
}
