package projet.m2dl.com.mdlinvaders;

import android.annotation.TargetApi;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import projet.m2dl.com.mdlinvaders.Classes.Invader;
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

    private static final int SIZE_SPACESHIP = 60;
    private static final int MARGIN_BOTTOM_SPACESHIP = 50;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private final int FLAT_INCLINATION = 25;
    private final int TIME_UPDATE_INVADERS = 2000;

    private RelativeLayout rootView;
    private SpaceShip spaceShip;
    private ImageView imgSpaceShip;
    private int marginLeftSpaceShip, marginTopSpaceship;
    private float lastXSpaceShip;
    private DisplayMetrics metrics;
    private ArrayList<Invader> invaders = new ArrayList<>();
    private final int NB_INVADERS_ROW = 4;
    Handler handlerInvaders = new Handler();
    private SensorManager sensorManager;
    private int time_update_invaders = TIME_UPDATE_INVADERS;

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

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.


        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        placeSpaceShip();

        displayInvaders();
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        // add listener. The listener will be  (this) class
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        launchTimer();
    }

    public void placeSpaceShip(){
        imgSpaceShip = (ImageView) rootView.findViewById(R.id.viewSpaceShip);
        imgSpaceShip.setOnTouchListener(moveSpaceShip);
        marginLeftSpaceShip = (metrics.widthPixels/2)-(SIZE_SPACESHIP/2);
        marginTopSpaceship = metrics.heightPixels-SIZE_SPACESHIP;
        lastXSpaceShip = (metrics.widthPixels/2);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(SIZE_SPACESHIP, SIZE_SPACESHIP);
        layoutParams.setMargins(marginLeftSpaceShip, marginTopSpaceship,0,0);
        imgSpaceShip.setLayoutParams(layoutParams);
    }

    public View.OnTouchListener moveSpaceShip = new View.OnTouchListener() {
       public boolean onTouch(View arg0, MotionEvent arg1) {
            switch (arg1.getAction())
            {
                case MotionEvent.ACTION_MOVE:
                {
                    // ici votre code...

                    float deltaX = arg1.getX() - lastXSpaceShip;
                        marginLeftSpaceShip += deltaX;
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(SIZE_SPACESHIP, SIZE_SPACESHIP);
                        //System.out.println("deltaX : " + deltaX);
                        //System.out.println("marginleft : " + marginLeftSpaceShip);
                        System.out.println("ArgX : " + arg1.getX());
                        //System.out.println("lastX : " + lastXSpaceShip);
                        System.out.println("PosSpaceship : " +  (int)(arg1.getX()-(SIZE_SPACESHIP/2)));
                        //((RelativeLayout.LayoutParams)imgSpaceShip.getLayoutParams()).setMargins(marginLeftSpaceShip, marginTopSpaceship, 0, 0);
                        layoutParams.setMargins((int) (arg1.getX()-(SIZE_SPACESHIP/2)), marginTopSpaceship, 0, 0);
                        imgSpaceShip.setLayoutParams(layoutParams);
                        lastXSpaceShip = arg1.getX();
                        imgSpaceShip.invalidate(); // pour invalider l'image et forcer un rappel à la methode onDraw de la classe.
                    }
                }
            return true;
        }
    };

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
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

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

    private void launchTimer(){
        handlerInvaders.postDelayed(invadersRunnable, time_update_invaders);
    }

    private Runnable invadersRunnable = new Runnable() {
        @Override
        public void run() {
            displayInvaders();
            handlerInvaders.postDelayed(invadersRunnable, time_update_invaders);
        }
    };

    private void displayInvaders(){

        Iterator<Invader> invaderIterator = invaders.iterator();
        while (invaderIterator.hasNext()){
            Invader invader = invaderIterator.next();
            invader.updateLigne();
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

        calculateNewTimer(inclination);
    }

    private void calculateNewTimer(int inclination) {
        if (inclination < FLAT_INCLINATION)
        {
            time_update_invaders = TIME_UPDATE_INVADERS;
        }
        else
        {
            if(inclination < FLAT_INCLINATION + 5){
                time_update_invaders = TIME_UPDATE_INVADERS - 300;
            }
            else if (inclination < FLAT_INCLINATION + 10){
                time_update_invaders = TIME_UPDATE_INVADERS - 800;
            }
            else if (inclination < FLAT_INCLINATION + 15){
                time_update_invaders = TIME_UPDATE_INVADERS - 1100;
            }
            else{
                time_update_invaders = TIME_UPDATE_INVADERS - 1500;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
