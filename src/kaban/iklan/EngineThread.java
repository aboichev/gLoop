package kaban.iklan;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class EngineThread extends Thread {

    private static final String TAG = EngineThread.class.getSimpleName();

    // desired fps
    private final static int 	MAX_FPS = 50;
    // maximum number of frames to be skipped
    private final static int	MAX_FRAME_SKIPS = 5;
    // the frame period
    private final static int	FRAME_PERIOD = 1000 / MAX_FPS;

    // flag to hold game state
    private boolean running;
    private GameInfo gameInfo;

    private SurfaceHolder surfaceHolder;
    private MainView gamePanel;

    public EngineThread(SurfaceHolder surfaceHolder, GameInfo gameInfo, MainView gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
        this.gameInfo = gameInfo;
    }

    public void startLoop() {
        this.running = true;
        this.start();
    }

    public void pauseLoop()
    {
        this.running = false;
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
        gameInfo.tickCount = 0L;
        gameInfo.frameCount = 0L;

        Log.d(TAG, "Starting game loop");

        long beginTime;		// the time when the cycle begun
        long timeDiff;		// the time it took for the cycle to execute
        int sleepTime;		// ms to sleep (<0 if we're behind)
        int framesSkipped;	// number of frames being skipped

        sleepTime = 0;

        while (running) {

            canvas = null;
            gameInfo.tickCount++;

            // try locking the canvas for exclusive pixel editing on the surface
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {

                    beginTime = System.currentTimeMillis();
                    framesSkipped = 0;	// resetting the frames skipped

                    // update game state
                    // draws the canvas on the panel
                    this.gamePanel.update();

                    gameInfo.frameCount++;
                    this.gamePanel.render(canvas);

                    // calculate sleep time
                    timeDiff = System.currentTimeMillis() - beginTime;
                    sleepTime = (int)(FRAME_PERIOD - timeDiff);
                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {}
                    }

                    gameInfo.framesSkipped = 0;
                    while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                        // we need to catch up
                        // update without rendering
                        this.gamePanel.update();
                        // add frame period to check if in next frame
                        sleepTime += FRAME_PERIOD;
                        framesSkipped++;
                        // record last non-zero skipped value;
                        gameInfo.framesSkipped = framesSkipped;
                    }

                    // for statistics
                    framesSkippedPerStatCycle += framesSkipped;
                    // calling the routine to store the gathered statistics
                    storeStats();
                }
            } finally {
                // in case of an exception the surface is not left in
                // an inconsistent state
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            } // end finally
        }
        Log.d(TAG, "Game loop executed " + gameInfo.tickCount + " times");

    }

    /**
    * The statistics - it is called every cycle, it checks if time since last
    * store is greater than the statistics gathering period (1 sec) and if so
    * it calculates the FPS for the last period and stores it.
    *
    *  It tracks the number of frames per period. The number of frames since
    *  the start of the period are summed up and the calculation takes part
    *  only if the next period and the frame count is reset to 0.
    */


    private final static int FPS_HISTORY_SIZE = 10;
    private double 	fpsStore[] = new double[FPS_HISTORY_SIZE];

    // number of frames skipped in a store cycle (1 sec)
    private int framesSkippedPerStatCycle;
    // number of frames skipped since the game started
    private int totalFramesSkipped = 0;

    // number of rendered frames in an interval
    private int frameCountPerStatCycle = 0;
    private long totalFrameCount = 0l;

    // the number of times the stat has been read
    private long 	statsCount = 0;
    // the average FPS since the game started
    private double 	averageFps = 0.0;
    // we'll be reading the stats every second
    private final static int STAT_INTERVAL = 1000; //ms
    // last time the status was stored
    private long lastStatusStore = 0;

    // the status time counter
    private long statusIntervalTimer = 0l;

    private void storeStats() {

        frameCountPerStatCycle++;
        totalFrameCount++;

        // check the actual time
        statusIntervalTimer += (System.currentTimeMillis() - statusIntervalTimer);

        if (statusIntervalTimer >= lastStatusStore + STAT_INTERVAL) {
            // calculate the actual frames per status check interval
            double actualFps = (double)(frameCountPerStatCycle / (STAT_INTERVAL / 1000));

            // stores the latest fps in the array
            fpsStore[(int) statsCount % FPS_HISTORY_SIZE] = actualFps;

            // increase the number of times statistics was calculated
            statsCount++;

            double totalFps = 0.0;
            // sum up the stored fps values
            for (int i = 0; i < FPS_HISTORY_SIZE; i++) {
                totalFps += fpsStore[i];
            }

            // obtain the average
            if (statsCount < FPS_HISTORY_SIZE) {
                // in case of the first 10 triggers
                averageFps = totalFps / statsCount;
            } else {
                averageFps = totalFps / FPS_HISTORY_SIZE;
            }
            // saving the number of total frames skipped
            totalFramesSkipped += framesSkippedPerStatCycle;

            // resetting the counters after a status record (1 sec)
            framesSkippedPerStatCycle = 0;
            statusIntervalTimer = 0;
            frameCountPerStatCycle = 0;


            statusIntervalTimer = System.currentTimeMillis();
            lastStatusStore = statusIntervalTimer;

            gameInfo.averageFps = averageFps ;
        }
    }

}