package com.tcc.lucas.governorsuggestor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Lucas on 10/16/2015.
 */
public class CpuFreqView extends View
{
    private final String LOG_TAG = getClass().getSimpleName();

    private float mX;
    private float mY;
    private float mWidth;
    private float mHeight;

    private float mStrokeWidth;
    private int mStrokeColor = Color.TRANSPARENT;

    private Shader mGradientShader;

    public CpuFreqView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        mStrokeWidth = 0;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        updateInformation();

        Paint paint = new Paint();
        paint.setShader(mGradientShader);
        paint.setStrokeWidth(mStrokeWidth);
        canvas.drawRect(new RectF(mX, mY, mWidth, mHeight), paint);
    }

    private void updateInformation()
    {
        mX = getX();
        mY = getY();

        mWidth = getWidth();
        mHeight = getHeight();

        if(mGradientShader == null)
            mGradientShader = new LinearGradient(mX, mY, mWidth, mHeight, Color.GREEN, Color.RED, Shader.TileMode.CLAMP);
    }
}
