package com.devmasterteam.photicker.utils;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import com.devmasterteam.photicker.R;
import com.devmasterteam.photicker.views.MainActivity;

import java.lang.reflect.Method;

public class PermissionUtil
{
    public static final int CAMERA_PERMISSION = 0 ;

    public static boolean hasCameraPersmission( Context context )
    {
        if ( needToAskPermission() )
        {
            return ActivityCompat.checkSelfPermission( context, Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    private static boolean needToAskPermission()
    {

        // **** INÍCIO trecho de código adicionado para solucionar CRASH DA CAMERA ****

        //link : https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed

        if ( Build.VERSION.SDK_INT >= 24 )
        {
            try
            {
                Method m = StrictMode.class.getMethod( "disableDeathOnFileUriExposure" );
                m.invoke( null );
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }

            return true;
        }

        else
        {
            return false;
        }

        // **** FIM trecho de código adicionado para solucionar CRASH DA CAMERA ****

        // codigo usando anteriormente acima -> return ( Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 );
    }

    public static void asksCameraPermission(final MainActivity context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(context)
                    .setMessage(context.getString(R.string.permission_camera_explanation))
                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(context, new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtil.CAMERA_PERMISSION);
                        }
                    }).show();
        }
        else
        {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtil.CAMERA_PERMISSION);
        }
    }
}
