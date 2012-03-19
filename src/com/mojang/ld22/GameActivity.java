package com.mojang.ld22;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Adapted from lunar lander example
 * 
 * @author schneg
 * 
 */
public class GameActivity extends Activity implements OnTouchListener
{

	// private ImageButton up;
	// private ImageButton down;
	// private ImageButton left;
	// private ImageButton right;
	private Button attack;
	private Button menu;

	private Game game;
	private GameView gameView;

	private Thread gameThread;

	private boolean shouldRun = true;

	public static GameActivity singleton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		singleton = this;

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		setContentView(R.layout.main);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		game = new Game(metrics.widthPixels, metrics.heightPixels);
		gameView = (GameView) findViewById(R.id.gameView);

		// up = (ImageButton)findViewById(R.id.buttonUp);
		// down = (ImageButton)findViewById(R.id.buttonDown);
		// left = (ImageButton)findViewById(R.id.buttonLeft);
		// right = (ImageButton)findViewById(R.id.buttonRight);
		attack = (Button) findViewById(R.id.buttonAttack);
		menu = (Button) findViewById(R.id.buttonMenu);

		// up.setOnTouchListener(this);
		// down.setOnTouchListener(this);
		// left.setOnTouchListener(this);
		// right.setOnTouchListener(this);
		attack.setOnTouchListener(this);
		menu.setOnTouchListener(this);
		gameView.setOnTouchListener(this);

		game.startRun(GameActivity.singleton);

		gameThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while (shouldRun)
				{
					game.iterate(GameView.gameCanvas);

					gameView.post(new Runnable()
					{
						public void run()
						{
							gameView.invalidate();
						}
					});
				}
			}
		});

		gameThread.start();
	}

	@Override
	protected void onPause()
	{
		// Log.e("ON PAUSE!!!!!!!!!!!!!!!", "ON PAUSE!!!!!!!!!!!!!!!");
		// shouldRun = false;
		// gameThread.stop();
		// gameThread.stop();
		super.onPause();
		// game.stop();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		shouldRun = false;
		game.stop();
	};

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		shouldRun = false;
		game.stop();
	};

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	private static final int INVALID_POINTER_ID = -1;
	float cursorX = -1;
	float cursorY = -1;
	int cursorId = -1;
	boolean cursorPressed = false;
	private String TAG = "DEBUG";

	/*
	 * @Override public boolean onTouchEvent(MotionEvent ev) { return
	 * onTouch(null, ev); }
	 */

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		//dumpEvent(event);
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;

		if (v == gameView)
		{
			Log.d("DEBUG", "dispatched to gameView");
			// cursor
			if (actionCode == MotionEvent.ACTION_DOWN || actionCode == MotionEvent.ACTION_POINTER_DOWN)
			{
				boolean match = false;
				for (int i = 0; i < event.getPointerCount(); i++)
				{
					if (event.getX(i) < gameView.getWidth() / 2)
					{
						match = true;
						cursorPressed = true;
						cursorId = event.getPointerId(i); //action >> MotionEvent.ACTION_POINTER_ID_SHIFT;
					}
				}
				if (match == false)
				{
					cursorPressed = false;
					cursorId = INVALID_POINTER_ID;
				}
			}
			if (actionCode == MotionEvent.ACTION_MOVE)
			{
//				// cursor moved outside of pad
//				if (cursorId != INVALID_POINTER_ID && event.getX(cursorId) > gameView.getWidth() / 2)
//				{
//					cursorPressed = false;
//					cursorId = INVALID_POINTER_ID;
//				}
//				// secondary cursor moved inside of pad, and if we currently
//				// have no
//				// pointer associated to the pad promote it
//				for (int i = 0; i < event.getPointerCount(); i++)
//				{
//					if (event.getX(i) < gameView.getWidth() / 2)
//					{
//						cursorPressed = true;
//						cursorId = event.getPointerId(i);// action >>
//															// MotionEvent.ACTION_POINTER_ID_SHIFT;
//					}
//				}
			}
			//Log.i("DEBUG", "Cursor is " + (cursorPressed ? ("pressed, is is " + cursorId + ", position is V(" + event.getX(cursorId) + " | " + event.getY(cursorId) + ")") : "released"));
			if (cursorId != INVALID_POINTER_ID && actionCode == MotionEvent.ACTION_MOVE)
			{
				cursorX = event.getX(cursorId);
				cursorY = event.getY(cursorId);

				float angle = 0.0f;
				float upx = 0.0f, upy = 1.0f;
				angle = (float) Math.atan2(cursorY - gameView.getHeight() / 2, cursorX - gameView.getWidth() / 5);
				angle = (float) Math.toDegrees(angle);
				if (angle < 0) angle += 360.0f;
				//Log.i("angle", "" + (angle));
				
				{
					game.getInputHandler().releaseAll();
					//version without vertical movement
					/*if (range(225, angle, 315) && !game.getInputHandler().isPressed(InputHandler.UP))
					{
						game.getInputHandler().keyEvent(InputHandler.UP, true);
						//game.getInputHandler().keyEvent(InputHandler.DOWN, false);
						//Log.d("debug", "up");
					}
					else if (range(135, angle, 225) && !game.getInputHandler().isPressed(InputHandler.LEFT))
					{
						game.getInputHandler().keyEvent(InputHandler.LEFT, true);
						//game.getInputHandler().keyEvent(InputHandler.RIGHT, false);
					}
					else if (range(45, angle, 135) && !game.getInputHandler().isPressed(InputHandler.DOWN))
					{
						game.getInputHandler().keyEvent(InputHandler.DOWN, true);
						//game.getInputHandler().keyEvent(InputHandler.UP, false);
					}
					else if ((range(315, angle, 360) || range(0, angle, 45))  && !game.getInputHandler().isPressed(InputHandler.RIGHT))
					{
						game.getInputHandler().keyEvent(InputHandler.RIGHT, true);
						//game.getInputHandler().keyEvent(InputHandler.LEFT, false);
					}*/
					
					//version with vertical movement
					if(range(202.5f, angle, 337.5f))// && !game.getInputHandler().isPressed(InputHandler.UP))
					{
						game.getInputHandler().keyEvent(InputHandler.UP, true);
						Log.w("w", "up");
					}
					if(range(157.5f, angle, 22.5))// && !game.getInputHandler().isPressed(InputHandler.DOWN))
					{
						game.getInputHandler().keyEvent(InputHandler.DOWN, true);
						Log.w("w", "down");
					}
					
					if(range(112.5f, angle, 247.5f))// && !game.getInputHandler().isPressed(InputHandler.LEFT))
					{
						game.getInputHandler().keyEvent(InputHandler.LEFT, true);
						Log.w("w", "left");
					}
					if((range(292.5f, angle, 360) || range(0, angle, 67.5f)))// && !game.getInputHandler().isPressed(InputHandler.RIGHT))
					{
						game.getInputHandler().keyEvent(InputHandler.RIGHT, true);
						Log.w("w", "right");
					}
					
					
				}
			}
			else
			// cursor is not pressed, disable all directions, to prevent further running in a direction if no direction is activated
			{
				game.getInputHandler().releaseAll();
			}
		}
		else if (v == attack || v == menu)
		{
			if (actionCode == MotionEvent.ACTION_DOWN || actionCode == MotionEvent.ACTION_UP)
			{
				if (v == attack)
				{
					Log.w("DEBUG", "attack pushed");
					game.getInputHandler().keyEvent(InputHandler.ATTACK, action == MotionEvent.ACTION_DOWN);
					return true;
				}
				if (v == menu)
				{
					Log.w("DEBUG", "menu pushed");
					game.getInputHandler().keyEvent(InputHandler.MENU, action == MotionEvent.ACTION_DOWN);
					return true;
				}
			}
		}
		return true;
	}

	/** Show an event in the LogCat view, for debugging */
	private void dumpEvent(MotionEvent event)
	{
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP)
		{
			sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++)
		{
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount()) sb.append(";");
		}
		sb.append("]");
		Log.d(TAG, sb.toString());
	}

	private boolean range(double a, double x, double b)
	{
		if ((a < x && x < b) || (a > x && x > b)) return true;
		return false;
	}
}
