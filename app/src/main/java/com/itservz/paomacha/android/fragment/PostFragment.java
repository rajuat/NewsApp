package com.itservz.paomacha.android.fragment;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itservz.paomacha.android.PostActivity;
import com.itservz.paomacha.android.R;
import com.itservz.paomacha.android.backend.FirebaseDatabaseService;
import com.itservz.paomacha.android.model.Pao;
import com.itservz.paomacha.android.preference.PrefManager;
import com.itservz.paomacha.android.utils.BitmapHelper;

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
    private PostActivity paoActivity = null;
    private Pao pao = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_edit_central, container, false);
        paoActivity = (PostActivity) getActivity();
        String fileName = this.getArguments().getString("file");
        String mAddressOutput = this.getArguments().getString("mAddressOutput");
        Location mLastLocation = this.getArguments().getParcelable("mLastLocation");
        final Bitmap bitmap = BitmapHelper.decodeSampledBitmapFromFile(paoActivity, fileName);

        pao = new Pao();
        ImageView paopic = (ImageView) fragmentView.findViewById(R.id.edit_paopic);
        paopic.setImageBitmap(bitmap);
        final EditText title = (EditText) fragmentView.findViewById(R.id.edit_title);
        final EditText body = (EditText) fragmentView.findViewById(R.id.edit_body);
        final EditText metadata = (EditText) fragmentView.findViewById(R.id.edit_metadata);
        metadata.setText(mAddressOutput + "Location" + mLastLocation.toString());
        TextView footer = (TextView) fragmentView.findViewById(R.id.edit_footer);
        footer.setText("paoap by " + pao.createdBy + " / " + pao.createdOn);

        Button buttonPost = (Button) fragmentView.findViewById(R.id.button_post);
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pao.createdOn = new Date().toString();
                pao.createdBy = "anonymous fireflies";
                pao.image = BitmapHelper.bitmapToString(bitmap);
                pao.title = "(User post) " + title.getText().toString();
                pao.body = body.getText().toString();
                pao.body = pao.body + "\n" + metadata.getText().toString();
                List<String> tags = new ArrayList<String>();
                tags.add("General Election");
                pao.tags = tags;
                //only when this is true it has to be approve for display
                pao.needsApproval = "true";
                FirebaseDatabaseService.postPao(pao);
                Toast.makeText(paoActivity, "Thanks for sharing the news.", Toast.LENGTH_LONG).show();
                paoActivity.finish();
            }
        });
        return fragmentView;
    }


}
