package com.beabow.activity;

import com.beabow.AppContext;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.security.acl.Permission;


/**
 * 启动页
 *
 * @author tony
 */
public class StartupAvtivity extends Activity {

    final int MY_Permission = 66;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
            //读取用户ID
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                new AlertDialog.Builder(this)
                        .setMessage("We neet these two Permission to contionue")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(StartupAvtivity.this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION}, MY_Permission);

                            }
                        })
                        .create()
                        .show();
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION}, MY_Permission);

            }
        }
        else{

            Intent starterIntent = new Intent(this, SetWifiActivity.class);
            startActivity(starterIntent);
            finish();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_Permission:
                Log.v("hello",grantResults.length+":"+grantResults[0]+":"+grantResults[1]);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Intent starterIntent = new Intent(this, SetWifiActivity.class);
                    startActivity(starterIntent);
                    finish();
                }
                else{
                    new AlertDialog.Builder(StartupAvtivity.this)
                            .setMessage("We can't get the necessary permission")
                            .setPositiveButton("OK",null)
                            .create()
                            .show();
                    finish();
                    startActivity(getIntent());
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppContext.activityList.add(this);
    }
}
