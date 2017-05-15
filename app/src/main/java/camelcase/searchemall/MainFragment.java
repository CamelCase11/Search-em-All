package camelcase.searchemall;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public class MainFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private final String TAG = MainFragment.class.getSimpleName();
    MainFragmentListener mainFragmentListener;
    private EditText mSearchBox;
    private Spinner mSearchScopeSpinner;
    private ImageButton mSearchButton;
    private String mStringSearchScope;
    private String mSearchQuery;
    private Util mUtil;
    private String rootUrl = "https://raw.githubusercontent.com/CamelCase11/SearchEnginesUrls/master/";
    private RelativeLayout mFetchMessageLayout;
    private RelativeLayout mMainFramentComponentsLayout;

    private String[] fileNames = {
            "search_web",
            "search_images",
            "search_torrents",
            "search_videos",
            "search_books"
    };

    public MainFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mainFragmentListener = (MainFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        mUtil = new Util(getContext());
        initViews(view);
        initSpinner();
        listenButtonEvent();
        mFetchMessageLayout = (RelativeLayout) view.findViewById(R.id.fetching_message_layout);
        mMainFramentComponentsLayout = (RelativeLayout) view.findViewById(R.id.main_fragment_components);

        mSearchBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    mSearchQuery = mSearchBox.getText().toString();
                    String searchScope = mStringSearchScope;
                    mainFragmentListener.getSearchInfo(mSearchQuery, searchScope);
                    return true;
                } else return false;
            }
        });

        new UrlFetchTask().execute(
                rootUrl + fileNames[0],
                rootUrl + fileNames[1],
                rootUrl + fileNames[2],
                rootUrl + fileNames[3],
                rootUrl + fileNames[4]
        );

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mStringSearchScope = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean initSpinner() {

        ArrayAdapter<CharSequence> mSpinnerAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.spinner_items,
                android.R.layout.simple_spinner_item);

        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mSearchScopeSpinner.setAdapter(mSpinnerAdapter);
        mSearchScopeSpinner.setOnItemSelectedListener(this);
        return true;
    }

    private boolean listenButtonEvent() {
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchQuery = mSearchBox.getText().toString();
                String searchScope = mStringSearchScope;
                mainFragmentListener.getSearchInfo(mSearchQuery, searchScope);
            }
        });
        return true;
    }

    private void initViews(View view) {
        mSearchBox = getEditText(view, R.id.main_fragment_search_box);
        mSearchScopeSpinner = getSpinner(view, R.id.main_fragment_search_scope);
        mSearchButton = getImageButton(view, R.id.main_fragment_search_button);
    }

    private ImageButton getImageButton(View view, int id) {
        return (ImageButton) view.findViewById(id);
    }

    private Spinner getSpinner(View view, int id) {
        return (Spinner) view.findViewById(id);
    }

    private EditText getEditText(View view, int id) {
        return (EditText) view.findViewById(id);
    }

    public interface MainFragmentListener {
        void getSearchInfo(String query, String Scope);
    }


    private class UrlFetchTask extends AsyncTask<String, Void, Void> {

        ArrayList<InputStream> inputStreams;

        public UrlFetchTask() {
            inputStreams = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(String... params) {
            int count = params.length;
            for (String param : params) {
                inputStreams.add(mUtil.openUrl(param));
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            for (String name : fileNames) {
                File f = new File(getContext().getFilesDir(), name);
                f.delete();
            }
            mMainFramentComponentsLayout.setVisibility(View.GONE);
            mFetchMessageLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int len = inputStreams.size();
            for (int i = 0; i < len; i++) {
                String name = fileNames[i];
                String content = mUtil.InputStreamToString(inputStreams.get(i));
                mUtil.writeToFile(name, content);
            }
            mFetchMessageLayout.setVisibility(View.GONE);
            mMainFramentComponentsLayout.setVisibility(View.VISIBLE);
        }
    }

}
