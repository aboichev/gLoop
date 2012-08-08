package kaban.iklan;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Ball {

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

    public void updatePos(Velocity velocity) {
        this.x += velocity.getVelX();
        this.y += velocity.getVelY();
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap( ball, x, y, null);
    }
}
