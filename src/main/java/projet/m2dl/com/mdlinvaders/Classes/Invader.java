package projet.m2dl.com.mdlinvaders.Classes;

/**
 * Created by fabien on 22/01/15.
 */

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.*;
import projet.m2dl.com.mdlinvaders.R;

public class Invader {

    ImageView imageView;
    Context context;

    public Invader(Context context, int position){
        this.context = context;
        imageView = new ImageView(context);
        imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.invaders1));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(60, 60);
        //layoutParams.setMargins(int left, int top, int right, int bottom);
        layoutParams.setMargins(0,0,0,0);
        imageView.setLayoutParams(layoutParams);
    }

    public ImageView getImageView(){
        return this.imageView;
    }
}
