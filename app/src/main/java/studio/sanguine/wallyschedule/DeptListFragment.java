package studio.sanguine.wallyschedule;


import android.os.Bundle;
import android.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeptListFragment extends DialogFragment {

    public DeptListFragment() {
        // Required empty public constructor
    }


    public static DeptListFragment newInstance() {
        DeptListFragment fragment = new DeptListFragment();


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dept_list, container, false);
        Button clearButton, closeButton;
        final EditText filterText = (EditText) view.findViewById(R.id.deptList_filterEditText);
        clearButton = (Button)view.findViewById(R.id.deptList_clearButton);
        closeButton = (Button)view.findViewById(R.id.deptList_closeButton);

        List<String> tmpStrings = Arrays.asList(getResources().getStringArray(R.array.departments));

        ListView departments = (ListView)view.findViewById(R.id.deptList_deptListView);
        ArrayList<DeptListItem> deptListItems = new ArrayList<DeptListItem>();
        final DeptListAdapter adapter = new DeptListAdapter(getActivity().getBaseContext(), deptListItems);
        departments.setAdapter(adapter);
        for(String s : tmpStrings){
            String id, desc;
            id = s.substring(0, s.indexOf("."));
            desc = s.substring(s.indexOf(".") + 1);
            DeptListItem item = new DeptListItem(id, desc);
            adapter.add(item);
        }
        adapter.notifyDataSetChanged();

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterText.setText("");
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        filterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

}
