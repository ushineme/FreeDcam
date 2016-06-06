package com.freedcam.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.freedcam.utils.Logger;

public class MyHistogram extends View {

    public MyHistogram(Context context) {
        super(context);
    }

    public MyHistogram(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHistogram(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Paint mPaint = new Paint ();
    private int [] redHistogram = new int [ 256 ];
    private int [] greenHistogram = new int [ 256 ];
    private int [] blueHistogram = new int [ 256 ];
    private Path mHistoPath = new Path ();

    private void drawHistogram(Canvas canvas, int[] histogram, int color) {
        int max = 0 ;
        for ( int i = 0 ; i < histogram . length ; i ++) {
            if ( histogram [ i ] > max ) {
                max = histogram [ i ];
            }
        }
        float w = getWidth (); // - Spline.curveHandleSize();
        float h = getHeight (); // - Spline.curveHandleSize() / 2.0f;
        float dx = 0 ; // Spline.curveHandleSize() / 2.0f;
        float wl = w / histogram.length ;
        float wh = h / max ;

        mPaint.reset ();
        mPaint.setAntiAlias(true);
        mPaint.setARGB( 100 , 255 , 255 , 255 );
        mPaint.setStrokeWidth ((int) Math . ceil ( wl ));

// Draw grid
        mPaint.setStyle(Paint.Style.STROKE );
        canvas.drawRect( dx, 0 , dx + w , h , mPaint );
        canvas.drawLine( dx + w / 3 , 0 , dx + w / 3 , h , mPaint );
        canvas.drawLine( dx + 2 * w / 3 , 0 , dx + 2 * w / 3 , h , mPaint );

        mPaint.setStyle(Paint.Style.FILL );
        mPaint.setColor( color );
        mPaint.setStrokeWidth( 6 );
        mPaint.setXfermode( new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        mHistoPath.reset();
        mHistoPath.moveTo( dx , h );
        boolean firstPointEncountered = false;
        float prev = 0 ;
        float last = 0 ;
        for ( int i = 0 ; i < histogram . length ; i ++) {
            float x = i * wl + dx;
            float l = histogram [ i ] * wh;
            if ( l != 0 ) {
                float v = h - ( l + prev ) / 2.0f;
                if (!firstPointEncountered ) {
                    mHistoPath.lineTo ( x , h );
                    firstPointEncountered = true;
                }
                mHistoPath.lineTo(x ,v);
                prev = l ;
                last = x ;
            }
        }
        mHistoPath.lineTo(last, h);
        mHistoPath.lineTo(w, h);
        mHistoPath.close();
        canvas.drawPath(mHistoPath, mPaint);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle ( Paint . Style . STROKE );
        mPaint.setARGB( 255 , 200 , 200 , 200 );
        canvas.drawPath ( mHistoPath , mPaint );
    }

    public void SetRgbArrays(int[] r, int[]g, int[] b)
    {
        redHistogram = r;
        greenHistogram = g;
        blueHistogram = b;
        invalidate();
    }

    public void SetHistogramData(int[] histo)
    {
        if (histo == null)
            return;
        System.arraycopy( histo , 0 , redHistogram , 0 , 256 );
        System.arraycopy( histo , 256 , greenHistogram , 0 , 256 );
        System.arraycopy( histo , 512 , blueHistogram , 0 , 256 );
        invalidate();
    }



    public void onDraw (Canvas canvas)
    {
        try {

        canvas.drawARGB ( 0 , 0 , 0 , 0 );
        drawHistogram(canvas ,redHistogram , Color.RED);
        drawHistogram(canvas ,greenHistogram , Color.GREEN);
        drawHistogram(canvas ,blueHistogram , Color.BLUE);
        }
        catch (RuntimeException ex)
        {
            Logger.d("histogram","bitmap got released");
        }
    }


}