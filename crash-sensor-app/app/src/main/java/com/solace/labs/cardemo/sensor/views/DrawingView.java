/*******************************************************************************
 * Copyright (c) 2014-2015 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors: Allan Marube, Mike Robertson
 *******************************************************************************/
package com.solace.labs.cardemo.sensor.views;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.solace.labs.cardemo.sensor.IoTStarterApplication;

/**
 * View that contains canvas to draw upon, handles all touch Events for
 * draw, drag.
 * Created by Allan Marube on 7/15/2014.
 */
public class DrawingView extends View {
    private final static String TAG = DrawingView.class.getName();
    private IoTStarterApplication app;
    private Context context;
    private Path drawPath; //user drawPath

    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    private float previousX;
    private float previousY;
    //canvas
    private Canvas drawCanvas; //canvas
    private int width = 0; //canvas width
    private int height = 0;//canvas height
    //canvas bitmap
    private Bitmap canvasBitmap;

    public DrawingView( Context context, AttributeSet attrs){
        super (context, attrs);
        setupDrawing();
    }

    /**
     * Initializes canvas and drawing classes.
     */
    private void setupDrawing() {
        //get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setDither(true);
        drawPaint.setPathEffect(new CornerPathEffect(10));
        int paintColor = Color.BLACK;
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        int strokeWidth = 5;
        drawPaint.setStrokeWidth(strokeWidth);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);

        if (this.getHeight() > 0 && this.getWidth() > 0) {
            canvasBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(canvasBitmap);
        }
    }

    /**
     * Set the context for the DrawingView
     *
     * @param context The context to use.
     */
    public void setContext(Context context) {
        Log.d(TAG, "setContext()");
        this.context = context;
        app = (IoTStarterApplication) context.getApplicationContext();
    }

    /**
     * Resize the view.
     *
     * @param w New view width.
     * @param h New view height.
     * @param oldw Old view width.
     * @param oldh Old view height.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "onSizeChanged()");
        //view given size
        width = w;
        height = h;
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        if (app != null) {
            colorBackground(app.getColor());
        }
    }

    /**
     *
     * @param color The color to set the canvas background to
     */
    public void colorBackground(int color) {
        if (drawCanvas != null) {
            // Draw white first in case alpha value is < 255
            //drawCanvas.drawColor(Color.WHITE);
            drawCanvas.drawColor(Color.argb(1, 58, 74, 83));
            drawCanvas.drawColor(color);
            invalidate();
        }
    }

    /**
     * Draw the path on the canvas.
     * @param canvas The canvas to draw on.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (drawCanvas != null) {
            drawCanvas.drawPath(drawPath, drawPaint);
            canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        }
    }

  }