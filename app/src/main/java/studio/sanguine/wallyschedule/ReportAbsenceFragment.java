package studio.sanguine.wallyschedule;


import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ReportAbsenceFragment extends DialogFragment {

    public ReportAbsenceFragment() {
        // Required empty public constructor
    }

    public static ReportAbsenceFragment newInstance() {
        ReportAbsenceFragment fragment = new ReportAbsenceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_absence, container, false);
        Button phoneButton = (Button)view.findViewById(R.id.reportabsence_phoneButton);
        Button internetButton = (Button)view.findViewById(R.id.reportabsence_internetButton);
        Button cancelButton = (Button)view.findViewById(R.id.reportabsence_calcelButton);

        phoneButton.setTypeface(FontManager.getTypeface(view.getContext(), FontManager.FONTAWESOME));
        internetButton.setTypeface(FontManager.getTypeface(view.getContext(), FontManager.FONTAWESOME));

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:1-800-775-5944"));
                startActivity(intent);
                dismiss();
            }
        });

        internetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://gta.walmartone.com/AssociateInformationLineWeb";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return  view;
    }

}
