package com.mojang.ld22.sound;

import com.mojang.ld22.GameActivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class Music {
	/**
	 * Ambient overworld music
	 */
	transient public static Music temple_in_the_storm	 	;//= new Music(R.raw.temple_in_the_storm_165200);
	transient public static Music carnivorus_carnival 	;//= new Music(R.raw.carnivorus_carnival_381218);
	transient public static Music heartbeat				;
	transient public static Music sad_song 				;
	transient public static Music vibe_timid_girl			;
	
	/**
	 * Epic, bossbattle theme
	 */
	transient public static Music dark_skies 				;//= new Music(R.raw.newgrounds_darksk_70107);
	/**
	 * Fearsome theme
	 */
	transient public static Music knock_knock				;
	
	transient public static boolean musicPlaying = false;
	transient public static Music currentlyPlaying = null;

	MediaPlayer mMediaPlayer;

	public Music(int resourceId) {
		mMediaPlayer = MediaPlayer.create(GameActivity.singleton, resourceId);
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				musicPlaying = false;
			}
		});
	}

	public void play() {
		play(1.f, false);
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
		
		currentlyPlaying = this;
		musicPlaying = true;
	}

	public void stop() {
		mMediaPlayer.stop();
		currentlyPlaying = null;
	}

	public boolean isPlaying() {
		return mMediaPlayer.isPlaying();
	}
}
