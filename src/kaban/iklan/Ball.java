package kaban.iklan;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

public class Ball {

    private static final String TAG = Ball.class.getName();

    private double x;
    private double y;
    private Bitmap ball;

    public Ball(Bitmap bitmap) {
        this.ball = bitmap;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public RectF getBounds(){
        return new RectF((float)x, (float)y, (float) (x + ball.getWidth()), (float)(y + ball.getHeight()));
    }

    public void setX(int xPox) {
        this.x = xPox;
    }

    public void setY(int yPox) {
        this.y = yPox;
    }

    public void updatePos(Velocity velocity, long time, int width, int height) {

        if(time == 0) {
            return;
        }

        float velX = velocity.getX();
        float velY = velocity.getY();

        width = width - ball.getWidth();
        height = height - ball.getHeight();

        double distX = this.x + (velX * time);
        double distY = this.y + (velY * time);

        while( distX < 0 || distY < 0 || distX > width || distY > height )
        {
            Log.d(TAG, "before update: distX = " + distX + " distY = " + distY + " time = " + time );
            // if both distX and distY off-screen to the right and bottom
            if( distX > width && distY > height) {

                if( distX - width > distY - height) {
                    // x-axis hit first
                    velX = -velX;
                    distX = width - (distX - width);
                }
                else {
                    // y-axis hit first
                    velY = -velY;
                    distY = height - (distY - height);
                }
                continue;
            }

            // if both distX and distY off-screen to the right and top
            if( distX > width && distY < 0) {

                if( distX - width > - distY) {
                    // x-axis hit first
                    velX = -velX;
                    distX = width - (distX - width);
                }
                else {
                    // y-axis hit first
                    velY = -velY;
                    distY = -distY;
                }
                continue;
            }

            // if both distX and distY off-screen to the left and top
            if( distX < 0 && distY < 0) {

                if( distX > distY) {
                    // x-axis hit first
                    velX = -velX;
                    distX = -distX;
                }
                else {
                    // y-axis hit first
                    velY = -velY;
                    distY = -distY;
                }
                continue;
            }

            // if both distX and distY off-screen to the left and bottom
            if( distX < 0 && distY > height) {

                if( -distX > distY - height) {
                    // x-axis hit first
                    velX = -velX;
                    distX = -distX;
                }
                else {
                    // y-axis hit first
                    velY = -velY;
                    distY = height - (distY - height);
                }
                continue;
            }

            if( distX > width) {
                // off-screen to the right
                velX = -velX;
                distX = width - (distX - width);
                continue;
            }

            if( distX < 0) {
                // off-screen to the left
                velX = -velX;
                distX = -distX;
                continue;
            }

            if( distY > height) {
                // off-screen to the bottom;
                velY = -velY;
                distY = height - (distY - height);
                continue;
            }

            if( distY < 0) {
                // off-screen to the top;
                velY = -velY;
                distY = -distY;
                continue;
            }
        }

        velocity.setX(velX);
        velocity.setY(velY);

        this.x = distX;
        this.y = distY;

        Log.d(TAG, "after update: distX = " + distX + " distY = " + distY);
        Log.d(TAG, "velX = " + velocity.getX() + " velY = " + velocity.getY());

    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap( ball, (int) Math.round(this.x), (int) Math.round(this.y), null);
    }
}
