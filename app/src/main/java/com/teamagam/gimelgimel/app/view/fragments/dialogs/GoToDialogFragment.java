package com.teamagam.gimelgimel.app.view.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.teamagam.gimelgimel.R;

/**
 * Created by Yoni on 3/9/2016.
 */
public class GoToDialogFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    EditText mLongitudeEditText;
    EditText mLatitudeEditText;
    EditText mAltitudeEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        View dialogView = inflater.inflate(R.layout.dialog_input_position, null);
        mLongitudeEditText = (EditText) dialogView.findViewById(R.id.dialog_longitude);
        mLatitudeEditText = (EditText) dialogView.findViewById(R.id.dialog_latitude);
        mAltitudeEditText = (EditText) dialogView.findViewById(R.id.dialog_altitude);

        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.dialog_position_massage)
                .setTitle(R.string.dialog_position_title)
                .setPositiveButton(R.string.dialog_position_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Send the positive button event back to the host activity
                                float longitude = Float.parseFloat(
                                        mLongitudeEditText.getText().toString());
                                float latitude = Float.parseFloat(
                                        mLatitudeEditText.getText().toString());
                                float altitude;
                                if (mAltitudeEditText.getText().toString().isEmpty()) {
                                    altitude = -1;
                                } else {
                                    altitude = Float.parseFloat(
                                            mAltitudeEditText.getText().toString());
                                }
                                mListener.onPositionDialogPositiveClick(GoToDialogFragment.this,
                                        longitude, latitude, altitude);
                            }
                        })
                .setNegativeButton(R.string.dialog_position_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Send the negative button event back to the host activity
                                mListener.onPositionDialogNegativeClick(GoToDialogFragment.this);
                            }
                        });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    //TODO: consider injecting map-goto capability instead of using event listener
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        void onPositionDialogPositiveClick(DialogFragment dialog, float longitude, float latitude,
                                           float altitude);

        void onPositionDialogNegativeClick(DialogFragment dialog);
    }


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        // Verify that the host **activity** implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, who knows? maybe the fragment will.
            mListener = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mListener != null) {
            return;
        }
        // if the activity doesn't implement callback then the target fragment should.
//        // Verify that the host **fragment** implements the callback interface
        try {
            mListener = (NoticeDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}