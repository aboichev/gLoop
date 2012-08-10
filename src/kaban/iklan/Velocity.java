package kaban.iklan;

public class Velocity {

    private int angle;
    private float speed;

    private float velX;
    private float velY;

    public float getVelX(){
       return velX;
    }

    public int getAngle() {
        return angle;
    }

    public float getVelY(){
       return velY;
    }

    public void setDir(int degrees) {
        this.angle = degrees;
        update();
    }

    public void reverseX(){
        velX = -velX;
    }

    public void reverseY(){
        velY = -velY;
    }

    public void setSpeed(float speedMultiplier) {
        this.speed = speedMultiplier;
        update();
    }

    private void update() {
        velX = (float) - (speed * Math.cos(Math.toRadians(angle)));
        velY = (float)  (speed * Math.sin(Math.toRadians(angle)));
    }
}
