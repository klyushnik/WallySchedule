package studio.sanguine.wallyschedule;

import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class FirstLaunchErrorFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private static int _errorCode;


    public FirstLaunchErrorFragment() {
        // Required empty public constructor
    }

    public static FirstLaunchErrorFragment newInstance(int errorCode) {
        FirstLaunchErrorFragment fragment = new FirstLaunchErrorFragment();
        Bundle args = new Bundle();
        _errorCode = errorCode;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.AppTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.welcome_error_fragment, container, false);
        TextView errorTextView = (TextView)view.findViewById(R.id.welcomeErrorDescriptionTextView);
        TextView backButtonTextView = (TextView)view.findViewById(R.id.welcomeErrorBack);
        switch (_errorCode){

            case 0:
                errorTextView.setText(R.string.welcome_error_connection);
                break;
            case 1:
                errorTextView.setText(R.string.welcome_error_credentials);
                break;
            case 2:
                errorTextView.setText(R.string.welcome_error_timeout);
                break;
            default:
                errorTextView.setText("Unknown error. If you are seeing this, contact the developer.");
                break;
        }

        backButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

}
