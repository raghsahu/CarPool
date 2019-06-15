package com.fortin.carpool.helper;

import android.content.Context;

import com.fortin.carpool.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by reeva on 4/2/16.
 */
public class NoInternetDialogue {

    public static ConnectionDetector con;

    public static void nointernet(Context c)
    {
        new SweetAlertDialog(c, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(c.getString(R.string.noint))
                .setContentText(c.getString(R.string.nointdesc))
                .setConfirmText(c.getString(R.string.alright))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    public static boolean isconnection(Context c)
    {
        con = new ConnectionDetector(c);

        if(con.isConnectingToInternet())
        {
            return true;
        }
        else {
            new SweetAlertDialog(c, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(c.getString(R.string.noint))
                    .setContentText(c.getString(R.string.nointdesc))
                    .setConfirmText(c.getString(R.string.alright))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
            return false;
        }
    }
}
