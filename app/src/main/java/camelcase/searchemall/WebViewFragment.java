package camelcase.searchemall;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.Serializable;

import static android.content.Intent.ACTION_VIEW;

public class WebViewFragment extends Fragment implements Serializable {

    private final String TAG = WebViewFragment.class.getSimpleName();
    WebviewFragmentListener webviewFragmentListener;
    private String mSearchQuery = "";
    private String mUrl = "";
    private boolean mJsEnable = false;
    private FloatingActionButton fab;
    private WebView mWebView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PackageManager mPm;

    public WebViewFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        webviewFragmentListener = (WebviewFragmentListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.webview_fragment, container, false);
        mWebView = (WebView) v.findViewById(R.id.webview_fragment_webview);
        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.webview_fragment_progressbar);

        fab = (FloatingActionButton) v.findViewById(R.id.webview_fragment_goBack);

        fab.setOnClickListener(myListener);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeToRefresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });

        WebSettings webSettings = mWebView.getSettings();

        webSettings.setJavaScriptEnabled(mJsEnable);

        mPm = getActivity().getPackageManager();

        mWebView.loadUrl(mUrl + mSearchQuery);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                if (mWebView.canGoBack()){

                    // check if any app is available to perform action or not.
                    if (url.startsWith("magnet:")
                            || url.startsWith("market://")
                            || url.startsWith("https://play.google.com")
                            || url.endsWith(".torrent")) {

                        Intent i = new Intent(ACTION_VIEW, Uri.parse(url));

                        ResolveInfo info = mPm.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY);
                        if (info != null) {
                            getActivity().startActivity(i);
                        } else {
                            Toast.makeText(getContext(), "No torrent client found, Please install one.", Toast.LENGTH_SHORT).show();
                            getActivity().startActivity(new Intent(ACTION_VIEW, Uri.parse("market://details?id=org.proninyaroslav.libretorrent")));
                        }

                    } else {
                        webviewFragmentListener.getCurrentUrl(url);
                    }
                }
                else webviewFragmentListener.getCurrentUrl("");
            }
        });
        WebView.setWebContentsDebuggingEnabled(false);
        return v;
    }

    // method to get search parameters from other fragment/activity
    public void getSearchParams(String query, String url, boolean isJsEnable) {
        mSearchQuery = query;
        mUrl = url;
        mJsEnable = isJsEnable;
    }

    public interface WebviewFragmentListener {
        void getCurrentUrl(String url);
    }

    private View.OnClickListener myListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                Toast.makeText(getContext(), "Can not go back", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
