package com.alfd.app.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.alfd.app.R;


public final class OverlayView extends View {

    private final Paint paint;
    private final int maskColor;

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.overlay_mask);
    }



    @Override
    public void onDraw(Canvas canvas) {
        int size;

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Rect frame;
        int padding = 20;
        if (width > height) {
            size =  height;
        }
        else {
            size = width;
        }
        frame = new Rect( padding, padding, size-padding, size-padding);
        Rect previewFrame = new Rect(0, 0, width, height);
        if (frame == null || previewFrame == null) {
            return;
        }
        paint.setColor(maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);
   }

}