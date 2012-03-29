package com.mojang.ld22.sound;

import com.mojang.ld22.GameActivity;

import oz.wizards.minicraft.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

public class Sound {
	public static final Sound playerHurt = new Sound(R.raw.playerhurt);
	public static final Sound playerDeath = new Sound(R.raw.death);
	public static final Sound monsterHurt = new Sound(R.raw.monsterhurt);
	public static final Sound tick = new Sound(R.raw.test);
	public static final Sound pickup = new Sound(R.raw.pickup);
	public static final Sound bossdeath = new Sound(R.raw.bossdeath);
	public static final Sound craft = new Sound(R.raw.craft);
	public static final Sound hit1 = new Sound(R.raw.hit1);
	
	private int id;
	
	transient private SoundPool soundpool = new SoundPool(8, AudioManager.STREAM_MUSIC, 100);
	transient private SparseIntArray soundpoolmap = new SparseIntArray();

	private Sound(int ressourceID) {
		try
		{
		id = ressourceID;
		soundpoolmap.put(ressourceID, soundpool.load(GameActivity.singleton, ressourceID, 1));
		play(0.0f);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void play ()
	{
		play(1.0f);
	}

	public void play(float volmod) {
		try
		{
		AudioManager mgr = (AudioManager)GameActivity.singleton.getSystemService(Context.AUDIO_SERVICE);
		float streamVolCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamVolMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float vol = streamVolCurrent / streamVolMax;
		vol *= volmod;
		
		soundpool.play(soundpoolmap.get(id), vol, vol, 1, 0, 1.f);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}