package com.example.rm49865.recordsfood.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.rm49865.recordsfood.R;

import java.io.Serializable;

/**
 * Created by Ricardo on 13/02/2016.
 */
public class ConfirmDialog extends DialogFragment{

    public static final String PARAM_MESSAGE = "message";
    public static final String PARAM_LISTENER = "listener";
    private String message;
    private OnConfirmDialogInteractionListener mListener;

    public ConfirmDialog(){}

    public static ConfirmDialog newInstance(String message, OnConfirmDialogInteractionListener mListener){
        ConfirmDialog confirmDialog = new ConfirmDialog();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_MESSAGE, message);
        bundle.putSerializable(PARAM_LISTENER, mListener);
        confirmDialog.setArguments(bundle);
        return confirmDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            message = getArguments().getString(PARAM_MESSAGE);
            mListener = (OnConfirmDialogInteractionListener) getArguments().getSerializable(PARAM_LISTENER);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.label_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.confirm();
                    }
                })
                .setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.cancel();
                    }
                });
        return builder.create();
    }

    public interface OnConfirmDialogInteractionListener extends Serializable{
        void confirm();
        void cancel();
    }
}
