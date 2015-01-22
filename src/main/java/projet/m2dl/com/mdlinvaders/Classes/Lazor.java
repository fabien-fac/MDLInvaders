package projet.m2dl.com.mdlinvaders.Classes;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import projet.m2dl.com.mdlinvaders.R;

/**
 * Created by fabien on 22/01/15.
 */
public class Lazor {

    private final int SIZE_LASER = 30;
    private final int MIN_TOP = 0;

    private ImageView imageView;
    private Context context;
    private int top = MIN_TOP;
    private int left = 0;

    public Lazor(Context context, int leftShipPosition, int topShipPosition){
        this.context = context;
        imageView = new ImageView(context);
        imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.laser));

        left = getInitalLeftFromPosition(leftShipPosition);
        top = getInitalTopFromPosition(topShipPosition);

        setLayout();
    }

    private int getInitalLeftFromPosition(int leftShipPosition){
        return leftShipPosition;
    }

    private int getInitalTopFromPosition(int topShipPosition){
        return topShipPosition + SIZE_LASER;
    }

    private void setLayout(){
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(SIZE_LASER, SIZE_LASER);
        layoutParams.setMargins(left,top,0,0);
        imageView.setLayoutParams(layoutParams);
    }

    public void updatePosition(){
        top -= SIZE_LASER;
        setLayout();
    }

    public ImageView getImageView(){
        return imageView;
    }

    public boolean isInvaderTouched(Invader invader){
        boolean touched = false;

        if(invader.isInvaderDestroyed()){
            return touched;
        }

        int xLaser = this.left + (SIZE_LASER / 2);
        int yLaserTop = this.top;
        int yLaserBottom = this.top + SIZE_LASER;

        if(xLaser >= invader.getLeft() && xLaser <= (invader.getLeft() + invader.SIZE_INVADER)){
            if( (yLaserTop >= invader.getTop()) && yLaserTop <= (invader.getTop() + invader.SIZE_INVADER)
                    || (yLaserBottom >= invader.getTop()) && yLaserBottom <= (invader.getTop() + invader.SIZE_INVADER) ){
                touched = true;
            }
        }


        return touched;
    }

}
