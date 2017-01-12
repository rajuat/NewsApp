package com.itservz.paomacha.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.itservz.paomacha.android.R;

/**
 * Fragment to manage the right page of the 5 pages application navigation (top, center, bottom, left, right).
 */
public class RightFragment extends Fragment {

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

		return fragmentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		webview.loadUrl(originalNewsUrl);
	}
}
