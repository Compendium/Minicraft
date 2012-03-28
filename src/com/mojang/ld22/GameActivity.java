package com.mojang.ld22;

import com.mojang.ld22.screen.TitleMenu;

import oz.wizards.minicraft.R;
import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;

public class GameActivity extends Activity implements OnTouchListener {
	public Game game = null;
	private GameView gameView;

	private Thread gameThread;

	private boolean shouldRun = true;
	private boolean saved = false;

	public static GameActivity singleton;

	public int width = 0, height = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		singleton = this;

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		setContentView(R.layout.main);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		width = metrics.widthPixels;
		height = metrics.heightPixels;
		game = new Game(metrics.widthPixels, metrics.heightPixels);

		gameView = (GameView) findViewById(R.id.gameView);
		gameView.setOnTouchListener(this);

		game.startRun(GameActivity.singleton);
		gameThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (shouldRun) {
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
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (shouldRun == false) {
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			game = new Game(metrics.widthPixels, metrics.heightPixels);

			gameView = (GameView) findViewById(R.id.gameView);
			gameView.setOnTouchListener(this);
			gameThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (shouldRun) {
						game.iterate(GameView.gameCanvas);

						gameView.post(new Runnable() {
							public void run() {
								gameView.invalidate();
							}
						});
					}
				}
			});
			game.setMenu(new TitleMenu());
			game.startRun(this);
			gameThread.start();
			shouldRun = true;

			saved = false;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		shouldRun = false;
		game.stop();
		considerSaving();
		Log.w("DEBUG", "pause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		shouldRun = false;
		game.stop();
		considerSaving();
		Log.w("DEBUG", "stop");
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		shouldRun = false;
		game.stop();
		considerSaving();

		Log.w("DEBUG", "destroy");
		this.finish();
	};

	private void considerSaving() {
		if (saved == false) {
			saved = true;

			game.save();
			game.saveSettings();
		}
	}

	@Override
	public void onBackPressed() {
		if (game.menu instanceof TitleMenu) {
			this.finish();
		} else {
			saved = false;
			considerSaving();
			game.setMenu(new TitleMenu());
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private static final int INVALID_POINTER_ID = -1;
	public float cursorX = -1;
	public float cursorY = -1;
	public float cursorCenterX = -1;
	public float cursorCenterY = -1;
	int cursorId = -1;
	public boolean cursorPressed = false;

	int attackId = -1;
	int menuId = -1;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// dumpEvent(event);
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;

		if (v == gameView) {
			if (actionCode == MotionEvent.ACTION_DOWN || actionCode == MotionEvent.ACTION_POINTER_DOWN) {
				boolean match = false;
				for (int i = 0; i < event.getPointerCount(); i++) {
					if ((game.settings.controlshflipped ? event.getX(i) > gameView.getWidth() / 2 : event.getX(i) < gameView.getWidth() / 2)) {
						match = true;
						cursorPressed = true;
						cursorId = event.getPointerId(i); // action >>
															// MotionEvent.ACTION_POINTER_ID_SHIFT;
						cursorCenterX = (game.settings.controlshflipped ? gameView.getWidth() - gameView.getWidth() / 5 : gameView.getWidth() / 5);
						cursorCenterY = gameView.getHeight() / 2;
					}
					if ((game.settings.controlshflipped ? event.getX(i) < gameView.getWidth() / 2 : event.getX(i) > gameView.getWidth() / 2)) {
						if (event.getY(i) < gameView.getHeight() / 2) {
							game.getInputHandler().keyEvent(InputHandler.MENU, true);
							menuId = event.getPointerId(i);
						} else if (event.getY(i) > gameView.getHeight() / 2) {
							game.getInputHandler().keyEvent(InputHandler.ATTACK, true);
							attackId = event.getPointerId(i);
						}
					}
				}
				if (match == false) {
					cursorPressed = false;
					cursorId = INVALID_POINTER_ID;
					cursorX = cursorY = -1.f;
				}
			}

			if (actionCode == MotionEvent.ACTION_UP || actionCode == MotionEvent.ACTION_POINTER_UP || actionCode == MotionEvent.ACTION_CANCEL) {
				if (action >> MotionEvent.ACTION_POINTER_ID_SHIFT == cursorId) {
					cursorPressed = false;
					cursorId = INVALID_POINTER_ID;
					cursorX = cursorY = -1.f;

					game.getInputHandler().keyEvent(InputHandler.UP, false);
					game.getInputHandler().keyEvent(InputHandler.DOWN, false);
					game.getInputHandler().keyEvent(InputHandler.RIGHT, false);
					game.getInputHandler().keyEvent(InputHandler.LEFT, false);
				} else if (action >> MotionEvent.ACTION_POINTER_ID_SHIFT == attackId) {
					game.getInputHandler().keyEvent(InputHandler.ATTACK, false);
				} else if (action >> MotionEvent.ACTION_POINTER_ID_SHIFT == menuId) {
					game.getInputHandler().keyEvent(InputHandler.MENU, false);
				}
			}

			// + cursorId + ", position is V(" + event.getX(cursorId) + " | " +
			// event.getY(cursorId) + ")") : "released"));
			if (cursorId != INVALID_POINTER_ID && actionCode == MotionEvent.ACTION_MOVE) {
				cursorX = event.getX(cursorId);
				cursorY = event.getY(cursorId);

				float angle = 0.0f;
				angle = (float) Math.atan2(cursorY - gameView.getHeight() / 2, cursorX - (game.settings.controlshflipped ? gameView.getWidth() - gameView.getWidth() / 5 : gameView.getWidth() / 5));
				angle = (float) Math.toDegrees(angle);
				if (angle < 0)
					angle += 360.0f;

				if (range(202.5f, angle, 337.5f)) {
					if (!game.getInputHandler().isDown(InputHandler.UP))
						game.getInputHandler().keyEvent(InputHandler.UP, true);
				} else {
					game.getInputHandler().keyEvent(InputHandler.UP, false);
				}

				if (range(157.5f, angle, 22.5)) {
					if (!game.getInputHandler().isDown(InputHandler.DOWN))
						game.getInputHandler().keyEvent(InputHandler.DOWN, true);
				} else {

					game.getInputHandler().keyEvent(InputHandler.DOWN, false);
				}

				if (range(112.5f, angle, 247.5f)) {
					if (!game.getInputHandler().isDown(InputHandler.LEFT))
						game.getInputHandler().keyEvent(InputHandler.LEFT, true);
				} else {
					game.getInputHandler().keyEvent(InputHandler.LEFT, false);
				}

				if ((range(292.5f, angle, 360) || range(0, angle, 67.5f))) {
					if (!game.getInputHandler().isDown(InputHandler.RIGHT))
						game.getInputHandler().keyEvent(InputHandler.RIGHT, true);
				} else {
					game.getInputHandler().keyEvent(InputHandler.RIGHT, false);
				}

			}
		}
		return true;
	}

	/** Show an event in the LogCat view, for debugging */
	@SuppressWarnings("unused")
	private void dumpEvent(MotionEvent event) {
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}
		sb.append("]");
	}

	private boolean range(double a, double x, double b) {
		if ((a < x && x < b) || (a > x && x > b))
			return true;
		return false;
	}
}
