package zeeb.de.kissthefrog;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import java.util.Random;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final int FROG_ID = View.generateViewId();
    private int points;
    private int round;
    private int countdown;
    private int highscore;
    private Random rnd = new Random();
    private ImageView frog;
    private Handler handler = new Handler();
    private Typeface ttf;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            countdown();
        }
    };

    private void newGame(){
        points = 0;
        round = 1;
        initRound();
    }

    private void initRound(){
        countdown=10;
        ViewGroup container = (ViewGroup) findViewById(R.id.container);
        container.removeAllViews();
        WimmelView wv = new WimmelView(this);
        container.addView(wv, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wv.setImageCount(2+round);
        frog = new ImageView(this);
        frog.setId(FROG_ID);
        frog.setImageResource(R.drawable.frog);
        frog.setScaleType(ImageView.ScaleType.CENTER);
        float scale = getResources().getDisplayMetrics().density;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(Math.round(64*scale),Math.round(61*scale));
        lp.gravity = Gravity.TOP + Gravity.LEFT;
        lp.leftMargin = rnd.nextInt(container.getWidth()-168);
        lp.topMargin = rnd.nextInt(container.getHeight()-160);
        frog.setOnClickListener(this);
        container.addView(frog, lp);
        update();
        //handler.postDelayed(runnable, 1000 - round * 50);
        handler.postDelayed(runnable, 1000);
    }

    private void update(){
        fillTextView(R.id.points, points);
        fillTextView(R.id.round, round);
        fillTextView(R.id.countdown, countdown);
        loadHighscore();
        fillTextView(R.id.highscore, highscore);
    }

    private void loadHighscore() {
        SharedPreferences sp = this.getPreferences(MODE_PRIVATE);
        highscore = sp.getInt("highscore",0);
    }

    private void countdown(){
        countdown--;
        update();
        if(countdown<=0){
            frog.setOnClickListener(null);
            if(points>highscore){
                saveHighscore(points);
            }
            showGameOverFragement();
        } else {
            handler.postDelayed(runnable, 1000 - round * 50);
        }
    }

    private void saveHighscore(int points) {
        highscore = points;
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("highscore", highscore);
        editor.commit();
    }

    private void fillTextView(int id, int number){
        try {
            TextView tv = (TextView) findViewById(id);
            if(id == R.id.points)
                tv.setText(number + " ");
            else if(id == R.id.round)
                tv.setText(" " + number);
            else if(id == R.id.countdown)
                tv.setText(number + " ");
            else
                tv.setText(number + "");
        }
        catch (Exception e){
            Log.d("Filltext",e.toString());
        }
    }
    private void showStartFragemnt(){
        findViewById(R.id.help).setVisibility(View.VISIBLE);
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.fragment_start,null));
        container.findViewById(R.id.start).setOnClickListener(this);
        ((TextView)findViewById(R.id.title)).setTypeface(ttf);
        ((TextView)findViewById(R.id.start)).setTypeface(ttf);
        findViewById(R.id.container).setBackgroundResource(R.drawable.background);
    }

    private void showGameOverFragement(){
        findViewById(R.id.help).setVisibility(View.VISIBLE);
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.addView(getLayoutInflater().inflate(R.layout.fragment_gameover,null));
        container.findViewById(R.id.playagain).setOnClickListener(this);
        ((TextView)findViewById(R.id.title)).setTypeface(ttf);
        ((TextView)findViewById(R.id.playagain)).setTypeface(ttf);
        findViewById(R.id.container).setBackgroundResource(R.drawable.dialog_background);
    }

    private void startGame(){
        findViewById(R.id.help).setVisibility(View.INVISIBLE);
        findViewById(R.id.container).setBackgroundResource(R.drawable.dialog_background);
        newGame();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ttf = Typeface.createFromAsset(getAssets(),"Drift.ttf");
        ((TextView)findViewById(R.id.countdown)).setTypeface(ttf);
        ((TextView)findViewById(R.id.round)).setTypeface(ttf);
        ((TextView)findViewById(R.id.points)).setTypeface(ttf);
        ((TextView)findViewById(R.id.help)).setTypeface(ttf);
        ((TextView)findViewById(R.id.highscore)).setTypeface(ttf);
        findViewById(R.id.help).setOnClickListener(this);
        loadHighscore();
        fillTextView(R.id.highscore,highscore);
        showStartFragemnt();
    }

    @Override
    public void onClick(View v) {
        kissFrog(v);
    }

    private void kissFrog(View v) {
        if(v.getId() == R.id.start){
            startGame();
        } else if(v.getId()==R.id.playagain){
            showStartFragemnt();
        } else if(v.getId()== FROG_ID){
            handler.removeCallbacks(runnable);
            //showToast(R.string.kissed);
            points += countdown * 1000;
            round++;
            initRound();
        } else if(v.getId() == R.id.help){
            showTutorial();
        }
    }
    private void showTutorial(){
        final Dialog dialog = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_tutorial);
        ((TextView)(dialog.findViewById(R.id.start))).setTypeface(ttf);
        ((TextView)(dialog.findViewById(R.id.text))).setTypeface(ttf);
        dialog.show();
        dialog.findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startGame();
            }
        });
    }
    private void showToast(int strResID) {
        Toast toast = new Toast(this);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        TextView textView = new TextView(this);
        textView.setText(strResID);
        textView.setTypeface(ttf);
        textView.setTextSize(48f);
        toast.setView(textView);
        toast.show();
    }

    @Override
    public void onPause(){
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}