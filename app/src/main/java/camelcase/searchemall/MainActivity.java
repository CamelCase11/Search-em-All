package camelcase.searchemall;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import static android.content.Intent.ACTION_VIEW;

public class MainActivity extends AppCompatActivity implements
        MainFragment.MainFragmentListener,
        ViewPagerFragment.ViewPagerFragmentListener,
        WebViewFragment.WebviewFragmentListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private String mCurrentUrl = "";
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMainFragment();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.webview_menu_openInBrowser:
                if (mCurrentUrl.startsWith("http://") || mCurrentUrl.startsWith("https://")
                        || mCurrentUrl.startsWith("market://") || mCurrentUrl.startsWith("magnet:?")) {
                    Toast.makeText(this, "Opening External : " + mCurrentUrl, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ACTION_VIEW, Uri.parse(mCurrentUrl));
                    this.startActivity(i);
                } else
                    Toast.makeText(this, "Please open a link first " + mCurrentUrl, Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    // method to get search info from main fragment
    @Override
    public void getSearchInfo(String query, String scope) {
        performSearch(query, scope);
    }

    private void performSearch(String query, String scope) {
        if (!query.equals("")) {
            ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
            viewPagerFragment.setSearchQueryAndScope(query, scope);
            mMenu.clear();
            getMenuInflater().inflate(R.menu.webview_menu,mMenu);
            manageFragment(viewPagerFragment, true);
        } else Toast.makeText(this, "please enter text", Toast.LENGTH_SHORT).show();
    }

    private boolean initMainFragment() {
        manageFragment(new MainFragment(), false);
        return true;
    }

    private boolean manageFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment, "mainFragment");
        if (addToBackStack) ft.addToBackStack(null);
        ft.commit();
        return true;
    }

    @Override
    public void onDetachCalled() {
        mMenu.clear();
    }

    @Override
    public void onPageSelected() {
        mCurrentUrl = "";
    }

    @Override
    public void onPageScrolled() {
        mCurrentUrl = "";
    }

    @Override
    public void getCurrentUrl(String url) {
        mCurrentUrl = url;
    }
}
