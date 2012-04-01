package com.mojang.ld22.sound;

import oz.wizards.minicraft.R;

import com.mojang.ld22.GameActivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class Ambient {
	transient public static Ambient forest1; // = new Ambient(R.raw.ambient_dobroide__20060824_forest03_22384);
	transient public static Ambient forest2; // = new Ambient(R.raw.ambient_reinsamba__morning_in_the_forest_2007_0415_33827);

	transient public static Ambient cave1; // = new Ambient(R.raw.ambient_erh__the_cave_ahr_o2_b2_109_4096);
	transient public static Ambient cave2; // = new Ambient(R.raw.ambient_plagasul__rarexport_05_4096);
	transient public static Ambient cave3; // = new Ambient(R.raw.ambient_roscoetoon__water_drip_echo2_27151);

	transient public static boolean isPlaying = false;
	transient public static Ambient currentlyPlaying = null;

	MediaPlayer mMediaPlayer;

	public Ambient(int resourceId) {
		mMediaPlayer = MediaPlayer.create(GameActivity.singleton, resourceId);
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				isPlaying = false;
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
		isPlaying = true;
	}

	public void stop() {
		mMediaPlayer.stop();
		currentlyPlaying = null;
		isPlaying = false;
	}

	public boolean isPlaying() {
		return mMediaPlayer.isPlaying();
	}
}
