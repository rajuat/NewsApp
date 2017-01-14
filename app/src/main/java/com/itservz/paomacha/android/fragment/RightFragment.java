package com.itservz.paomacha.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.itservz.paomacha.android.R;

/**
 * Fragment to manage the right page of the 5 pages application navigation (top, center, bottom, left, right).
 */
public class RightFragment extends Fragment /*implements RightFragment.UrlLoader*/{

	private WebView webview;
	private String originalNewsUrl;
	private static final String TAG = "RightFragment";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		originalNewsUrl = this.getArguments().getString("originalNewsUrl");
		View fragmentView = inflater.inflate(R.layout.fragment_right, container, false);
		webview = (WebView) fragmentView.findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setUseWideViewPort(true);
		webview.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
			}
		});
        final ProgressBar progressBar = (ProgressBar) fragmentView.findViewById(R.id.progressBar);
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);

                } else {
                    progressBar.setVisibility(View.VISIBLE);

                }
            }
        });

		return fragmentView;
	}

    public void load() {
        Log.d(TAG, "load");
        webview.loadUrl(originalNewsUrl);
    }


	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				return true;
		}
		return false;
	}

}
