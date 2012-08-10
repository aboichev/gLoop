package kaban.iklan;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class Ball {

    private static final String TAG = Ball.class.getName();

    private int x;
    private int y;
    private Bitmap ball;

    public Ball(Bitmap bitmap) {
        this.ball = bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rect getBounds(){
        return new Rect(x, y, x + ball.getWidth(), y + ball.getHeight());
    }

    public void setX(int xPox) {
        this.x = xPox;
    }

    public void setY(int yPox) {
        this.y = yPox;
    }

    public void updatePos(Velocity velocity, long time) {

        this.x += velocity.getVelX() * time;
        this.y += velocity.getVelY() * time;
        Log.d(TAG, "velX=" + velocity.getVelX() + " velY=" + velocity.getVelY() +
                    " time=" + time);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap( ball, x, y, null);
    }
}
