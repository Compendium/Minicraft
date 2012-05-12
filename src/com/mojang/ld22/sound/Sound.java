package com.mojang.ld22.sound;

import com.mojang.ld22.GameActivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class Sound {
	public static Sound tick 		;//= new Sound(R.raw.test);
	public static Sound playerHurt 	;//= new Sound(R.raw.playerhurt);
	public static Sound playerDeath ;//= new Sound(R.raw.death);
	public static Sound monsterHurt ;//= new Sound(R.raw.monsterhurt);
	public static Sound pickup 		;//= new Sound(R.raw.pickup);
	public static Sound bossdeath 	;//= new Sound(R.raw.bossdeath);
	public static Sound craft 		;//= new Sound(R.raw.craft);
	public static Sound hit 		;//= new Sound(R.raw.hit1);

	private int sid;
	private boolean complete = false;

	transient private static SoundPool soundpool;

	public Sound(int ressourceID) {
		try {
			if (soundpool == null) {
				soundpool = new SoundPool(8, AudioManager.STREAM_MUSIC, 100);
			}

			sid = soundpool.load(GameActivity.singleton, ressourceID, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play() {
		play(0.1f, false);
	}
	
	public void play (float volmod)
	{
		play(volmod, false);
	}

	public void play(float volmod, boolean loop) {
		try {
			AudioManager mgr = (AudioManager) GameActivity.singleton.getSystemService(Context.AUDIO_SERVICE);
			float streamVolCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
			float streamVolMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			float vol = streamVolCurrent / streamVolMax;
			vol *= (Music.currentlyPlaying == null ? volmod : volmod*0.5f);

			// soundpool.play(soundpoolmap.get(id), vol, vol, 1, 0, 1.f);
			soundpool.play(sid, vol, vol, 1, (loop ? -1 : 0), 1.f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isReady() {
		return complete;
	}
}