package com.example.catalogos.services.pdfviewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.catalogos.R;

import java.io.File;

public class SelectViewerForPDFDialog extends DialogFragment {

    private String filePath;
    private File pdfFile;
    private OnSelectViewerForPDFDialogListener onSelectViewerForPDFDialogListener;

    public SelectViewerForPDFDialog(String filePath, File pdfFile){
        super();
        this.filePath = filePath;
        this.pdfFile = pdfFile;
    }

    public interface OnSelectViewerForPDFDialogListener {
        void onLocal(String filePath, File pdfFile);
        void onExternal(String filePath, File pdfFile);
    }

    @Override
    public void onAttach(@NonNull Activity activity){
        super.onAttach(activity);
        try {
            onSelectViewerForPDFDialogListener = (OnSelectViewerForPDFDialogListener) activity;
        } catch (Exception e) {
            throw new ClassCastException(
                activity.toString() + " no implementó onSelectViewerForPDFDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        return createSelectViewerForPDFDialog ();
    }


    public AlertDialog createSelectViewerForPDFDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setTitle(R.string.select_pdf_viewer)
                .setMessage(R.string.pdf_viewer_question)
                .setPositiveButton(R.string.local,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                onSelectViewerForPDFDialogListener.onLocal (filePath, pdfFile);
                                dismiss();
                            }
                        })
                .setNegativeButton(R.string.external,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                onSelectViewerForPDFDialogListener.onExternal (filePath, pdfFile);
                                dismiss();
                            }
                        })
                .setNeutralButton (R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                dismiss();
                            }
                        })
                .setCancelable(true);

        return builder.create();
    }

}
