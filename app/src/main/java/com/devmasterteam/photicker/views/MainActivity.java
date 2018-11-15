package com.devmasterteam.photicker.views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.devmasterteam.photicker.R;
import com.devmasterteam.photicker.utils.ImageUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final ViewHolder mViewHolder = new ViewHolder();
    private ImageView mImageSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        List<Integer> mListImages = ImageUtil.getImagesList();

        this.mViewHolder.mRelativePhotoContent =  RelativeLayout relativeLayout = (RelativeLayout) this.findViewById(R.id.relative_photo_content_draw);
        final LinearLayout content = (LinearLayout) this.findViewById(R.id.linear_horizontal_scroll_content);

        for (Integer imageId : mListImages) {
            ImageView image = new ImageView(this);

            image.setImageBitmap(ImageUtil.decodeSampledBitmapFromResource(getResources(), imageId, 70, 70));
            image.setPadding(20, 10, 20, 10);

            BitmapFactory.Options dimensions = new BitmapFactory.Options();
            dimensions.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), imageId, dimensions);

            final int width = dimensions.outWidth;
            final int height = dimensions.outHeight;

            image.setOnClickListener(onClickImageOption(this.mViewHolder.mRelativePhotoContent , imageId, width, height));

            content.addView(image);
        }

        this.mViewHolder.mLinearControllPanel = (LinearLayout) this.findViewById(R.id.linear_control_panel);
        this.mViewHolder.mLinearLayoutSharePanel = (LinearLayout) this.findViewById(R.id.linear_share_panel);

        this.mViewHolder.mButtonZoomIn = (ImageView) this.findViewById( R.id.image_zoom_in );
        this.mViewHolder.mButtonZoomOut = (ImageView) this.findViewById( R.id.image_zoom_out );
        this.mViewHolder.mButtonRotateLeft = (ImageView) this.findViewById( R.id.image_rotate_left );
        this.mViewHolder.mButtonRotateRight = (ImageView) this.findViewById( R.id.image_rotate_right );
        this.mViewHolder.mButtonRotateFinish = (ImageView) this.findViewById( R.id.image_finisht );
        this.mViewHolder.mButtonRotateRemove = (ImageView) this.findViewById( R.id.image_remove );

        this.setListener();
    }

    private void setListener()
    {
        this.findViewById(R.id.image_zoom_in).setOnClickListener( this );
        this.findViewById(R.id.image_zoom_out).setOnClickListener( this );
        this.findViewById(R.id.image_rotate_left).setOnClickListener( this );
        this.findViewById(R.id.image_rotate_right).setOnClickListener( this );
        this.findViewById(R.id.image_finisht).setOnClickListener( this );
        this.findViewById(R.id.image_remove).setOnClickListener( this );
    }

    private View.OnClickListener onClickImageOption(final RelativeLayout relativeLayout, final Integer imageId, int width, int height) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final ImageView image = new ImageView(MainActivity.this);
                image.setBackgroundResource(imageId);
                relativeLayout.addView(image);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) image.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);

                toogleControllPanel(true);
            }
        };
    }

    private void toogleControllPanel(boolean showControlls)
    {

        if (showControlls) {
            this.mViewHolder.mLinearControllPanel.setVisibility(View.VISIBLE);
            this.mViewHolder.mLinearLayoutSharePanel.setVisibility(View.GONE);
        }
        else
        {
            this.mViewHolder.mLinearControllPanel.setVisibility(View.GONE);
            this.mViewHolder.mLinearLayoutSharePanel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.image_zoom_in:
                ImageUtil.handleZoomIn(this.mImageSelected);
                break;

            case R.id.image_zoom_out:
                ImageUtil.handleZoomOut(this.mImageSelected);
                break;
            case R.id.image_rotate_left:
                ImageUtil.handlerotate_left(this.mImageSelected);
                break;
            case R.id.image_rotate_right:
                ImageUtil.handlerotate_right(this.mImageSelected);
                break;
            case R.id.image_finisht:
                toogleControllPanel(false);
                break;
            case R.id.image_remove:
                this.mViewHolder.mRelativePhotoContent.removeView( this.mImageSelected );
                break;
        }
    }

    private static class ViewHolder {

        ImageView mButtonZoomIn;
        ImageView mButtonZoomOut;
        ImageView mButtonRotateLeft;
        ImageView mButtonRotateRight;
        ImageView mButtonRotateFinish;
        ImageView mButtonRotateRemove;

        LinearLayout mLinearLayoutSharePanel;
        LinearLayout mLinearControllPanel;
        RelativeLayout mRelativePhotoContent;
    }
}
