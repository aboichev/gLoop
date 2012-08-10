package kaban.iklan;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class EngineThread extends Thread {

    private static final String TAG = EngineThread.class.getName();

    // desired fps
    private final static int 	MAX_FPS = 50;
    // maximum number of frames to be skipped
    private final static int	MAX_FRAME_SKIPS = 5;
    // the frame period
    private final static int	FRAME_PERIOD = 1000 / MAX_FPS;

    // flag to hold game state
    private boolean running;
    private boolean isPaused;
    private GameInfo gameInfo;

    private SurfaceHolder surfaceHolder;
    private MainView gamePanel;

    public EngineThread(SurfaceHolder surfaceHolder, GameInfo gameInfo, MainView gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
        this.gameInfo = gameInfo;
    }

    @Override
    public void start() {
        this.running = true;
        super.start();
    }

    public boolean isLoopRunning() {
       return !isPaused;
    }

    public void pauseLoop()
    {
        this.isPaused = true;
    }

    public void resumeLoop(){
        this.isPaused = false;
    }

    public void stopLoop() {

        this.running = false;
        boolean retry = true;
        while (retry) {
            try {
                this.join(100);
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the thread
            }
        }
    }

    @Override
    public void run() {

        Canvas canvas;

        Log.d(TAG, "Starting game loop");

        long beginTime;		// the time when the cycle begun
        long timeDiff = 0;		// the time it took for the cycle to execute
        int sleepTime = 0;		// ms to sleep (<0 if we're behind)
        int framesSkipped;	// number of frames being skipped


        while (running) {

            canvas = null;

            // try locking the canvas for exclusive pixel editing on the surface
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {

                    beginTime = System.currentTimeMillis();
                    framesSkipped = 0;	// resetting the frames skipped

                    if(!isPaused){
                    // / update state
                    this.gamePanel.update(timeDiff);
                    }
                    else { // paused
                        try {
                            Thread.sleep(sleepTime);
                        }  catch (InterruptedException e) {}
                        // draws after some sleep without updating stats
                        this.gamePanel.render(canvas);
                        continue;
                    }
                    // draw
                    this.gamePanel.render(canvas);

                    // calculate sleep time
                    timeDiff = System.currentTimeMillis() - beginTime;
                    sleepTime = (int)(FRAME_PERIOD - timeDiff);
                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {}
                    }


                    while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                        // update without rendering
                        this.gamePanel.update(timeDiff);
                        // add frame period to check if in next frame
                        sleepTime += FRAME_PERIOD;
                        framesSkipped++;
                    }
                }
                // update frames stats
                gameInfo.update(framesSkipped);
            } finally {
                // in case of an exception the surface is not left in
                // an inconsistent state
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            } // end finally
        }
        Log.d(TAG, "Game loop drawn " + gameInfo.getTotalFrames() + " frames");
    }

}