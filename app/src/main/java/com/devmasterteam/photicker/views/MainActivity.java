package com.devmasterteam.photicker.views;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.devmasterteam.photicker.R;
import com.devmasterteam.photicker.utils.ImageUtil;
import com.devmasterteam.photicker.utils.LongEventType;
import com.devmasterteam.photicker.utils.PermissionUtil;
import com.devmasterteam.photicker.utils.SocialUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, View.OnLongClickListener{

    private static final int REQUEST_TAKE_PHOTO = 2;
    private final ViewHolder mViewHolder = new ViewHolder();
    private ImageView mImageSelected;
    private boolean mAutoIncrement;
    private LongEventType mLongType;
    private Handler mRepeatUpdateHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      // teste para resolve rproblema da camera *****

      //  StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        //StrictMode.setVmPolicy(builder.build());

        // teste para resolve rproblema da camera *****

        getSupportActionBar().setDisplayShowTitleEnabled( false );
        getSupportActionBar().setDisplayShowHomeEnabled( true );
        getSupportActionBar().setIcon( R.mipmap.ic_launcher );

        List<Integer> mListImages = ImageUtil.getImagesList();

        this.mViewHolder.mRelativePhotoContent =  ( RelativeLayout ) this.findViewById(R.id.relative_photo_content_draw);
        final LinearLayout content = ( LinearLayout ) this.findViewById(R.id.linear_horizontal_scroll_content);

        for ( Integer imageId : mListImages ) {
            ImageView image = new ImageView(this );

            image.setImageBitmap( ImageUtil.decodeSampledBitmapFromResource(getResources(), imageId, 70, 70) );
            image.setPadding( 20, 10, 20, 10 );

            BitmapFactory.Options dimensions = new BitmapFactory.Options();
            dimensions.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), imageId, dimensions );

            final int width = dimensions.outWidth;
            final int height = dimensions.outHeight;

            image.setOnClickListener(onClickImageOption( this.mViewHolder.mRelativePhotoContent , imageId, width, height) );

            content.addView( image );
        }

        this.mViewHolder.mLinearControllPanel = ( LinearLayout) this.findViewById(R.id.linear_control_panel );
        this.mViewHolder.mLinearLayoutSharePanel = ( LinearLayout) this.findViewById(R.id.linear_share_panel );

        this.mViewHolder.mImageInstagram = (ImageView) this.findViewById( R.id.image_instagram );
        this.mViewHolder.mImageFacebook = (ImageView) this.findViewById( R.id.image_facebook );
        this.mViewHolder.mImageTwitter = (ImageView) this.findViewById( R.id.image_twitter );
        this.mViewHolder.mImageWhatsapp = (ImageView) this.findViewById( R.id.image_whats );

        this.mViewHolder.mImagePhoto = (ImageView) this.findViewById( R .id.image_photo );
        this.mViewHolder.mButtonZoomIn = ( ImageView) this.findViewById( R.id.image_zoom_in );
        this.mViewHolder.mButtonZoomOut = ( ImageView) this.findViewById( R.id.image_zoom_out );
        this.mViewHolder.mButtonRotateLeft = ( ImageView) this.findViewById( R.id.image_rotate_left );
        this.mViewHolder.mButtonRotateRight = ( ImageView) this.findViewById( R.id.image_rotate_right );
        this.mViewHolder.mButtonRotateFinish = ( ImageView) this.findViewById( R.id.image_finisht );
        this.mViewHolder.mButtonRotateRemove = ( ImageView) this.findViewById( R.id.image_remove );

        this.setListener();
    }

    private void setListener()
    {
        this.findViewById( R.id.image_take_photo).setOnClickListener( this );
        this.findViewById(R.id.image_zoom_in).setOnClickListener( this );
        this.findViewById(R.id.image_zoom_out).setOnClickListener( this );
        this.findViewById(R.id.image_rotate_left).setOnClickListener( this );
        this.findViewById(R.id.image_rotate_right).setOnClickListener( this );
        this.findViewById(R.id.image_finisht).setOnClickListener( this );
        this.findViewById(R.id.image_remove).setOnClickListener( this );
        this.findViewById(R.id.image_take_photo).setOnClickListener( this );

        this.findViewById( R.id.image_whats).setOnClickListener( this );
        this.findViewById( R.id.image_instagram).setOnClickListener( this );
        this.findViewById( R.id.image_twitter).setOnClickListener( this );
        this.findViewById( R.id.image_facebook).setOnClickListener( this );

        this.findViewById(R.id.image_zoom_in).setOnLongClickListener( this );
        this.findViewById(R.id.image_zoom_out).setOnLongClickListener( this );
        this.findViewById(R.id.image_rotate_left).setOnLongClickListener( this );
        this.findViewById(R.id.image_rotate_right).setOnLongClickListener( this );
        this.findViewById(R.id.image_finisht).setOnLongClickListener( this );
        this.findViewById(R.id.image_remove).setOnLongClickListener( this );

        this.findViewById(R.id.image_zoom_in).setOnTouchListener( this );
        this.findViewById(R.id.image_zoom_out).setOnTouchListener( this );
        this.findViewById(R.id.image_rotate_left).setOnTouchListener( this );
        this.findViewById(R.id.image_rotate_right).setOnTouchListener( this );
        this.findViewById(R.id.image_finisht).setOnTouchListener( this );
        this.findViewById(R.id.image_remove).setOnTouchListener( this );
    }

    private View.OnClickListener onClickImageOption( final RelativeLayout relativeLayout,
                                                     final Integer imageId, int width, int height )
    {
        return new View.OnClickListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick( View v ) {
                final ImageView image = new ImageView(MainActivity.this );
                image.setBackgroundResource( imageId );
                relativeLayout.addView( image );

                RelativeLayout.LayoutParams layoutParams = ( RelativeLayout.LayoutParams ) image.getLayoutParams();
                layoutParams.addRule( RelativeLayout.CENTER_HORIZONTAL );
                layoutParams.addRule( RelativeLayout.CENTER_VERTICAL );

                mImageSelected = image;

                toogleControllPanel(true );

                image.setOnTouchListener(new View.OnTouchListener(){

                    @Override
                    public boolean onTouch( View v, MotionEvent motionEvent )
                    {

                        float x, y;

                        switch ( motionEvent.getAction() )
                        {
                            case MotionEvent.ACTION_DOWN:
                                mImageSelected = image;
                                toogleControllPanel( true );
                                break;
                            case MotionEvent.ACTION_MOVE:
                                int coords[] = { 0,0 };
                                relativeLayout.getLocationOnScreen( coords );

                                x = ( motionEvent.getRawX() - (image.getWidth() / 2) );
                                y = motionEvent.getRawY() - ((coords[1] + 100 ) + (image.getHeight() / 2) );
                                image.setX( x );
                                image.setY( y );
                                break;
                            case MotionEvent.ACTION_UP:
                                break;
                        }
                        return true;
                    }
                });
            }
        };
    }

    private void toogleControllPanel( boolean showControlls )
    {
        if ( showControlls ) {
            this.mViewHolder.mLinearControllPanel.setVisibility( View.VISIBLE );
            this.mViewHolder.mLinearLayoutSharePanel.setVisibility(View.GONE );
        }
        else
        {
            this.mViewHolder.mLinearControllPanel.setVisibility( View.GONE );
            this.mViewHolder.mLinearLayoutSharePanel.setVisibility( View.VISIBLE );
        }
    }

    @Override
    public void onClick( View v )
    {
        switch ( v.getId() )
        {
            case R.id.image_take_photo:

                if ( ! PermissionUtil.hasCameraPersmission ( this ))
                {
                    PermissionUtil.asksCameraPermission ( this );
                }

                dispatchTakePictureIntent();
                break;

            case R.id.image_zoom_in:
                ImageUtil.handleZoomIn( this.mImageSelected);
                break;

            case R.id.image_zoom_out:
                ImageUtil.handleZoomOut( this.mImageSelected );
                break;
            case R.id.image_rotate_left:
                ImageUtil.handlerotate_left(this.mImageSelected );
                break;
            case R.id.image_rotate_right:
                ImageUtil.handlerotate_right(this.mImageSelected );
                break;
            case R.id.image_finisht:
                toogleControllPanel( false );
                break;
            case R.id.image_remove:
                this.mViewHolder.mRelativePhotoContent.removeView( this.mImageSelected );
                break;

            case R.id.image_instagram:
                SocialUtil.shareImageOnInsta( this, mViewHolder.mRelativePhotoContent, v);
                break;
            case R.id.image_facebook:
                SocialUtil.shareImageOnFace( this, mViewHolder.mRelativePhotoContent, v);
                break;
            case R.id.image_twitter:
                SocialUtil.shareImageOnTT( this, mViewHolder.mRelativePhotoContent, v);
                break;
            case R.id.image_whats:
                SocialUtil.shareImageOnWhats( this, mViewHolder.mRelativePhotoContent, v);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
       if ( requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK )
       {
           this.setPhotoAsBackground();
       }
    }

    private void setPhotoAsBackground()
    {
        int targetW = this.mViewHolder.mImagePhoto.getWidth();
        int targetH = this.mViewHolder.mImagePhoto.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile( this.mViewHolder.mUriPhotoPath.getPath(), bmOptions) ;
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactory = Math.min( photoW / targetW, photoH / targetH );

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactory;

        Bitmap bitmap = BitmapFactory.decodeFile( this.mViewHolder.mUriPhotoPath.getPath(), bmOptions );

        Bitmap bitmapRotated = ImageUtil.rotateImageIfRequerid( bitmap, this.mViewHolder.mUriPhotoPath );

        this.mViewHolder.mImagePhoto.setImageBitmap( bitmapRotated );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if ( requestCode == PermissionUtil.CAMERA_PERMISSION )
        {
            if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
            {
                dispatchTakePictureIntent();
            }
            else
            {
                new AlertDialog.Builder( this)
                        .setMessage( getString( R.string.without_permission_camera_explanation))
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }
    }

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );

        if ( takePictureIntent.resolveActivity(getPackageManager() ) != null )
        {
            File photoFile = null;

            try
            {
                photoFile = ImageUtil.createImageFile( this );
                this.mViewHolder.mUriPhotoPath = Uri.fromFile( photoFile );
            }
            catch ( IOException ex )
            {
                Toast.makeText( this, "Não foi possível iniciar a câmera", Toast.LENGTH_LONG ).show();
            }

            if ( photoFile != null )
            {
                takePictureIntent.putExtra( MediaStore.EXTRA_OUTPUT, Uri.fromFile( photoFile));
                startActivityForResult( takePictureIntent, REQUEST_TAKE_PHOTO );
            }
        }
    }

    @Override
    public boolean onLongClick( View view )
    {
        if( view.getId() == R.id.image_zoom_in) this.mLongType = LongEventType.ZoomIn;
        if( view.getId() == R.id.image_zoom_out) this.mLongType  = LongEventType.ZoomOut;
        if( view.getId() == R.id.image_rotate_left) this.mLongType = LongEventType.RotateLeft;
        if( view.getId() == R.id.image_rotate_right) this.mLongType  = LongEventType.RotateRight;

        mAutoIncrement = true;
        new RptUpdater().run();
        return false;
    }

    @Override
    public boolean onTouch( View view, MotionEvent motionEvent )
    {
        int id = view.getId();
        if (id == R.id.image_zoom_in || id == R.id.image_zoom_out ||
            id == R.id.image_rotate_left  || id == R.id.image_rotate_right )
        {
            if( motionEvent.getAction() == MotionEvent.ACTION_UP && mAutoIncrement )
            {
                mAutoIncrement = false;
                this.mLongType = null;
            }
        }
        return false;
    }

    private static class ViewHolder {

        ImageView mImageInstagram;
        ImageView mImageFacebook;
        ImageView mImageTwitter;
        ImageView mImageWhatsapp;

        Uri mUriPhotoPath;
        ImageView mButtonZoomIn;
        ImageView mButtonZoomOut;
        ImageView mButtonRotateLeft;
        ImageView mButtonRotateRight;
        ImageView mButtonRotateFinish;
        ImageView mButtonRotateRemove;
        ImageView mImagePhoto;
        LinearLayout mLinearLayoutSharePanel;
        LinearLayout mLinearControllPanel;
        RelativeLayout mRelativePhotoContent;
    }

    private class RptUpdater implements Runnable{

        @Override
        public void run() {
            if ( mAutoIncrement )
            {
                mRepeatUpdateHandler.postDelayed( new RptUpdater(), 50 );
            }

            if( mLongType != null )
            {
                switch ( mLongType )
                {
                    case ZoomIn:
                        ImageUtil.handleZoomIn( mImageSelected);
                        break;
                    case ZoomOut:
                        ImageUtil.handleZoomOut( mImageSelected );
                        break;
                    case RotateLeft:
                        ImageUtil.handlerotate_left( mImageSelected );
                        break;
                    case RotateRight:
                        ImageUtil.handlerotate_right( mImageSelected );
                        break;
                }
            }
        }
    }
}
