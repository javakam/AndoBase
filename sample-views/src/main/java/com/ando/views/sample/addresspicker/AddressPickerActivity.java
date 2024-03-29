package com.ando.views.sample.addresspicker;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ando.views.sample.R;
import com.ando.views.sample.activity.BaseActivity;
import com.ando.views.sample.addresspicker.bean.AddressBean;
import com.ando.views.sample.addresspicker.bean.ServerEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: AddressPickerActivity
 * <p>
 * Description:
 * </p>
 * Author Changbao
 * Date 2018/11/7  11:02
 */
public class AddressPickerActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
    }

    public void lightPhone(View v) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //第一步，从后台获取24省
        List<ServerEntity> provinces = new ArrayList<>();


        AddressPickerDialog dialog = new AddressPickerDialog.Builder(this)
                .setConfig(AddressPickerDialog.getConfigOfPhoneLight())
                .setThemeResId(R.style.AddressPickerDialogPhoneStyle)
                .setOnConfirmClickListener(new AddressPickerDialog.OnConfirmClickListener() {
                    @Override
                    public void confirm(List<AddressBean> pickedAddresses) {
                        Toast.makeText(AddressPickerActivity.this, pickedAddresses.size() + " ", Toast.LENGTH_LONG).show();
                    }
                }).build();
        dialog.show();
    }

    public void lightPad(View v) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        AddressPickerDialog dialog = new AddressPickerDialog.Builder(this)
                .setConfig(AddressPickerDialog.getConfigOfPadLight())
                .setThemeResId(R.style.AddressPickerDialogPadLightStyle)
                .setOnConfirmClickListener(new AddressPickerDialog.OnConfirmClickListener() {
                    @Override
                    public void confirm(List<AddressBean> pickedAddresses) {
                        Toast.makeText(AddressPickerActivity.this, pickedAddresses.size() + " ", Toast.LENGTH_LONG).show();
                    }
                }).build();
        dialog.show();
    }

    public void darkPad(View v) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        new AddressPickerDialog.Builder(this)
                .setConfig(AddressPickerDialog.getConfigOfPadDark())
                .setThemeResId(R.style.AddressPickerDialogPadDarkStyle)
                .setOnConfirmClickListener(new AddressPickerDialog.OnConfirmClickListener() {
                    @Override
                    public void confirm(List<AddressBean> pickedAddresses) {
                        Toast.makeText(AddressPickerActivity.this, pickedAddresses.size() + " ", Toast.LENGTH_LONG).show();
                    }
                }).build().show();
    }
}
