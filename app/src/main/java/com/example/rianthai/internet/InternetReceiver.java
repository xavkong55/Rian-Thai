package com.example.rianthai.internet;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.rianthai.R;

public class InternetReceiver extends BroadcastReceiver {

    Dialog dialog;
    String status;

    @NonNull
    @Override
    public String toString() {
        return status;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        dialog = new Dialog(context);
        status = CheckInternet.getNetworkInfo(context);
        if (status.equals("disconnected")){
            dialog.setContentView(R.layout.dialog_no_connection);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Button btn_proceed = dialog.findViewById(R.id.btn_proceed);
            btn_proceed.setOnClickListener(v-> dialog.dismiss());
            dialog.show();
        }
//        if (status.equals("connected")){}
    }
}
