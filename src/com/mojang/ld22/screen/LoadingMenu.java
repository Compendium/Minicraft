package com.mojang.ld22.screen;

import java.io.IOException;
import java.io.StreamCorruptedException;

import oz.wizards.minicraft.R;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.sound.Music;

public class LoadingMenu extends Menu {
	private Menu parent;
	public static final int NEWGAME = 0;
	public static final int LOADGAME = 1;
	public boolean once = true;
	private int intent = 0;
	public boolean success = false;
	private Thread loadingThread;
	private Thread musicLoadingThread;

	public LoadingMenu(Menu parent, int i) {
		this.parent = parent;
		intent = i;
	}

	public void tick() {
		if (once) {
			once = false;
			if (intent == 0) {
				loadingThread = new Thread(new Runnable() {
					public void run() {
						game.resetGame();
						success = true;
					}
				});
				// game.setMenu(null);
				loadingThread.start();
			} else if (intent == 1) {
				loadingThread = new Thread(new Runnable() {
					public void run() {
						try {
							game.load();
							success = true;
							// ((LoadingMenu)game.menu).success = true;
						} catch (StreamCorruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				loadingThread.start();
			}

			if (musicLoadingThread == null) {
				musicLoadingThread = new Thread(new Runnable() {

					@Override
					public void run() {
						Music.carnivorus_carnival = new Music(R.raw.music_carnivorus_carnival_381218);
						Music.heartbeat = new Music(R.raw.music_heartbeat_201982);
						Music.knock_knock = new Music(R.raw.music_knock__knock_399895);
						Music.dark_skies = new Music(R.raw.music_newgrounds_darksk_70107);
						Music.sad_song = new Music(R.raw.music_sad_song_374621);
						Music.temple_in_the_storm = new Music(R.raw.music_temple_in_the_storm165200);
						Music.vibe_timid_girl = new Music(R.raw.music_vibe_timid_girl_217741);
					}
				});
				musicLoadingThread.start();
			}
		}

		if (loadingThread.isAlive() == false) {
			if (success)
				game.setMenu(null);
			else
				game.setMenu(parent);
		}
	}

	public void render(Screen screen) {
		screen.clear(0);
		String msg1 = "Loading";
		String msg2 = "Please be patient ";

		Font.draw(msg1, screen, screen.w / 2 - (msg1.length() * 8 / 2), screen.h / 2 - 4, Color.get(0, 111, 111, 111));
		Font.draw(msg2, screen, screen.w / 2 - (msg2.length() * 8 / 2), screen.h / 2 - 4 + 8, Color.get(0, 111, 111, 111));
		screen.render((screen.w / 2 - (msg2.length() * 8 / 2)) + (msg2.length() * 8), screen.h / 2 - 4 + 8, 0 + 12 * 32, Color.get(000, 200, 500, 533), 0);

		float steps = (screen.w / 8) - 2;
		float stepsize = steps / 100.f;
		float maxstep = (float) game.percentage * stepsize;

		for (int i = 8; i < (screen.w - 8); i += 8) {
			int c;
			if ((i / 8) - 1 < maxstep)
				c = Color.get(0, 999, 999, 999);
			else
				c = Color.get(0, 111, 111, 111);

			Font.draw("|", screen, i, screen.h / 2 - 4 + 8 * 2, c);
		}
		String percentage = Integer.toString(game.percentage);
		Font.draw(percentage, screen, screen.w / 2 - (percentage.length() * 8 / 2), screen.h / 2 - 4 + 8 * 2, Color.get(-1, 555, 555, 555));
	}
}