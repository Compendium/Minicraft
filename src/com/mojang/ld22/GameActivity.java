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
		// attack = (Button) findViewById(R.id.buttonAttack);
		// menu = (Button) findViewById(R.id.buttonMenu);

		// up.setOnTouchListener(this);
		// down.setOnTouchListener(this);
		// left.setOnTouchListener(this);
		// right.setOnTouchListener(this);
		// attack.setOnTouchListener(this);
		// menu.setOnTouchListener(this);
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

	int attackId = -1;
	int menuId = -1;

	/*
	 * @Override public boolean onTouchEvent(MotionEvent ev) { return
	 * onTouch(null, ev); }
	 */

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		// dumpEvent(event);
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;

		if (v == gameView)
		{
			if (actionCode == MotionEvent.ACTION_DOWN || actionCode == MotionEvent.ACTION_POINTER_DOWN)
			{
				boolean match = false;
				for (int i = 0; i < event.getPointerCount(); i++)
				{
					if (event.getX(i) < gameView.getWidth() / 2)
					{
						match = true;
						cursorPressed = true;
						cursorId = event.getPointerId(i); // action >>
															// MotionEvent.ACTION_POINTER_ID_SHIFT;
					}
					if (event.getX(i) > gameView.getWidth() / 2)
					{
						if (event.getY(i) < gameView.getHeight() / 2)
						{
							game.getInputHandler().keyEvent(InputHandler.MENU, true);
							menuId = event.getPointerId(i);
						}
						else if (event.getY(i) > gameView.getHeight() / 2)
						{
							game.getInputHandler().keyEvent(InputHandler.ATTACK, true);
							attackId = event.getPointerId(i);
						}
					}
				}
				if (match == false)
				{
					cursorPressed = false;
					cursorId = INVALID_POINTER_ID;
				}
			}

			if (actionCode == MotionEvent.ACTION_UP || actionCode == MotionEvent.ACTION_POINTER_UP || actionCode == MotionEvent.ACTION_CANCEL)
			{
				if (action >> MotionEvent.ACTION_POINTER_ID_SHIFT == cursorId)
				{
					cursorPressed = false;
					cursorId = -1;

					game.getInputHandler().keyEvent(InputHandler.UP, false);
					game.getInputHandler().keyEvent(InputHandler.DOWN, false);
					game.getInputHandler().keyEvent(InputHandler.RIGHT, false);
					game.getInputHandler().keyEvent(InputHandler.LEFT, false);
				}
				else if (action >> MotionEvent.ACTION_POINTER_ID_SHIFT == attackId)
				{
					game.getInputHandler().keyEvent(InputHandler.ATTACK, false);
				}
				else if (action >> MotionEvent.ACTION_POINTER_ID_SHIFT == menuId)
				{
					game.getInputHandler().keyEvent(InputHandler.MENU, false);
				}
			}
			if (actionCode == MotionEvent.ACTION_MOVE)
			{
				// // cursor moved outside of pad
				// if (cursorId != INVALID_POINTER_ID && event.getX(cursorId) >
				// gameView.getWidth() / 2)
				// {
				// cursorPressed = false;
				// cursorId = INVALID_POINTER_ID;
				// }
				// // secondary cursor moved inside of pad, and if we currently
				// // have no
				// // pointer associated to the pad promote it
				// for (int i = 0; i < event.getPointerCount(); i++)
				// {
				// if (event.getX(i) < gameView.getWidth() / 2)
				// {
				// cursorPressed = true;
				// cursorId = event.getPointerId(i);// action >>
				// // MotionEvent.ACTION_POINTER_ID_SHIFT;
				// }
				// }
			}
			// Log.i("DEBUG", "Cursor is " + (cursorPressed ? ("pressed, is is "
			// + cursorId + ", position is V(" + event.getX(cursorId) + " | " +
			// event.getY(cursorId) + ")") : "released"));
			if (cursorId != INVALID_POINTER_ID && actionCode == MotionEvent.ACTION_MOVE)
			{
				cursorX = event.getX(cursorId);
				cursorY = event.getY(cursorId);

				float angle = 0.0f;
				angle = (float) Math.atan2(cursorY - gameView.getHeight() / 2, cursorX - gameView.getWidth() / 5);
				angle = (float) Math.toDegrees(angle);
				if (angle < 0) angle += 360.0f;
				// Log.i("angle", "" + (angle));

				{
					if (range(202.5f, angle, 337.5f))
					{
						if (!game.getInputHandler().isPressed(InputHandler.UP)) game.getInputHandler().keyEvent(InputHandler.UP, true);
					}
					else if (range(157.5f, angle, 22.5))
					{
						if (!game.getInputHandler().isPressed(InputHandler.DOWN)) game.getInputHandler().keyEvent(InputHandler.DOWN, true);
					}
					else
					{
						game.getInputHandler().keyEvent(InputHandler.UP, false);
						game.getInputHandler().keyEvent(InputHandler.DOWN, false);
					}

					if (range(112.5f, angle, 247.5f))
					{
						if (!game.getInputHandler().isPressed(InputHandler.LEFT)) game.getInputHandler().keyEvent(InputHandler.LEFT, true);
					}
					else if ((range(292.5f, angle, 360) || range(0, angle, 67.5f)))
					{
						if (!game.getInputHandler().isPressed(InputHandler.RIGHT)) game.getInputHandler().keyEvent(InputHandler.RIGHT, true);
					}
					else
					{
						game.getInputHandler().keyEvent(InputHandler.RIGHT, false);
						game.getInputHandler().keyEvent(InputHandler.LEFT, false);
					}

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
