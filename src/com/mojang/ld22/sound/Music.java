package com.mojang.ld22.sound;

import com.mojang.ld22.GameActivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class Music {
	public static Music sadness_and_sorrow 		;//= new Music(R.raw.sadness_and_sorrow_151445);
	public static Music temple_in_the_storm	 	;//= new Music(R.raw.temple_in_the_storm_165200);
	public static Music dark_skies 				;//= new Music(R.raw.newgrounds_darksk_70107);
	public static Music carnivorus_carnival 	;//= new Music(R.raw.carnivorus_carnival_381218);

	MediaPlayer mMediaPlayer;

	public Music(int resourceId) {
		mMediaPlayer = MediaPlayer.create(GameActivity.singleton, resourceId);
	}

	public void play() {
		if (mMediaPlayer.isPlaying() == false)
			mMediaPlayer.start();
	}

	public void play(float volmod) {
		play(volmod, false);
	}

	public void play(final float volmod, final boolean looping) {
		AudioManager mgr = (AudioManager) GameActivity.singleton.getSystemService(Context.AUDIO_SERVICE);
		float streamVolCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamVolMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float vol = streamVolCurrent / streamVolMax;
		vol *= volmod;
		mMediaPlayer.setVolume(vol, vol);
		mMediaPlayer.setLooping(looping);
		mMediaPlayer.start();
	}

	public void stop() {
		mMediaPlayer.stop();
	}
}
