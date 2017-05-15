package camelcase.searchemall;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private Util mUtil;

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
        // TODO : replace this with fetched urls
        mUtil = new Util(getContext());
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
        ArrayList<WebPageProperties> result = new ArrayList<>();
        String Urls = mUtil.readFile(asset);
        String[] UrlList = Urls.split(";");
        for (String s : UrlList) {
            String[] info = s.split(",");
            result.add(new WebPageProperties(info[0], info[1], Boolean.parseBoolean(info[2])));
        }
        return result;
    }

    private String getAssetName() {
        if (mSearchScope.equalsIgnoreCase("web")) return "search_web";
        else if (mSearchScope.equalsIgnoreCase("images")) return "search_images";
        else if (mSearchScope.equalsIgnoreCase("videos")) return "search_videos";
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

    private class myViewPagerAdapter extends FragmentStatePagerAdapter {

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
