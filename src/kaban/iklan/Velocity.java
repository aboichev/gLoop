package kaban.iklan;

import android.graphics.PointF;
import android.nfc.Tag;
import android.util.Log;

public class Velocity {

    private static final String TAG = Velocity.class.getName();

    private int angle;
    private float speed;

    private float velX;
    private float velY;

    public float getX(){
       return velX;
    }

    public float getY(){
       return velY;
    }

    public void setX(float x){
        this.velX = x;
    }

    public void setY(float y){
        this.velY = y;
    }

    public int getAngle() {
        return angle;
    }

    public void setDir(int degrees) {
        this.angle = degrees;
        update();
    }

    public void setSpeed(float speedMultiplier) {
        this.speed = speedMultiplier;
        update();
    }

    private void update() {
        velX = (float) (speed * Math.cos(Math.toRadians(angle)));
        velY = - (float) (speed * Math.sin(Math.toRadians(angle)));
        Log.d(TAG, "angle = " + angle + " speed = " + speed + " xVel = " + velX + " yVel = " + velY );
    }
}
