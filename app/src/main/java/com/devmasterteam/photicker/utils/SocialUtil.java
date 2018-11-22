package com.devmasterteam.photicker.utils;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.devmasterteam.photicker.R;
import com.devmasterteam.photicker.views.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SocialUtil
{
    private static final String HASHTAG = "photickerApp";

    public static void shareImageOnInsta( MainActivity mainActivity, RelativeLayout mRelativePhotoContent, View v )
    {
        PackageManager pkManager = mainActivity.getPackageManager();

        try
        {
          pkManager.getPackageInfo( "com.instagram.android", 0 );

          try
          {
              Bitmap image = ImageUtil.drawBitmap( mRelativePhotoContent );
              ByteArrayOutputStream bytes = new ByteArrayOutputStream();

              image.compress( Bitmap.CompressFormat.JPEG, 100, bytes);


              File file = new File( Environment.getExternalStorageDirectory() + File.separator + "temp_file.jpg");

              try
              {
                  File.createNewFile();
                  FileOutputStream fo = new FileOutputStream( file );
                  fo.write( bytes.toByteArray() );

                  Intent sendIntent = new Intent();
                  sendIntent.setAction( Intent.ACTION_SEND );
                  sendIntent.putExtra( Intent.EXTRA_STREAM, Uri.parse( "file:///sdcard/temp_file.jpg"));
              }

              catch ( FileNotFoundException e )
              {
                  Toast.makeText( mainActivity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
                  e.printStackTrace();
              }
          }

        catch ( IOException e )
        {
            Toast.makeText( mainActivity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

        catch (PackageManager.NameNotFoundException e)
    {
        Toast.makeText( mainActivity, R.string.instagram_not_installed, Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }
    }

    public static void shareImageOnFace( MainActivity mainActivity, RelativeLayout mRelativePhotoContent, View v )
    {
    }

    public static void shareImageOnWhats( MainActivity mainActivity, RelativeLayout mRelativePhotoContent, View v )
    {
        PackageManager pkManager = mainActivity.getPackageManager();

        try
        {
            pkManager.getPackageInfo("com.whatsapp", 0);

            String fileName = "temp_file" + System.currentTimeMillis() + ".jpg";

            try
            {
                mRelativePhotoContent.setDrawingCacheEnabled( true );
                mRelativePhotoContent.buildDrawingCache( true );

                File imageFile = new File( Environment.getExternalStorageDirectory(), fileName );
                FileOutputStream fileOutputStream = new FileOutputStream( imageFile );

                mRelativePhotoContent.getDrawingCache( true )
                        .compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream );

                fileOutputStream.close();

                mRelativePhotoContent.setDrawingCacheEnabled( false );
                mRelativePhotoContent.destroyDrawingCache();

                try
                {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction( Intent.ACTION_SEND );
                    sendIntent.putExtra( Intent.EXTRA_TEXT, HASHTAG);
                    sendIntent.putExtra( Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/" + fileName ));

                    sendIntent.setType("image/jpeg");
                    sendIntent.setPackage("com.whatsapp");

                    // Chamada da Activity para compartilhamento no whatsapp
                    v.getContext().startActivity( Intent.createChooser( sendIntent, mainActivity.getString( R.string.share_image )));
                }
                catch ( Exception e )
                {
                    Toast.makeText( mainActivity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            catch ( FileNotFoundException e )
            {
                Toast.makeText( mainActivity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            catch (IOException e)
            {
                Toast.makeText( mainActivity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }

        catch (PackageManager.NameNotFoundException e)
        {
            Toast.makeText( mainActivity, R.string.whatsapp_not_installed, Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
        }
    }

    public static void shareImageOnTT( MainActivity mainActivity, RelativeLayout mRelativePhotoContent, View v )
    {
        PackageManager pkManager = mainActivity.getPackageManager();

        try
        {
            pkManager.getPackageInfo("com.twitter.android", 0);

            try
            {
                Intent tweetIntent = new Intent( Intent.ACTION_SEND );

                Bitmap image = ImageUtil.drawBitmap( mRelativePhotoContent );

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                image.compress( Bitmap.CompressFormat.JPEG, 100, bytes );

                File file = new File( Environment.getExternalStorageDirectory() + File.separator + "temp_file.jpg" );
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream( file );
                fo.write( bytes.toByteArray() );

                tweetIntent.putExtra( Intent.EXTRA_TEXT, HASHTAG );
                tweetIntent.putExtra( Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temp_file.jpg" ));

                PackageManager pm = mainActivity.getPackageManager();
                List<ResolveInfo> resolve = pm.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY );

                boolean resolved = false;

                for ( ResolveInfo ri : resolve )
                {
                    if ( ri.activityInfo.name.contains("twitter"))
                    {
                        tweetIntent.setClassName( ri.activityInfo.packageName, ri.activityInfo.name );
                        resolved = true;
                        break;
                    }
                }

                //inicialização do twitter
                v.getContext().startActivity(
                        resolved ? tweetIntent : Intent.createChooser( tweetIntent, mainActivity.getString( R.string.share_image )));
            }

            catch ( FileNotFoundException e )
            {
                Toast.makeText( mainActivity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            catch ( IOException e )
            {
                Toast.makeText( mainActivity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }

        catch (PackageManager.NameNotFoundException e)
        {
            Toast.makeText( mainActivity, R.string.twitter_not_installed, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
