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
import android.widget.Toast;

import static android.content.Intent.ACTION_VIEW;

public class MainActivity extends AppCompatActivity implements
        MainFragment.MainFragmentListener,
        ViewPagerFragment.ViewPagerFragmentListener,
        WebViewFragment.WebviewFragmentListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private String mCurrentUrl = "";
    private MainFragment mainFragment;
    private ViewPagerFragment viewPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFragment = new MainFragment();
        viewPagerFragment = new ViewPagerFragment();

        initUiComponents();
        initMainFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                    Toast.makeText(this, "Unable to open url " + mCurrentUrl, Toast.LENGTH_SHORT).show();
                break;

        }
        return true;
    }

    // method to get search info from main fragment
    @Override
    public void getSearchInfo(String query, String scope) {
        if (!query.equals("")) {
            viewPagerFragment.setSearchQueryAndScope(query, scope);
            Menu menu = mToolbar.getMenu();
            menu.clear();
            mToolbar.inflateMenu(R.menu.webview_menu);
            manageFragment(viewPagerFragment, true);
        } else Toast.makeText(this, "please enter text", Toast.LENGTH_SHORT).show();
    }

    private void initUiComponents() {
        mToolbar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(mToolbar);
    }

    private boolean initMainFragment() {
        manageFragment(mainFragment, false);
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

    private void initMainFragmentMenu() {
        Menu menu = mToolbar.getMenu();
        menu.clear();
    }

    @Override
    public void onDetachCalled() {
        initMainFragmentMenu();
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
