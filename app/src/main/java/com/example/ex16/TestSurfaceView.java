package com.example.ex16;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread dt;

    public TestSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        dt = new DrawThread(getContext(), holder);
        dt.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        dt.requestStop();
        while (retry) {
            try {
                dt.join();
                retry = false;

            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dt.setXYR(event.getX(), event.getY(), 0);
        return true;
    }
}
class DrawThread extends Thread {
    private SurfaceHolder sh;
    private volatile boolean running = true;
    private boolean started = false;
    private float r = -1;
    private float x = 100;
    private float y = 100;

    public boolean isStarted() {
        return started && running;
    }

    public void setXYR(float x, float y, float r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public DrawThread(Context context, SurfaceHolder sh) {
        this.sh = sh;
    }

    public void requestStop() {
        running = false;
    }

    @Override
    public void run() {
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        while (running) {
            Canvas canvas = sh.lockCanvas();
            if (canvas != null) {
                try {
                    canvas.drawColor(Color.BLUE);
                    canvas.drawCircle(x, y, r, paint);
                } finally {
                    sh.unlockCanvasAndPost(canvas);
                }
            }
            if (r != -1) r+=5;
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}




