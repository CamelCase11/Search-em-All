package camelcase.searchemall;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;

public class ViewPagerFragment extends Fragment implements Serializable {

    private static String mSearchScope;
    private final String TAG = ViewPagerFragment.class.getSimpleName();
    ViewPagerFragmentListener viewPagerFragmentListener;

    private int mArrayListSize = 0;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private Context mContext;
    private String mSearchQuery;
    private ArrayList<WebPageProperties> mProperties;
    private WebPageProperties mCurrentPage;
    private WebViewFragment webViewFragment;
    private String mCurrentPageName;
    private TabLayout mTabLayout;

    public ViewPagerFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewPagerFragmentListener = (ViewPagerFragmentListener) context;
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager_fragment, container, false);
        mProperties = readAssets(getAssetName());
        mArrayListSize = mProperties.size();
        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        initViewPager(view);
        mTabLayout.setupWithViewPager(mViewPager);
        return view;
    }

    // get search query and search scope from main fragment
    public void setSearchQueryAndScope(String query, String scope) {
        mSearchQuery = query;
        mSearchScope = scope;
    }

    private void initViewPager(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager_fragment_viewPager);
        mPagerAdapter = new myViewPagerAdapter(getFragmentManager());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                viewPagerFragmentListener.onPageScrolled();
            }

            @Override
            public void onPageSelected(int position) {
                viewPagerFragmentListener.onPageSelected();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(mPagerAdapter);
    }

    // read assets from file
    private ArrayList<WebPageProperties> readAssets(String asset) {
        InputStream is = null;
        try {
            is = mContext.getAssets().open(asset);
        } catch (IOException e) {
            Log.e(TAG, "readAssets: Error reading assets",e );
        }
        ArrayList<WebPageProperties> result = new ArrayList<>();
        String url;
        assert is != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            while ((url = reader.readLine()) != null) {
                String[] info = url.split(",");
                result.add(new WebPageProperties(info[0], info[1], Boolean.parseBoolean(info[2])));
            }
        } catch (IOException e) {
            Log.e(TAG, "readAssets: Error reading Line",e );
        }
        return result;
    }

    private String getAssetName() {
        if (mSearchScope.equalsIgnoreCase("web")) return "search_web";
        else if (mSearchScope.equalsIgnoreCase("images")) return "search_images";
        else if (mSearchScope.equalsIgnoreCase("videos")) return "search_videos";
//        else if (mSearchScope.equalsIgnoreCase("music")) return "search_music"; // currently not working
        else if (mSearchScope.equalsIgnoreCase("torrents")) return "search_torrents";
        else if (mSearchScope.equalsIgnoreCase("books and articles")) return "search_books";
        else return "serch_web";
    }

    @Override
    public void onDetach() {
        super.onDetach();
        viewPagerFragmentListener.onDetachCalled();
    }

    public interface ViewPagerFragmentListener {
        void onDetachCalled();

        void onPageScrolled();

        void onPageSelected();
    }

    private class myViewPagerAdapter extends FragmentPagerAdapter {

        public myViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            mCurrentPage = mProperties.get(position);
            webViewFragment = new WebViewFragment();
            String url = mCurrentPage.getWebPageUrl();
            boolean isJsEnable = mCurrentPage.isJsEnable();
            mCurrentPageName = mCurrentPage.getPageName();
            webViewFragment.getSearchParams(mSearchQuery, url, isJsEnable);
            return webViewFragment;
        }

        @Override
        public int getCount() {
            return mArrayListSize;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mProperties.get(position).getPageName();
        }
    }
}
