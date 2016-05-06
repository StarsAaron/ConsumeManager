package com.rujian.consumemanager.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rujian.consumemanager.R;


public class MyDialog {
    private AfterClickBtn afterClickBtn = null;
    private Context context = null;
    private AlertDialog dialog = null;
    private RelativeLayout view = null;

    public  MyDialog(Context context,String title,String message,boolean showBtnBar,AfterClickBtn click){
        this.context = context;
        view = (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.dialog_style,null);
        dialog =new AlertDialog.Builder(context).create();
        if(showBtnBar){
            afterClickBtn = click;
            LinearLayout ll_btn_bar = (LinearLayout)view.findViewById(R.id.ll_btn_bar);
            ll_btn_bar.setVisibility(View.VISIBLE);
            Button btn_dialog_OK = (Button)view.findViewById(R.id.btn_dialog_OK);
            Button btn_dialog_concel = (Button)view.findViewById(R.id.btn_dialog_concel);
            btn_dialog_OK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    afterClickBtn.clickOk();
                }
            });
            btn_dialog_concel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    afterClickBtn.clickConcel();
                }
            });
        }else{
            ImageView iv_dialog_concel = (ImageView)view.findViewById(R.id.iv_dialog_concel);
            iv_dialog_concel.setVisibility(View.VISIBLE);
            iv_dialog_concel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        TextView tv_dialog_title = (TextView)view.findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText(title);
        TextView tv_dialog_message = (TextView)view.findViewById(R.id.tv_dialog_message);
        tv_dialog_message.setText(message);
    }

    public void showDialog(){
        if(dialog != null){
            dialog.show();
            dialog.setContentView(view);
        }
    }

    public interface AfterClickBtn{
        void clickOk();
        void clickConcel();
    }
}
