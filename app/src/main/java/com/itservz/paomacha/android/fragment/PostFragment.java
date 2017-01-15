package com.itservz.paomacha.android.fragment;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itservz.paomacha.android.PaoActivity;
import com.itservz.paomacha.android.PostActivity;
import com.itservz.paomacha.android.R;
import com.itservz.paomacha.android.backend.FirebaseDatabaseService;
import com.itservz.paomacha.android.model.Pao;
import com.itservz.paomacha.android.preference.PrefManager;
import com.itservz.paomacha.android.utils.AddressHelper;
import com.itservz.paomacha.android.utils.BitmapHelper;
import com.itservz.paomacha.android.utils.ScreenSizeScaler;
import com.itservz.paomacha.android.view.FlowLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Fragment to manage the central page of the 5 pages application navigation (top, center, bottom, left, right).
 */
public class PostFragment extends Fragment {
    static final String TAG = "PostFragment";
    private PrefManager prefManager;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private PostActivity postActivity = null;
    private Pao pao = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_edit_central, container, false);
        postActivity = (PostActivity) getActivity();
        String fileName = this.getArguments().getString("file");
        Location mLastLocation = this.getArguments().getParcelable("mLastLocation");
        List<String> cats = this.getArguments().getStringArrayList(PaoActivity.CATEGORY_TAG);
        //final Bitmap bitmap = BitmapHelper.decodeSampledBitmapFromFile(postActivity, fileName);
        final Bitmap bitmap = BitmapHelper.decode(fileName);
        pao = new Pao();
        pao.tags = new ArrayList<String>();

        ImageView paopic = (ImageView) fragmentView.findViewById(R.id.edit_paopic);
        paopic.setImageBitmap(bitmap);
        final TextInputEditText title = (TextInputEditText) fragmentView.findViewById(R.id.edit_title);
        final TextInputEditText body = (TextInputEditText) fragmentView.findViewById(R.id.edit_body);
        final TextInputEditText metadata = (TextInputEditText) fragmentView.findViewById(R.id.edit_metadata);
        //if (postActivity.mAddressOutput == null || postActivity.mAddressOutput.isEmpty()) {
            metadata.setText(AddressHelper.getAddress(postActivity, mLastLocation));
        /*} else {
            metadata.setText(mLastLocation.toString());
        }*/
        final EditText profile = (EditText) fragmentView.findViewById(R.id.profile);
        FlowLayout paoCategoriesLayout = (FlowLayout) fragmentView.findViewById(R.id.pao_categories);
        for (String cat : cats) {
            addCategories(paoCategoriesLayout, cat, pao.tags);
        }

        Button buttonPost = (Button) fragmentView.findViewById(R.id.button_post);
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pao.createdOn = -1 * new Date().getTime();
                String user = profile.getText().toString();
                pao.createdBy = (user != null || user.length() == 0) ? user : "Anonymous";
                pao.image = BitmapHelper.bitmapToString(bitmap);
                pao.title = "(User post) " + title.getText().toString();
                pao.body = body.getText().toString();
                pao.body = pao.body + "\n" + metadata.getText().toString();
                //only when this is true it has to be approve for display
                pao.needsApproval = "true";
                FirebaseDatabaseService.createUserPao(pao);
                Toast.makeText(postActivity, "Thanks for sharing the news.", Toast.LENGTH_LONG).show();
                postActivity.finish();
            }
        });
        return fragmentView;
    }

    private void addCategories(FlowLayout flowLayout, String i, final List<String> tags) {
        LinearLayout linearLayout = new LinearLayout(this.getActivity());
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layout);
        ScreenSizeScaler screenSizeScaler = new ScreenSizeScaler(getResources());
        final int px = screenSizeScaler.getdpAspixel(8);
        linearLayout.setPadding(px, px, px, px);

        final TextView textView = new TextView(this.getActivity());
        textView.setLayoutParams(layout);
        textView.setBackground(getResources().getDrawable(R.drawable.rounded_border));
        textView.setPadding(px, px, px, px);
        textView.setTextColor(getResources().getColor(R.color.primary_dark));
        textView.setText(i);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = textView.getText().toString();
                if ("added".equals(textView.getTag())) {//deselect
                    textView.setBackground(getResources().getDrawable(R.drawable.rounded_border));
                    textView.setPadding(px, px, px, px);
                    if (tags.contains(value)) {
                        tags.remove(value);
                    }
                    textView.setTag("");
                } else {
                    textView.setBackground(getResources().getDrawable(R.drawable.rounded_border_selected));
                    textView.setPadding(px, px, px, px);
                    if (!tags.contains(value)) {
                        tags.add(value);
                    }
                    textView.setTag("added");
                }
            }
        });

        linearLayout.addView(textView);
        flowLayout.addView(linearLayout);
    }


}
