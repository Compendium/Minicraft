package com.mojang.ld22;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.text.format.Time;
import android.util.Log;

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
	public static final String NAME = "Minicraft";
	private static final int HEIGHT = 120;
	private static int WIDTH = 160;
	private static int SCALE;

	// private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels;
	private boolean running = false;
	private Screen screen;
	private Screen lightScreen;
	private InputHandler input = new InputHandler(this);

	private int[] colors = new int[256];
	private int tickCount = 0;
	public int gameTime = 0;

	private Level level;
	private Level[] levels = new Level[5];
	private int currentLevel = 3;
	public Player player;

	public Menu menu;
	private int playerDeadTime;
	private int pendingLevelChange;
	private int wonTimer = 0;
	public boolean hasWon = false;

	public static int getWidth() {
		return WIDTH;
	}

	public static int getHeight() {
		return HEIGHT;
	}

	public Game(final int displayWidth, final int displayHeigth) {
		final int scaleX = displayWidth / WIDTH;
		final int scaleY = displayHeigth / HEIGHT;
		if (scaleX < scaleY) {
			SCALE = scaleX;
		} else {
			SCALE = scaleY;
		}
		WIDTH = displayWidth / SCALE;

		pixels = new int[WIDTH * HEIGHT * SCALE];
		GameView.refreshCanvasSize();
	}

	private Paint blackPaint;

	public void setMenu(Menu menu) {
		this.menu = menu;
		if (menu != null)
			menu.init(this, input);
	}

	public void start() {
		running = true;
		// new Thread(this).start();
		// this is already in a thread
		// run(activity);
	}

	public void stop() {
		running = false;
	}

	public void resetGame() {
		playerDeadTime = 0;
		wonTimer = 0;
		gameTime = 0;
		hasWon = false;

		levels = new Level[5];
		currentLevel = 3;
		
		//TODO add loading screen

		
		screen.clear(999);
		String msg = "Now loading...";
		int col = Color.get(0, 111, 111, 111);
		Font.draw(msg, screen, 0, 0, col);
		Log.i("Loading Level", "Loading Level 1, Stage 1");
		for (int y = 0; y < screen.h; y++) {
			for (int x = 0; x < screen.w; x++) {
				int cc = screen.pixels[x + y * screen.w];
				if (cc < 255)
					pixels[x + y * WIDTH] = colors[cc];
			}
		}

		// TODO add proper user interface
		GameView.gameCanvas.drawBitmap(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT, false, null);
		
		
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

		blackPaint = new Paint();
		blackPaint.setARGB(255, 0, 0, 0);

		int pp = 0;
		for (int r = 0; r < 6; r++) {
			for (int g = 0; g < 6; g++) {
				for (int b = 0; b < 6; b++) {
					int rr = (r * 255 / 5);
					int gg = (g * 255 / 5);
					int bb = (b * 255 / 5);
					int mid = (rr * 30 + gg * 59 + bb * 11) / 100;

					int r1 = ((rr + mid * 1) / 2) * 230 / 255 + 10;
					int g1 = ((gg + mid * 1) / 2) * 230 / 255 + 10;
					int b1 = ((bb + mid * 1) / 2) * 230 / 255 + 10;
					colors[pp++] = r1 << 16 | g1 << 8 | b1;

				}
			}
		}
		try {
			screen = new Screen(WIDTH, HEIGHT, new SpriteSheet(ImageIO.read(activity.getResources().openRawResource(R.raw.icons))));
			lightScreen = new Screen(WIDTH, HEIGHT, new SpriteSheet(ImageIO.read(activity.getResources().openRawResource(R.raw.icons))));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// resetGame();
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
			}
		}
	}

	public void tick() {
		tickCount++;
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
		if (menu == null) {
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
		} else {
			menu.render(screen);
		}

		for (int y = 0; y < screen.h; y++) {
			for (int x = 0; x < screen.w; x++) {
				int cc = screen.pixels[x + y * screen.w];
				if (cc < 255)
					pixels[x + y * WIDTH] = colors[cc];
			}
		}

		// TODO add proper user interface
		canvas.drawBitmap(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT, false, null);
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
		
		//render time in bottom right
		String time = "";
		Time t = new Time();
		t.setToNow();
		time = t.hour + ":" + t.minute;
		Font.draw(time, screen, screen.w - (time.length() * 8), screen.h - 8, Color.get(0, 111, 111, 111));
	}

	public void scheduleLevelChange(int dir) {
		pendingLevelChange = dir;
	}

	// NOTE: main moved to GameActivity

	public void won() {
		wonTimer = 60 * 3;
		hasWon = true;
	}

	public InputHandler getInputHandler() {
		return input;
	}
}