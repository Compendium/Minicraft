package com.mojang.ld22.sound;

import com.mojang.ld22.GameActivity;
import com.mojang.ld22.Settings;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public class Music {
	/**
	 * Ambient overworld music
	 */
	transient public static Music temple_in_the_storm;// = new
														// Music(R.raw.temple_in_the_storm_165200);
	transient public static Music vibe_timid_girl;

	/**
	 * Epic, bossbattle theme
	 */
	transient public static Music dark_skies;// = new
												// Music(R.raw.newgrounds_darksk_70107);

	transient public static boolean musicPlaying = false;
	transient public static Music currentlyPlaying = null;

	MediaPlayer mMediaPlayer;
	float volume = 1.0f;

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
		if (GameActivity.singleton.game.settings.musicEnabled) {
			volume = volmod;
			AudioManager mgr = (AudioManager) GameActivity.singleton
					.getSystemService(Context.AUDIO_SERVICE);
			float streamVolCurrent = mgr
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			float streamVolMax = mgr
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			float vol = streamVolCurrent / streamVolMax;
			vol *= volmod;
			mMediaPlayer.setVolume(vol, vol);
			mMediaPlayer.setLooping(looping);
			mMediaPlayer.start();

			currentlyPlaying = this;
			musicPlaying = true;
		}
	}

	public void stop() {
		mMediaPlayer.stop();
		currentlyPlaying = null;
	}

	public boolean isPlaying() {
		return mMediaPlayer.isPlaying();
	}

	public void fadeOut(final float fact) {
		new Thread(new Runnable() {
			public void run() {
				while (volume >= 0.01f) {
					AudioManager mgr = (AudioManager) GameActivity.singleton
							.getSystemService(Context.AUDIO_SERVICE);
					float streamVolCurrent = mgr
							.getStreamVolume(AudioManager.STREAM_MUSIC);
					float streamVolMax = mgr
							.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
					float vol = streamVolCurrent / streamVolMax;
					vol *= volume;
					mMediaPlayer.setVolume(vol, vol);
					volume -= fact;
					Log.w("DEBUG", "" + volume);

					try {
						Thread.sleep(150);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (currentlyPlaying != null) {
					currentlyPlaying.stop();
					currentlyPlaying = null;
				}
			}
		}).start();
	}
}
