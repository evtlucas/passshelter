package br.unisinos.evertonlucas.cryptocontact.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import br.unisinos.evertonlucas.cryptocontact.R;

/**
 * Class responsible for show a confirmation dialog
 * Created by everton on 08/08/15.
 */
public class ConfirmationDialog extends DialogFragment {

    private ConfirmationDialogListener listener;
    private int title;
    private int message;

    public static ConfirmationDialog newInstance(ConfirmationDialogListener  listener) {
        ConfirmationDialog d = new ConfirmationDialog();
        d.listener = listener;
        return d;
    }

    public ConfirmationDialog setTitle(int title) {
        this.title = title;
        return this;
    }

    public ConfirmationDialog setMessage(int message) {
        this.message = message;
        return this;
    }

    public interface ConfirmationDialogListener {
        public void onConfirmationPositive();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setTitle(this.title)
                .setMessage(this.message)
                .setCancelable(true)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onConfirmationPositive();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return alertBuilder.create();
    }
}
