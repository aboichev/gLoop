package kaban.iklan;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class EngineThread extends Thread {

    private static final String TAG = EngineThread.class.getName();

    // desired fps
    private final static int 	MAX_FPS = 25;
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

    public void pauseLoop() {
        this.isPaused = true;
    }

    public void resumeLoop() {
        this.isPaused = false;
    }

    public void stopLoop() {

        this.running = false;
        boolean retry = true;
        while (retry) {
            try {
                this.join();
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
        int sleepTime;		// ms to sleep (<0 if we're behind)
        int framesSkipped;	// number of frames being skipped

        while (running) {

            canvas = null;
            beginTime = System.currentTimeMillis();
            framesSkipped = 0;	// resetting the frames skipped

            if( isPaused) {
                // render a paused state and sleep.
                try { // lock canvas and draw
                    canvas = this.surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        // draw a frame
                        this.gamePanel.render(canvas);
                    }
                }
                finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                // sleep for 2 sec to consume battery
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {}
                continue;
            } // end of paused state loop

            // Updating game state
            Log.d(TAG, "Updating game state. timeDiff = " + timeDiff);
            this.gamePanel.update(timeDiff);

            try { // lock canvas and draw
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    // draw a frame
                    this.gamePanel.render(canvas);
                }
            }
            finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            // calculate sleep time
            timeDiff = System.currentTimeMillis() - beginTime;
            sleepTime = (int)(FRAME_PERIOD - timeDiff);
            if (sleepTime > 0) {
                try {
                    Log.d(TAG, "Sleeping for " + sleepTime + "ms");
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {}
            }

//            while (sleepTime <= 0 && framesSkipped < MAX_FRAME_SKIPS) {
//                // update without rendering
//                beginTime = System.currentTimeMillis();
//                Log.d(TAG, "Updating game state. Frame skipped. timeDiff = " + timeDiff);
//                this.gamePanel.update(timeDiff);
//                // add frame period to check if in next frame
//                sleepTime += FRAME_PERIOD;
//                framesSkipped++;
//                timeDiff = System.currentTimeMillis() - beginTime;
//            }
            // update frames stats
            gameInfo.update(framesSkipped);
        }
        Log.d(TAG, "Game loop drawn " + gameInfo.getTotalFrames() + " frames");
    }

}