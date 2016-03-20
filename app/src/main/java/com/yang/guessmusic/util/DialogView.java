package com.yang.guessmusic.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yang.guessmusic.R;
import com.yang.guessmusic.observe.MyAlertDialogClickListener;

public class DialogView {
    private static AlertDialog mAlertDialog;

    @SuppressLint("NewApi")
    public static void showDialog(final Context context, String title, String message, final MyAlertDialogClickListener listener) {
        View dialogView;
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_Transparent);
        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_view, null);
        ImageButton certainButton = (ImageButton) dialogView.findViewById(R.id.btn_dialog_certain);
        ImageButton cancelButton = (ImageButton) dialogView.findViewById(R.id.btn_dialog_cancel);
        TextView tipsTv = (TextView) dialogView.findViewById(R.id.tv_dialog_tips);
        TextView messageTv = (TextView) dialogView.findViewById(R.id.tv_dialog_message);
        tipsTv.setText(title);
        messageTv.setText(message);
        certainButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 令AlertDialog消失
                if (mAlertDialog != null) {
                    mAlertDialog.cancel();
                }
                if (listener != null) {
                    //回调相应事件
                    listener.onClick();
                }
                MyPlayer.playSound(context, MyPlayer.SOUND_ENTER);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mAlertDialog != null) {
                    mAlertDialog.cancel();
                }
                MyPlayer.playSound(context, MyPlayer.SOUND_CANCEL);
            }

        });
        //设置AlertDialog显示自定义View
        builder.setView(dialogView);
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }
}
