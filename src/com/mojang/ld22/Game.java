package com.mojang.ld22;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import oz.wizards.minicraft.R;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;
import android.content.Intent;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
//import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.gfx.SpriteSheet;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;
import com.mojang.ld22.misc.ImageIO;
import com.mojang.ld22.screen.DeadMenu;
import com.mojang.ld22.screen.LevelTransitionMenu;
import com.mojang.ld22.screen.Menu;
import com.mojang.ld22.screen.TitleMenu;
import com.mojang.ld22.screen.WonMenu;

public class Game {
	public Context ctxt;
	public static final String NAME = "Minicraft";
	private static final int HEIGHT = 120;
	private static int WIDTH = 160;
	private static int SCALE;

	private boolean running = false;
	private Screen screen;
	private Screen lightScreen;
	private InputHandler input = new InputHandler(this);

	public int gameTime = 0;

	private Level level;
	private Level[] levels = new Level[5];
	private int currentLevel = 3;
	public Player player;

	public Menu menu = null;
	private int playerDeadTime;
	private int pendingLevelChange;
	private int wonTimer = 0;
	public boolean hasWon = false;

	public boolean mExternalStorageAvailable = false;
	public boolean mExternalStorageWriteable = false;
	String mExtStorageState;

	String status = "";
	Time t = new Time();
	int battery = 0;

	public static int getWidth() {
		return WIDTH;
	}

	public static int getHeight() {
		return HEIGHT;
	}

