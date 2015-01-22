package projet.m2dl.com.mdlinvaders.Classes;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import projet.m2dl.com.mdlinvaders.R;

/**
 * Created by Joris on 22/01/2015.
 */
public class SpaceShip {
    ImageView imgSpaceShip;
    Context context;
    public static final int SIZE_SPACESHIP = 75;
    private static final int MARGIN_BOTTOM_SPACESHIP = 25;
    private DisplayMetrics metrics;
    private int marginLeftSpaceShip, marginTopSpaceship;

    public SpaceShip(Context context, ImageView imgSpaceShip, DisplayMetrics metrics){
        this.context = context;
        this.imgSpaceShip = imgSpaceShip;
        this.metrics = metrics;
        imgSpaceShip.setOnTouchListener(moveSpaceShip);
        marginLeftSpaceShip = (metrics.widthPixels/2)-(SIZE_SPACESHIP/2);
        marginTopSpaceship = metrics.heightPixels-SIZE_SPACESHIP-MARGIN_BOTTOM_SPACESHIP;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(SIZE_SPACESHIP, SIZE_SPACESHIP);
        layoutParams.setMargins(marginLeftSpaceShip, marginTopSpaceship,0,0);
        imgSpaceShip.setLayoutParams(layoutParams);
    }

    public View.OnTouchListener moveSpaceShip = new View.OnTouchListener() {
        public boolean onTouch(View arg0, MotionEvent arg1) {
            RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) imgSpaceShip.getLayoutParams();
            if (arg1.getAction()==MotionEvent.ACTION_DOWN) {
                marginLeftSpaceShip = (int)arg1.getX();
                imgSpaceShip.bringToFront();
                return true;
            }
            if (arg1.getAction()==MotionEvent.ACTION_MOVE) {
                if ((arg1.getRawX() - marginLeftSpaceShip)>0 && (arg1.getRawX() - marginLeftSpaceShip)<(metrics.widthPixels-SIZE_SPACESHIP)){
                    layout.leftMargin = (int) arg1.getRawX() - marginLeftSpaceShip;
                }
            }
            imgSpaceShip.setLayoutParams(layout);
            return true;
        }
    };

    public int getMarginLeftSpaceShip() {
        return marginLeftSpaceShip;
    }

    public int getMarginTopSpaceship() {
        return marginTopSpaceship;
    }

   /* public ImageView getImageView(){
        return this.imgSpaceShip;
    }

    public void setImageView(ImageView imageView) {
        this.imgSpaceShip = imageView;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static int getSizeSpaceship() {
        return SIZE_SPACESHIP;
    }

    public static int getMarginBottomSpaceship() {
        return MARGIN_BOTTOM_SPACESHIP;
    }

    public int getMarginLeftSpaceShip() {
        return marginLeftSpaceShip;
    }

    public void setMarginLeftSpaceShip(int marginLeftSpaceShip) {
        this.marginLeftSpaceShip = marginLeftSpaceShip;
    }

    public int getMarginTopSpaceship() {
        return marginTopSpaceship;
    }

    public void setMarginTopSpaceship(int marginTopSpaceship) {
        this.marginTopSpaceship = marginTopSpaceship;
    }*/

}
