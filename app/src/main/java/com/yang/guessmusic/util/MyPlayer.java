package com.yang.guessmusic.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

public class MyPlayer{
	private static MediaPlayer mMediaPlayer;
	private static final String[] SOUND_NAMES = { "cancel.mp3", "coin.mp3",
			"enter.mp3" };
	public static final int SOUND_CANCEL = 0;
	public static final int SOUND_COIN = 1;
	public static final int SOUND_ENTER = 2;
	private static MediaPlayer[] mSoundPlayer = new MediaPlayer[SOUND_NAMES.length];
	private static MyMusicCompleteListener mMusicCompleteListener;
	public interface MyMusicCompleteListener {
		void onMusicComplete();
	}
	public static void setOnMusicCompleteListener(MyMusicCompleteListener listener){
		mMusicCompleteListener = listener;
	}

	public static void playSong(Context context, String fileName) {
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
		}
		mMediaPlayer.reset();
		AssetManager assetManager = context.getAssets();
		try {
			AssetFileDescriptor descriptor = assetManager.openFd(fileName);
			mMediaPlayer.setDataSource(descriptor.getFileDescriptor(),
					descriptor.getStartOffset(), descriptor.getLength());
			mMediaPlayer.prepare();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					mMusicCompleteListener.onMusicComplete();
				}
			});
			mMediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void playSound(Context context, int soundType) {
		AssetManager assetManager = context.getAssets();
		if (mSoundPlayer[soundType] == null) {
			mSoundPlayer[soundType] = new MediaPlayer();

			try {
				AssetFileDescriptor descriptor = assetManager
						.openFd(SOUND_NAMES[soundType]);
				mSoundPlayer[soundType].setDataSource(
						descriptor.getFileDescriptor(),
						descriptor.getStartOffset(), descriptor.getLength());
				mSoundPlayer[soundType].prepare();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mSoundPlayer[soundType].start();
	}

	public static void stopPlay() {
		if (mMediaPlayer != null) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
		}
	}

	public static void releaseMedia() {
		if (mMediaPlayer != null) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}


}
