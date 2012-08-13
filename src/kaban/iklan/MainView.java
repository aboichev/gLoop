package kaban.iklan;
import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.text.DecimalFormat;

public class MainView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = MainView.class.getName();
    private GameInfo gameInfo;
    private DecimalFormat decimalFormat = new DecimalFormat("0.##");

    private Velocity velocity;
    private Ball ball;
    private Rect canvasRect;

    private EngineThread engine;

    public MainView(Context context) {
        super(context);
        getHolder().addCallback(this);

        // create the game loop thread
        gameInfo = new GameInfo();
        engine = new EngineThread(getHolder(), gameInfo, this);
        ball = new Ball(BitmapFactory.decodeResource(getResources(), R.drawable.ball));
        velocity = new Velocity();

        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
       canvasRect = new Rect(0,0, width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        velocity.setDir(320);
        velocity.setSpeed(3f);
        ball.setX(400);
        ball.setY(400);

        engine.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        engine.stopLoop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getY() > getHeight() - 50) {
                engine.stopLoop();
                ((Activity)getContext()).finish();
            } else {
                if(engine.isLoopRunning()) {
                    engine.pauseLoop();
                }
                else {
                    engine.resumeLoop();
                }

                Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
            }
        }
        return super.onTouchEvent(event);
    }

    protected void update(long time) {

        ball.updatePos(velocity, time, this.getWidth(), this.getHeight());
    }

//    private void bounceOffWall() {
//
//		// top wall
//		if(!canvasRect.contains(ball.getBounds().left, ball.getBounds().top) &&
//		   !canvasRect.contains(ball.getBounds().right, ball.getBounds().top)) {
//			velocity.reverseY();
//			return;
//		}
//
//        // bottom wall
//        if(!canvasRect.contains(ball.getBounds().left, ball.getBounds().bottom) &&
//           !canvasRect.contains(ball.getBounds().right, ball.getBounds().bottom)) {
//            velocity.reverseY();
//            return;
//        }
//
//        // left wall
//        if(!canvasRect.contains(ball.getBounds().left, ball.getBounds().top)  &&
//            !canvasRect.contains(ball.getBounds().left, ball.getBounds().bottom)) {
//            velocity.reverseX();
//            return;
//        }
//        // right wall
//        if(!canvasRect.contains(ball.getBounds().right, ball.getBounds().top)  &&
//            !canvasRect.contains(ball.getBounds().right, ball.getBounds().bottom)) {
//            velocity.reverseX();
//            return;
//        }
//    }

    private Paint paint = new Paint();
    protected void render(Canvas canvas) {

        // draw background
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        // draw ball
        ball.draw(canvas);

        // draw info bar
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText(" Frames: " + gameInfo.getTotalFrames() +
                        " FPS: "  + decimalFormat.format(gameInfo.getAverageFps()) +
                        " Skipped: " + gameInfo.getAvgFramesSkipped()  +
                        " " + gameInfo.getMessage(), 10, 30, paint);
    }
}