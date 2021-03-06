package projet.m2dl.com.mdlinvaders;

import android.annotation.TargetApi;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import projet.m2dl.com.mdlinvaders.Classes.Invader;
import projet.m2dl.com.mdlinvaders.Classes.Lazor;
import projet.m2dl.com.mdlinvaders.Classes.SpaceShip;
import projet.m2dl.com.mdlinvaders.util.SystemUiHider;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */

public class GameActivity extends Activity implements SensorEventListener{
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;


    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private final int FLAT_INCLINATION = 25;
    private final int TIME_UPDATE_INVADERS = 2000;
    private final int NB_INVADERS_ROW = 3;
    private final int TIME_UPDATE_LASERS = 200;
    private final int BOMB_DELAY = 10000;

    private RelativeLayout rootView;
    private SpaceShip spaceShip;
    private DisplayMetrics metrics;
    private ArrayList<Invader> invaders = new ArrayList<>();
    private ArrayList<Lazor> lasers = new ArrayList<>();
    Handler handlerInvaders = new Handler();
    Handler handlerAudioRecord = new Handler();
    Handler handlerLasers = new Handler();
    private SensorManager sensorManager;
    private int time_update_invaders = TIME_UPDATE_INVADERS;
    private MediaRecorder mRecorder = null;

    private int cptLaser = 0;

    private boolean bombAvailable = true;
    private int score = 0;
    private int bonus = 1;
    private TextView txtScore;
    private TextView txtBonus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        rootView = (RelativeLayout) findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, rootView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        txtScore = (TextView) rootView.findViewById(R.id.textview_score);
        txtBonus = (TextView) rootView.findViewById(R.id.textview_bonus);
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        spaceShip = new SpaceShip(this, (ImageView)rootView.findViewById(R.id.viewSpaceShip), metrics);

        displayInvaders();
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        // add listener. The listener will be  (this) class
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        startRecord();

        launchAudioRecordTimer();
        launchTimer();
    }

    public void startRecord() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
                mRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return  mRecorder.getMaxAmplitude();
        else
            return 0;

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    /*
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    */

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void launchBombDisabledTimer(){
        Toast.makeText(GameActivity.this, "Bomb disable",
                Toast.LENGTH_LONG).show();
        handlerAudioRecord.postDelayed(BombDisabledRunnable, BOMB_DELAY);
    }

    private Runnable BombDisabledRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(GameActivity.this, "Bomb available",
                            Toast.LENGTH_LONG).show();
            bombAvailable=true;
        }
    };

    private void launchAudioRecordTimer(){
        handlerAudioRecord.postDelayed(audioRecordRunnable, 100);
    }

    private Runnable audioRecordRunnable = new Runnable() {
        @Override
        public void run() {
            double volume = getAmplitude();
            if (bombAvailable){
                if (volume > 25000){
                    bombAvailable = false;
                    launchBombDisabledTimer();
                    destroyAllInvaders();
                    System.out.println("Volume : " + volume);
                }
            }
           handlerAudioRecord.postDelayed(audioRecordRunnable, 100);
        }
    };

    private void destroyAllInvaders(){
        for (Invader curInvader : invaders){
            score = curInvader.destroyInvader(score, bonus);
        }
        txtScore.setText("Score : "+String.valueOf(score));
    }

    private void launchTimer(){
        handlerInvaders.postDelayed(invadersRunnable, time_update_invaders);
        handlerLasers.postDelayed(lasersRunnable, TIME_UPDATE_LASERS);
    }

    private Runnable invadersRunnable = new Runnable() {
        @Override
        public void run() {
            displayInvaders();
            handlerInvaders.postDelayed(invadersRunnable, time_update_invaders);
        }
    };

    private Runnable lasersRunnable = new Runnable() {
        @Override
        public void run() {
            displayLasers();
            detectColisions();
            handlerInvaders.postDelayed(lasersRunnable, TIME_UPDATE_LASERS);
        }
    };

    private void displayInvaders(){
        Iterator<Invader> invaderIterator = invaders.iterator();
        while (invaderIterator.hasNext()){
            Invader invader = invaderIterator.next();
            if(invader.isInvaderDestroyed()){
                rootView.removeView(invader.getImageView());
                invaderIterator.remove();
            }
            else{
                invader.updateLigne();
            }
        }
        for(int i=0; i<NB_INVADERS_ROW; i++){
            Invader invader = new Invader(this, i);
            invaders.add(invader);
            rootView.addView(invader.getImageView());
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            getAccelerometer(event);
        }
    }

    private void getAccelerometer(SensorEvent event) {
        float[] g = new float[3];
        g = event.values.clone();

        double norm_Of_g = Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]);

        // Normalize the accelerometer vector
        g[0] = (float) (g[0] / norm_Of_g);
        g[1] = (float) (g[1] / norm_Of_g);
        g[2] = (float) (g[2] / norm_Of_g);

        int inclination = (int) Math.round(Math.toDegrees(Math.acos(g[2])));

        calculateNewTimerAndBonus(inclination);
    }

    private void calculateNewTimerAndBonus(int inclination) {
        if (inclination < FLAT_INCLINATION)
        {
            time_update_invaders = TIME_UPDATE_INVADERS;
        }
        else
        {
            if(inclination < FLAT_INCLINATION + 5){
                time_update_invaders = TIME_UPDATE_INVADERS - 300;
                bonus = 1;
            }
            else if (inclination < FLAT_INCLINATION + 10){
                time_update_invaders = TIME_UPDATE_INVADERS - 800;
                bonus = 2;
            }
            else if (inclination < FLAT_INCLINATION + 15){
                time_update_invaders = TIME_UPDATE_INVADERS - 1100;
                bonus = 3;
            }
            else{
                time_update_invaders = TIME_UPDATE_INVADERS - 1500;
                bonus = 4;
            }
            txtBonus.setText("Bonus et vitesse : "+String.valueOf(bonus));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void displayLasers(){
        Iterator<Lazor> lazorIterator = lasers.iterator();
        while (lazorIterator.hasNext()){
            Lazor laser = lazorIterator.next();
            laser.updatePosition();
        }

        if(cptLaser == 4){
            Lazor laser = new Lazor(this, spaceShip.getMarginLeftSpaceShip(), spaceShip.getMarginTopSpaceship());
            lasers.add(laser);
            rootView.addView(laser.getImageView());
            cptLaser = 0;
        }

        cptLaser++;
    }

    private void detectColisions(){

        Iterator<Lazor> lazorIterator = lasers.iterator();
        while (lazorIterator.hasNext()){

            Lazor laser = lazorIterator.next();

            Iterator<Invader> invaderIterator = invaders.iterator();
            boolean touched = false;
            while (invaderIterator.hasNext() && !touched){

                Invader invader = invaderIterator.next();

                if(laser.isInvaderTouched(invader)){
                    score = invader.destroyInvader(score, bonus);
                    txtScore.setText("Score : "+String.valueOf(score));
                    invaderIterator.remove();
                    touched = true;
                }
            }

            if(touched){
                lazorIterator.remove();
            }
        }
    }
}
