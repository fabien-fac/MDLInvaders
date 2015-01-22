package projet.m2dl.com.mdlinvaders.Classes;

/**
 * Created by fabien on 22/01/15.
 */

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.*;

import java.util.Random;

import projet.m2dl.com.mdlinvaders.R;

public class Invader {

    private final int SIZE_INVADER = 50;
    private final int MIN_TOP = 10;
    private final int MIN_LEFT = 10;
    private final int STRAF_LEFT = 30;
    private final int WIDTH_BETWEEN_INVADERS = 20;
    private final int HEIGHT_BETWEEN_INVADERS = 10;

    private ImageView imageView;
    private Context context;
    private int top = MIN_TOP;
    private int left = 0;
    private int ligne = 0;

    public Invader(Context context, int position){
        this.context = context;
        imageView = new ImageView(context);
        imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), getRandomInvader()));

        left = getInitalLeftFromPosition(position);

        setLayout();
    }


    public ImageView getImageView(){
        return this.imageView;
    }

    public int getRandomInvader(){
        Random r = new Random();
        int val = r.nextInt(4 - 0) + 0;
        int invader;

        switch (val){
            case 0:
                invader = R.drawable.invaders1;
                break;
            case 1:
                invader = R.drawable.invaders2;
                break;
            case 2:
                invader = R.drawable.invaders3;
                break;
            case 3:
                invader = R.drawable.invaders4;
                break;
            default:
                invader = R.drawable.invaders1;
        }

        return invader;
    }

    private int getInitalLeftFromPosition(int position){
       return ((position*SIZE_INVADER) + (WIDTH_BETWEEN_INVADERS *(position+1)) + MIN_LEFT);
    }

    private void setLayout(){
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(SIZE_INVADER, SIZE_INVADER);
        layoutParams.setMargins(left,top,0,0);
        imageView.setLayoutParams(layoutParams);
    }

    public void updateLigne(){
        ligne++;

        if(ligne%2==0){
            left+= STRAF_LEFT;
        }
        else {
            left-= STRAF_LEFT;
        }

        top = (ligne*SIZE_INVADER + MIN_TOP + HEIGHT_BETWEEN_INVADERS);
        setLayout();
    }
}
