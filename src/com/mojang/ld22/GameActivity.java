package com.mojang.ld22;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Adapted from lunar lander example
 * @author schneg
 *
 */
public class GameActivity extends Activity implements OnTouchListener {

	private ImageButton up;
	private ImageButton down;
	private ImageButton left;
	private ImageButton right;
	private Button attack;
	private Button menu;

    private Game game;
    private GameView gameView;

    private Thread gameThread;

    private boolean shouldRun = true;
    
    public static GameActivity singleton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        singleton = this;
        
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		setContentView(R.layout.main);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
        game = new Game(metrics.widthPixels, metrics.heightPixels);
        gameView = (GameView)findViewById(R.id.gameView);

        up = (ImageButton)findViewById(R.id.buttonUp);
		down = (ImageButton)findViewById(R.id.buttonDown);
		left = (ImageButton)findViewById(R.id.buttonLeft);
		right = (ImageButton)findViewById(R.id.buttonRight);
		attack = (Button)findViewById(R.id.buttonAttack);
		menu = (Button)findViewById(R.id.buttonMenu);
		
		up.setOnTouchListener(this);
		down.setOnTouchListener(this);
		left.setOnTouchListener(this);
		right.setOnTouchListener(this);
		attack.setOnTouchListener(this);
		menu.setOnTouchListener(this);

        game.startRun(GameActivity.singleton);

        gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(shouldRun)
                {
                    game.iterate(GameView.gameCanvas);

                    gameView.post(new Runnable() {
                        public void run() {
                            gameView.invalidate();
                        }
                    });
                }
            }
        });

        gameThread.start();
	}
	
	@Override
	protected void onPause() {
        //Log.e("ON PAUSE!!!!!!!!!!!!!!!", "ON PAUSE!!!!!!!!!!!!!!!");
        //shouldRun = false;
        //gameThread.stop();
        //gameThread.stop();
		super.onPause();
        //game.stop();
	}

	@Override
	protected void onStop() {
		super.onStop();
		shouldRun = false;
		game.stop();
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		shouldRun = false;
		game.stop();
	};
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP)
		{
			if (v == up)
			{
				game.getInputHandler().keyEvent(InputHandler.UP, action == MotionEvent.ACTION_DOWN);
				return true;
			}
			if (v == down)
			{
                game.getInputHandler().keyEvent(InputHandler.DOWN, action == MotionEvent.ACTION_DOWN);
				return true;
			}
			if (v == left)
			{
                game.getInputHandler().keyEvent(InputHandler.LEFT, action == MotionEvent.ACTION_DOWN);
				return true;
			}
			if (v == right)
			{
                game.getInputHandler().keyEvent(InputHandler.RIGHT, action == MotionEvent.ACTION_DOWN);
				return true;
			}
			if (v == attack)
			{
                game.getInputHandler().keyEvent(InputHandler.ATTACK, action == MotionEvent.ACTION_DOWN);
				return true;
			}
			if (v == menu)
			{
				game.getInputHandler().keyEvent(InputHandler.MENU, action == MotionEvent.ACTION_DOWN);
				return true;
			}
			
		}
		return false;
	}
}
