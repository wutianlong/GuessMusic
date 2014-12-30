package com.guessmusic.tools;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

/**
 * ������Ч����
 */
public class MyPlayer {

	// ��Ч�����±�
	public final static int INDEX_TONE_ENTER = 0;
	public final static int INDEX_TONE_CANCEL = 1;
	public final static int INDEX_TONE_COIN = 2;
	// ��Ч���ļ���
	private final static String[] TONE_NAMES = { "enter.mp3", "cancel.mp3",
			"coin.mp3" };

	// ��������
	private static MediaPlayer mMusicMediaPlayer;
	// ��Ч
	private static MediaPlayer[] mToneMediaPlayer = new MediaPlayer[TONE_NAMES.length];

	/**
	 * ���Ÿ���
	 * 
	 * @param context
	 * @param fileName
	 */
	public static void playSong(Context context, String fileName) {
		if (mMusicMediaPlayer == null) {
			mMusicMediaPlayer = new MediaPlayer();
		}

		// ǿ������
		mMusicMediaPlayer.reset();

		// ���������ļ�
		AssetManager assetManager = context.getAssets();
		try {
			AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
			mMusicMediaPlayer
					.setDataSource(fileDescriptor.getFileDescriptor(),
							fileDescriptor.getStartOffset(),
							fileDescriptor.getLength());

			mMusicMediaPlayer.prepare();
			mMusicMediaPlayer.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void stopSong(Context context) {
		if (mMusicMediaPlayer != null) {
			mMusicMediaPlayer.stop();
		}
	}
 
	public static void playTone(Context context, int index) {
		if (mToneMediaPlayer[index] == null) {
			mToneMediaPlayer[index] = new MediaPlayer();
			// ���������ļ�
			AssetManager assetManager = context.getAssets();
			try {
				AssetFileDescriptor fileDescriptor = assetManager
						.openFd(TONE_NAMES[index]);
				mToneMediaPlayer[index].setDataSource(
						fileDescriptor.getFileDescriptor(),
						fileDescriptor.getStartOffset(),
						fileDescriptor.getLength());

				mToneMediaPlayer[index].prepare();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		mToneMediaPlayer[index].start();
	}

}