	public Game(final int displayWidth, final int displayHeigth) {
		this.ctxt = GameActivity.singleton;
		final int scaleX = displayWidth / WIDTH;
		final int scaleY = displayHeigth / HEIGHT;
		if (scaleX < scaleY) {
			SCALE = scaleX;
		} else {
			SCALE = scaleY;
		}
		WIDTH = displayWidth / SCALE;

		GameView.refreshCanvasSize();

		mExtStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(mExtStorageState)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(mExtStorageState)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}

	}

	// private Paint blackPaint;

	public void setMenu(Menu menu) {
		this.menu = menu;
		if (menu != null)
			menu.init(this, input);
	}

	public void start() {
		running = true;
	}

	public void stop() {
		running = false;
	}

	public void resetGame() {
		playerDeadTime = 0;
		wonTimer = 0;
		gameTime = 0;
		hasWon = false;

		currentLevel = 3;

		Log.i("Loading Level", "Loading Level 1, Stage 1");

		levels[4] = new Level(128, 128, 1, null);
		Log.i("Loading Level", "Loading Level 1, Stage 2");
		levels[3] = new Level(128, 128, 0, levels[4]);
		Log.i("Loading Level", "Loading Level 1, Stage 3");
		levels[2] = new Level(128, 128, -1, levels[3]);
		Log.i("Loading Level", "Loading Level 1, Stage 4");
		levels[1] = new Level(128, 128, -2, levels[2]);
		Log.i("Loading Level", "Loading Level 1, Stage 5");
		levels[0] = new Level(128, 128, -3, levels[1]);
		level = levels[currentLevel];
		player = new Player(this, input);
		player.findStartPos(level);

		level.add(player);

		for (int i = 0; i < 5; i++) {
			levels[i].trySpawn(5000);
		}
	}

	private void init(Context activity) {
		try {
			screen = new Screen(WIDTH, HEIGHT, new SpriteSheet(ImageIO.read(activity.getResources().openRawResource(R.raw.icons))));
			lightScreen = new Screen(WIDTH, HEIGHT, new SpriteSheet(ImageIO.read(activity.getResources().openRawResource(R.raw.icons))));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (menu == null)
			setMenu(new TitleMenu());
	}

	private long lastTime;
	private double unprocessed;
	private double nsPerTick;
	private int frames;
	private long lastTimer1;

	public void startRun(Context activity) {
		lastTime = System.nanoTime();
		unprocessed = 0;
		nsPerTick = 1000000000.0 / 60;
		frames = 0;
		lastTimer1 = System.currentTimeMillis();

		init(activity);
		start();
	}

	public void iterate(android.graphics.Canvas canvas) {
		if (running) {
			long now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = true;
			while (unprocessed >= 1) {
				tick();
				unprocessed -= 1;
				shouldRender = true;
			}

			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (shouldRender) {
				frames++;
				render(canvas);
			}

			if (System.currentTimeMillis() - lastTimer1 > 1000) {
				lastTimer1 += 1000;
				Log.v(Game.NAME, frames + " fps");
				frames = 0;

				t.setToNow();

				Intent bat = GameActivity.singleton.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
				int level = bat.getIntExtra("level", 0);
				int scale = bat.getIntExtra("scale", 100);
				int battery = level * 100 / scale;

				status = battery + "% " + t.hour + ":" + t.minute;
			}
		}
	}

	public void tick() {
		if (player != null && !player.removed && !hasWon)
			gameTime++;

		input.tick();
		if (menu != null) {
			menu.tick();
		} else {
			if (player.removed) {
				playerDeadTime++;
				if (playerDeadTime > 60) {
					setMenu(new DeadMenu());
				}
			} else {
				if (pendingLevelChange != 0) {
					setMenu(new LevelTransitionMenu(pendingLevelChange));
					pendingLevelChange = 0;
				}
			}
			if (wonTimer > 0) {
				if (--wonTimer == 0) {
					setMenu(new WonMenu());
				}
			}
			level.tick();
			Tile.tickCount++;
		}
	}

	public void changeLevel(int dir) {
		level.remove(player);
		currentLevel += dir;
		level = levels[currentLevel];
		player.x = (player.x >> 4) * 16 + 8;
		player.y = (player.y >> 4) * 16 + 8;
		level.add(player);

	}

	public void render(android.graphics.Canvas canvas) {
		// player and level are null when in the main menu
		if (player != null && level != null) {
			int xScroll = player.x - screen.w / 2;
			int yScroll = player.y - (screen.h - 8) / 2;
			if (xScroll < 16)
				xScroll = 16;
			if (yScroll < 16)
				yScroll = 16;
			if (xScroll > level.w * 16 - screen.w - 16)
				xScroll = level.w * 16 - screen.w - 16;
			if (yScroll > level.h * 16 - screen.h - 16)
				yScroll = level.h * 16 - screen.h - 16;
			if (currentLevel > 3) {
				int col = Color.get(20, 20, 121, 121);
				for (int y = 0; y < 14; y++)
					for (int x = 0; x < 24; x++) {
						screen.render(x * 8 - ((xScroll / 4) & 7), y * 8 - ((yScroll / 4) & 7), 0, col, 0);
					}
			}

			level.renderBackground(screen, xScroll, yScroll);
			level.renderSprites(screen, xScroll, yScroll);

			if (currentLevel < 3) {
				lightScreen.clear(0);
				level.renderLight(lightScreen, xScroll, yScroll);
				screen.overlay(lightScreen, xScroll, yScroll);
			}
			renderGui();
		}

		if (menu != null) {
			menu.render(screen);
		}

		/*
		 * int cc = 0; for (int y = 0; y < screen.h; y++) { for (int x = 0; x < screen.w; x++) { cc = screen.pixels[x + y * screen.w]; if (cc < 255) pixels[x + y * WIDTH] = colors[cc]; } }
		 */

		// TODO add proper user interface
		canvas.drawBitmap(screen.pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT, false, null);
	}

	private void renderGui() {
		// crosshair for center of controls
		screen.render(((screen.w / 5)), (screen.h / 2), 32 - 1, Color.get(-1, 222, 333, 444), 0);

		// black bar on bottom
		for (int y = 0; y < 2; y++) {
			for (int x = 0; x < (screen.w / 8); x++) {
				screen.render(x * 8, screen.h - 16 + y * 8, 0, Color.get(000, 000, 000, 000), 0);
			}
		}

		for (int i = 0; i < 10; i++) {
			if (i < player.health)
				screen.render(i * 8, screen.h - 16, 0 + 12 * 32, Color.get(000, 200, 500, 533), 0);
			else
				screen.render(i * 8, screen.h - 16, 0 + 12 * 32, Color.get(000, 100, 000, 000), 0);

			if (player.staminaRechargeDelay > 0) {
				if (player.staminaRechargeDelay / 4 % 2 == 0)
					screen.render(i * 8, screen.h - 8, 1 + 12 * 32, Color.get(000, 555, 000, 000), 0);
				else
					screen.render(i * 8, screen.h - 8, 1 + 12 * 32, Color.get(000, 110, 000, 000), 0);
			} else {
				if (i < player.stamina)
					screen.render(i * 8, screen.h - 8, 1 + 12 * 32, Color.get(000, 220, 550, 553), 0);
				else
					screen.render(i * 8, screen.h - 8, 1 + 12 * 32, Color.get(000, 110, 000, 000), 0);
			}
		}
		if (player.activeItem != null) {
			player.activeItem.renderInventory(screen, 10 * 8, screen.h - 16);
		}

		// render status info in bottom right
		Font.draw(status, screen, screen.w - (status.length() * 8), screen.h - 8, Color.get(0, 111, 111, 111));
	}

	public void scheduleLevelChange(int dir) {
		pendingLevelChange = dir;
	}

	public void won() {
		wonTimer = 60 * 3;
		hasWon = true;
	}

	public InputHandler getInputHandler() {
		return input;
	}

	public void save() {
		if (player == null || level == null)
			return;

		if (mExternalStorageWriteable) {
			try {
				File file = new File(ctxt.getExternalFilesDir(null), "save.obj");
				Log.w("DEBUG", file.getPath());
				FileOutputStream fos = new FileOutputStream(file, false);
				ObjectOutputStream os = new ObjectOutputStream(fos);

				// os.writeInt(playerDeadTime);
				// Log.w("DEBUG", "saved state");
				// os.writeInt(wonTimer);
				// Log.w("DEBUG", "1");
				// os.writeInt(gameTime);
				// Log.w("DEBUG", "2");
				// os.writeBoolean(hasWon);
				// Log.w("DEBUG", "3");
				os.writeInt(currentLevel);
				Log.w("DEBUG", "4");

				os.writeObject(player);

				long starTime = System.nanoTime();
				for (int i = 0; i < levels.length; i++) {
					Log.w("DEBUG", "Saving level " + i);
					os.writeObject(levels[i]);
				}
				long finishTime = System.nanoTime() - starTime;
				Log.w("DEBUG", "Wrote levels, took " + ((float) finishTime / (float) 1000000000) + " seconds");

				os.flush();
				os.close();
				Log.w("DEBUG", "saved state");

				Context context = ctxt.getApplicationContext();
				CharSequence text = "Game saved!";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();

			} catch (IOException e) {
				e.printStackTrace();
				Log.e("ExternalStorage", "Error saving save-state");

				Context context = ctxt.getApplicationContext();
				CharSequence text = "Couldn't access file system. Saving failed";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
		}
	}

	public void load() throws ClassNotFoundException, StreamCorruptedException, IOException {
		if (mExternalStorageAvailable) {
			File file = new File(ctxt.getExternalFilesDir(null), "save.obj");
			if (!file.exists())
				throw new IOException("Savegame doesn't exist");
			Log.w("DEBUG", file.getPath());
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream is = new ObjectInputStream(fis);
			// playerDeadTime = is.readInt();
			// wonTimer = is.readInt();
			// gameTime = is.readInt();
			// hasWon = is.readBoolean();
			playerDeadTime = 0;
			wonTimer = 0;
			gameTime = 0;
			hasWon = false;

			currentLevel = is.readInt();

			player = (Player) is.readObject();
			player.game = this;
			player.input = input;
			player.stamina = player.maxStamina;
			player.staminaRecharge = 0;
			player.staminaRechargeDelay = 40;

			long starTime = System.nanoTime();
			for (int i = 0; i < levels.length; i++) {
				Log.i("Loading Level", "Loading Level " + i);
				levels[i] = (Level) is.readObject();
				levels[i].reset();
			}
			long finishTime = System.nanoTime() - starTime;
			Log.w("DEBUG", "Loaded levels, took " + ((float) finishTime / (float) 1000000000) + " seconds");
			level = levels[currentLevel];
			level.player = player;

			is.close();
		} else
			throw new IOException("Cannot access file system");
	}
}