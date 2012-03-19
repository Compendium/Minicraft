package com.mojang.ld22.sound;

import com.mojang.ld22.GameActivity;
import com.mojang.ld22.R;

import android.media.MediaPlayer;

public class Sound {
	public static final Sound playerHurt = new Sound(R.raw.playerhurt);
	public static final Sound playerDeath = new Sound(R.raw.death);
	public static final Sound monsterHurt = new Sound(R.raw.monsterhurt);
	public static final Sound tick = new Sound(R.raw.test);
	public static final Sound pickup = new Sound(R.raw.pickup);
	public static final Sound bossdeath = new Sound(R.raw.bossdeath);
	public static final Sound craft = new Sound(R.raw.craft);
	public static final Sound hit1 = new Sound(R.raw.hit1);
	
	private MediaPlayer mediaPlayer;

	private Sound(int ressourceID) {
		try {
			mediaPlayer = MediaPlayer.create(GameActivity.singleton, ressourceID);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void play() {
		try {
			new Thread() {
				public void run() {
					mediaPlayer.start();
				}
			}.start();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}