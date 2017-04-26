package camelcase.searchemall;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

public class MainFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    MainFragmentListener mainFragmentListener;
    private EditText mSearchBox;
    private Spinner mSearchScope;
    private ImageButton mSearchButton;
    private String mStringSearchScope;

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
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        initViews(view);
        initSpinner();
        listenButtonEvent();
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent,
                               View view,
                               int position,
                               long id) {
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
        mSearchScope.setAdapter(mSpinnerAdapter);
        mSearchScope.setOnItemSelectedListener(this);
        return true;
    }

    private boolean listenButtonEvent() {
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = mSearchBox.getText().toString();
                String searchScope = mStringSearchScope;
                mainFragmentListener.getSearchInfo(searchQuery, searchScope);
            }
        });
        return true;
    }

    private void initViews(View view) {
        mSearchBox = getEditText(view, R.id.main_fragment_search_box);
        mSearchScope = getSpinner(view, R.id.main_fragment_search_scope);
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

}
