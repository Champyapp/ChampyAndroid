package com.azinecllc.champy.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.azinecllc.champy.R;
import com.azinecllc.champy.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * @autor SashaKhyzhun
 * Created on 4/3/17.
 */

public class SettingsProfileFragment extends Fragment {

    @BindView(R.id.iv_change_photo)
    ImageView ivChangePhoto;
    @BindView(R.id.tv_logout)
    TextView tvLogout;
    @BindView(R.id.tv_delete_account)
    TextView tvDeleteAcc;
    @BindView(R.id.tv_first_name)
    TextView tvFirstName;
    @BindView(R.id.tv_last_name)
    TextView tvLastName;
    private SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = SessionManager.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_profile, container, false);
        ButterKnife.bind(this, view);

//        ExpandableHeightListView listView = (ExpandableHeightListView) view.findViewById(R.id.list_view_colors);
//
//        Integer[] intColors = new Integer[]{
//                Color.RED,
//                Color.parseColor("#FFCF670C"),
//                Color.YELLOW,
//                Color.GREEN,
//                Color.BLUE,
//                Color.parseColor("#FF7209DA"),
//                Color.parseColor("#FFEC03DC")
//        };
//
//        String[] stringColors = new String[]{
//                "Red",
//                "Orange",
//                "Yellow",
//                "Green",
//                "Blue",
//                "Purple",
//                "Pink"
//        };
//
//        ArrayAdapter<String> adapterColorText = new ArrayAdapter<String>(
//                getContext(),
//                R.layout.item_list_view_colors,
//                R.id.text_view_color,
//                stringColors
//        );
//        ArrayAdapter<Integer> adapterColorHex = new ArrayAdapter<Integer>(
//                getContext(),
//                R.layout.item_list_view_colors,
//                R.id.text_view_color,
//                intColors
//        );
//
//
//
//
//        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        listView.setAdapter(adapterColorText);
//        listView.setExpanded(true); // This actually do the magic
//        listView.setAdapter(adapterColor);

        ImageView ivUserPhotoBG = (ImageView) view.findViewById(R.id.iv_profile_picture_bg);
        ImageView ivUserPhoto = (ImageView) view.findViewById(R.id.iv_profile_picture);

//        ImageView ivChangePhoto = (ImageView) view.findViewById(R.id.iv_change_photo);
//        TextView tvFirstName = (TextView) view.findViewById(R.id.tv_first_name);
//        TextView tvLastName = (TextView) view.findViewById(R.id.tv_last_name);
//        TextView tvLogout = (TextView) view.findViewById(R.id.tv_logout);
//        TextView tvDeleteAcc = (TextView) view.findViewById(R.id.tv_delete_account);

        Switch switchFB = (Switch) view.findViewById(R.id.switch_facebook);


        String userPicture = sessionManager.getUserPicture();
        String userEmail = sessionManager.getUserEmail();
        String userName = sessionManager.getUserName();
        Glide.with(this)
                .load(userPicture)
                .bitmapTransform(new BlurTransformation(getContext(), 25))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(ivUserPhotoBG);
        Glide.with(this)
                .load(userPicture)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(ivUserPhoto);

        tvFirstName.setText(userName);
        tvLastName.setText(userEmail);


        return view;
    }


    @OnClick(R.id.iv_change_photo)
    public void onClickChangePhoto() {
        Toast.makeText(getContext(), "clicked on the camera", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Title");
        builder.setItems(new CharSequence[]{"From Camera", "From Gallery"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    Toast.makeText(getContext(), "clicked 1", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getContext(), "clicked 2", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        builder.create().show();
    }

//    public static void setListViewHeightBasedOnChildren(ListView listView) {
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null)
//            return;
//
//        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
//        int totalHeight = 0;
//        View view = null;
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            view = listAdapter.getView(i, view, listView);
//            if (i == 0)
//                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));
//
//            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
//            totalHeight += view.getMeasuredHeight();
//        }
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        listView.setLayoutParams(params);
//        listView.requestLayout();
//    }


}
