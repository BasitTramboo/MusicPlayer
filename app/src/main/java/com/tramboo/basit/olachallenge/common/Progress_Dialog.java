package com.tramboo.basit.olachallenge.common;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by basit on 12/19/17.
 */

public class Progress_Dialog {
    private ProgressDialog progressDialog;
    private Context context;
    private String message;

    public Progress_Dialog(Context context, String message) {
        this.context = context;
        this.message = message;
    }

    public void showProgressDialog(){
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
        }
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void hideProgressDialog(){
        progressDialog.dismiss();
    }
}
