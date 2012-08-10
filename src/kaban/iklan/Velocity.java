package kaban.iklan;

public class Velocity {

    private int angle;
    private float speed;

    private int velX;
    private int velY;

    public int getVelX(){
       return velX;
    }

    public int getAngle() {
        return angle;
    }

    public int getVelY(){
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
        velX = -(int) (speed * Math.cos(Math.toRadians(angle)));
        velY = (int) (speed * Math.sin(Math.toRadians(angle)));
    }
}
