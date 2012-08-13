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

    public void updatePos(Velocity velocity, long time, int width, int height) {

        if(time == 0){
            return;
        }

        float velX = velocity.getX();
        float velY = velocity.getY();

        double straitLinePosX = this.x + (velX * time);
        double straitLinePosY = this.y + (velY * time);

        while( straitLinePosX < 0 || straitLinePosY < 0 || straitLinePosX > width || straitLinePosY > height )
        {
            if( straitLinePosX > 1000000 || straitLinePosY > 100000){
                return;
            }
            double hitPosRatio;
            double hitPosX;
            double hitPosY;

            double newPosX;
            double newPosY;

            if( straitLinePosX - width >= straitLinePosY - height) {
                // x-axis hit
                hitPosX = width;
                hitPosRatio = hitPosX / straitLinePosX;
                hitPosY = straitLinePosY * hitPosRatio;
                velX = -velX;
                newPosX = straitLinePosX - width;
                newPosY = hitPosY;
            }
            else {
                // y-axis hit
                hitPosY = height;
                hitPosRatio = hitPosY / straitLinePosY;
                hitPosX = straitLinePosX * hitPosRatio;
                velY = -velY;
                newPosX = hitPosX;
                newPosY = straitLinePosY - height;
            }

            straitLinePosX = newPosX;
            straitLinePosY = newPosY;

            velocity.setX(velX);
            velocity.setY(velY);

            Log.d(TAG, "straitLinePosX = " + straitLinePosX + " straitLinePosY = " + straitLinePosY +
                    " time = " + time + " newPosX = " + newPosX + " newPosY = " + newPosY);
        }

        this.x = (int)Math.round(straitLinePosX);
        this.y = (int)Math.round(straitLinePosY);

        Log.d(TAG, "velX = " + velocity.getX() + " velY = " + velocity.getY() +
                    " time = " + time + " x = " + this.x + " y = " + this.y);

    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap( ball, x, y, null);
    }
}
