package studio.sanguine.wallyschedule;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class OpenScheduleFragment extends DialogFragment {

    private static final String OPENSCHEDULE_ELEMENTS = "";
    private String openScheduleItems;
    private static String _date;
    ArrayList<OpenScheduleItem> items;

    public OpenScheduleFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static OpenScheduleFragment newInstance(String param1, String date) {
        OpenScheduleFragment fragment = new OpenScheduleFragment();
        Bundle args = new Bundle();
        args.putString(OPENSCHEDULE_ELEMENTS, param1);
        fragment.setArguments(args);
        _date = date;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            openScheduleItems = getArguments().getString(OPENSCHEDULE_ELEMENTS);

        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_openshift, container, false);

        TextView lastUpdatedText = (TextView)view.findViewById(R.id.openShiftLastUpdatedText);
        ListView listView = (ListView) view.findViewById(R.id.openShiftListView);
        items = new ArrayList<OpenScheduleItem>();
        final OpenScheduleItemAdapter adapter = new OpenScheduleItemAdapter(getActivity().getBaseContext(), items);
        listView.setAdapter(adapter);
        lastUpdatedText.setText("Last updated: " + _date);

        Document document = Jsoup.parse(openScheduleItems);
        Elements elements = document.select("div.unfilledScroller").select("td.unfilledDetails");
        for(Element element : elements) {
            String openShiftTime = element.toString();
            String openShiftPosition;
            openShiftPosition = openShiftTime.substring(openShiftTime.indexOf("</span>") + 8, openShiftTime.indexOf("</div>")).trim();
            openShiftTime = openShiftTime.substring(openShiftTime.indexOf("Time") + 6, openShiftTime.indexOf("</span>"));
            adapter.add(new OpenScheduleItem(openShiftPosition, openShiftTime));
        }

        Button closeButton = (Button)view.findViewById(R.id.openShiftCloseButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

}
