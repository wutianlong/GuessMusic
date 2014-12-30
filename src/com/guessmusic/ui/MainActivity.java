package com.guessmusic.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import com.guessmusic.R;
import com.guessmusic.data.Const;
import com.guessmusic.model.GetSong;
import com.guessmusic.model.WordButton;
import com.guessmusic.model.iDialogButtonListener;
import com.guessmusic.model.iWordButtonClickListener;
import com.guessmusic.myui.MyGridView;
import com.guessmusic.tools.FileOperate;
import com.guessmusic.tools.MyPlayer;
import com.guessmusic.tools.Tools;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//GBK coding ����Windows->Preferences, Ȼ��ѡ��General�����Workspace. Text file encodingѡ��Other GBK�� ���û��GBK��ѡ� û��ϵ�� ֱ������GBK������ĸ�� Apply�� GBK��������ģ� �Ѿ�����������
public class MainActivity extends Activity implements iWordButtonClickListener {
	// ��ѡ��������
	public final static int COUNTS_WORDS = 24;

	// ����ȷ
	public final static int ANSWER_RIGHT = 1;
	// �𰸴���
	public final static int ANSWER_WRONG = 2;
	// �𰸲�����
	public final static int ANSWER_LACK = 3;

	public final static int ID_DIALOG_DELET_WORD = 1;
	public final static int ID_DIALOG_TIP_WORD = 2;
	public final static int ID_DIALOG_LACK_COINS_WORD = 3;

	// ������˸����
	public final static int SPARD_TIME = 6;

	// ��Ƭ�����ˣ���ʼ��ť����
	private Animation mDiscAnim, mDiscBarInAnim, mDiscBarOutAnim;
	private LinearInterpolator mDiscLin, mDiscBarInLin, mDiscBarOutLin;

	// ��Ƭ�����ˣ���ʼ��ť
	private ImageView mViewDisc, mViewDiscBar;
	private ImageButton mbtnGameStart;

	// ��ǰ�����Ƿ�����
	private boolean mIsRunning = false;

	// ��ѡ����ѡ���ֿ�����
	private ArrayList<WordButton> mAllWords, mSelWords;

	// ��ѡ���ֿ�ؼ�
	private MyGridView mMyGridView;

	// ��ѡ�����ֿ�Ĳ��ֿؼ�
	private LinearLayout mViewWordsContainer;

	// �ؿ�����
	private int mCurrentIndex;
	private TextView mTextPassIndex;
	private TextView mTextCurrentIndex;

	// ��ǰ�ؿ�����
	private GetSong mCurrentSong;
	private TextView mTextCurrentPassSongName;

	// ���ؽ���
	private LinearLayout mPassEvent;

	// ��ѡ������ʾ��ť����ѡ����ɾ����ť, ����ť
	private ImageButton mBtnTip, mBtnDelet;

	// ��ӵ�еĽ������
	private TextView mTextCurrentCoin;
	private int mCurrentCoins;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/**
		 * �ؼ���ʼ��
		 */
		// ��Ƭ�����ˣ���ʼ��ť
		mViewDisc = (ImageView) findViewById(R.id.view_disc);
		mViewDiscBar = (ImageView) findViewById(R.id.view_disc_bar);
		// ��Ϸ��ʼ��ť
		mbtnGameStart = (ImageButton) findViewById(R.id.btn_game_start);
		// ��ѡ���ֿ�ؼ�
		mMyGridView = (MyGridView) findViewById(R.id.gridview);
		// ��ѡ�����ֿ�Ĳ��ֿؼ�
		mViewWordsContainer = (LinearLayout) findViewById(R.id.layout_word_sel);
		// ��ѡ������ʾ��ť����ѡ����ɾ����ť, ����ť
		mBtnTip = (ImageButton) findViewById(R.id.btn_tip_word);
		mBtnDelet = (ImageButton) findViewById(R.id.btn_delet_word);

		/**
		 * ��ʼ������
		 */
		// ��Ƭ��ת
		mDiscAnim = AnimationUtils.loadAnimation(this, R.anim.disc_rotate);
		mDiscLin = new LinearInterpolator();
		mDiscAnim.setInterpolator(mDiscLin);
		mDiscAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mViewDiscBar.startAnimation(mDiscBarOutAnim); // ���˿�ʼ�˳�
			}
		});
		// ���˿�ʼ
		mDiscBarInAnim = AnimationUtils.loadAnimation(this,
				R.anim.disc_bar_in_rotate);
		mDiscBarInLin = new LinearInterpolator();
		mDiscBarInAnim.setInterpolator(mDiscBarInLin);
		mDiscBarInAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mViewDisc.startAnimation(mDiscAnim); // ��Ƭ��ʼת��
				// ��������
				MyPlayer.playSong(MainActivity.this,
						mCurrentSong.getSongFileName());
			}
		});
		// �����˳�
		mDiscBarOutAnim = AnimationUtils.loadAnimation(this,
				R.anim.disc_bar_out_rotate);
		mDiscBarOutLin = new LinearInterpolator();
		mDiscBarOutAnim.setInterpolator(mDiscBarOutLin);
		mDiscBarOutAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mbtnGameStart.setVisibility(View.VISIBLE); // ������������ʼ��ťȡ������
				mIsRunning = false;
			}
		});

		// ���ֲ��Ű�ť
		mbtnGameStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ���Ų��˽��붯��
				handlePlayButton();
			}
		});

		// ��ʼ����ǰ�ؿ���
		mCurrentIndex = FileOperate.dataLoad(MainActivity.this)[FileOperate.GAME_LEVEL];

		// ��ʾ��ǰ�ص�����
		mTextCurrentIndex = (TextView) findViewById(R.id.text_level);
		mTextCurrentIndex.setText((mCurrentIndex + 1) + "");

		// ��ʼ����ǰ�����
		mCurrentCoins = FileOperate.dataLoad(MainActivity.this)[FileOperate.GAME_COINS];

		// ��ʾ��ǰ�����
		mTextCurrentCoin = (TextView) findViewById(R.id.text_bar_coins);
		mTextCurrentCoin.setText(mCurrentCoins + "");

		// �������ֿ�ļ����¼�
		mMyGridView.registOnWordButtonClick(this);

		// ��ʼ�����ֿ�����
		initCurrentData();

		// ɾ��ĳ�����ֿ�
		handleDeletWord();

		// ��ʾһ����ȷ��
		handleTipWord();

	}

	@Override
	protected void onPause() {
		// �������ʱȡ����Ƭת������
		mViewDisc.clearAnimation();

		// ��ͣ����
		MyPlayer.stopSong(MainActivity.this);
		super.onPause();

		// ��������
		FileOperate.dataSave(MainActivity.this, mCurrentIndex, mCurrentCoins);
	}

	/**
	 * ������������
	 */
	private void handlePlayButton() {
		if (mViewDiscBar != null) {
			if (!mIsRunning) {
				// ���˶�����ʼ����ʼ��ť����
				mViewDiscBar.startAnimation(mDiscBarInAnim);
				mbtnGameStart.setVisibility(View.INVISIBLE);
				mIsRunning = true;
			}
		}
	}

	/**
	 * ��ʼ����ǰ�ؿ�����
	 */
	private void initCurrentData() {

		// ��ʼ����ѡ���ֿ�
		mSelWords = initSelectWord();
		// ������ѡ���ֿ��С
		LayoutParams params = new LayoutParams(120, 120);

		// ���ԭ���Ĵ�
		mViewWordsContainer.removeAllViews();

		// ��̬������ֿ�
		for (int i = 0; i < mSelWords.size(); i++) {
			mViewWordsContainer.addView(mSelWords.get(i).mViewButton, params);
		}

		// ��ʼ����ѡ���ֿ�
		mAllWords = initGetWord();

		// ����MyGridView����
		mMyGridView.updateData(mAllWords);

		// �Զ���������
		handlePlayButton();

	}

	/**
	 * ��ʼ����ѡ���ֿ�
	 */
	private ArrayList<WordButton> initSelectWord() {

		mCurrentSong = initCurrentSong(); // ��ȡ��ǰ������Ϣ

		ArrayList<WordButton> data = new ArrayList<WordButton>();

		for (int i = 0; i < mCurrentSong.getSongNameLenth(); i++) {
			View view = Tools.getView(MainActivity.this,
					R.layout.self_ui_gridview_item);

			final WordButton btnWord = new WordButton();
			btnWord.mViewButton = (Button) view
					.findViewById(R.id.btn_gridview_item);
			btnWord.mViewButton.setTextColor(Color.WHITE);
			btnWord.mViewButton.setText("");
			btnWord.mIsVisible = false;
			btnWord.mViewButton
					.setBackgroundResource(R.drawable.game_wordblank); // ����������ѡ���ֿ�ı���
			data.add(btnWord);
			btnWord.mViewButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					cleanSelectWord(btnWord);
					// ������Ч
					MyPlayer.playTone(MainActivity.this,
							MyPlayer.INDEX_TONE_CANCEL);
				}
			});
		}
		return data;
	}

	/**
	 * ��ʼ����ѡ���ֿ�
	 */
	private ArrayList<WordButton> initGetWord() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();

		// ��ʼ����ѡ��������
		String[] allWord = generateAllWord();

		// Ϊ��ѡ��ֵ��Ӧ����
		for (int i = 0; i < COUNTS_WORDS; i++) {
			WordButton btn = new WordButton();
			btn.mWordString = allWord[i];
			data.add(btn);
		}
		return data;
	}

	@Override
	/**
	 * �����ѡ���ֿ�����¼�
	 */
	public void onWordButtonClickListener(WordButton wordButton) {
		// ������ѡ���ֿ�����
		setSelectWord(wordButton);

		// ����
		int checkAnswer = checkAnswer();

		// ����ȷ
		if (checkAnswer == ANSWER_RIGHT) {
			for (int i = 0; i < mSelWords.size(); i++) {
				mSelWords.get(i).mViewButton.setTextColor(Color.WHITE);
			}
			handlePassEvent();
		}
		// �𰸴���
		else if (checkAnswer == ANSWER_WRONG) {
			sparkWord();
		}
		// �𰸲�����
		else if (checkAnswer == ANSWER_LACK) {
			for (int i = 0; i < mSelWords.size(); i++) {
				mSelWords.get(i).mViewButton.setTextColor(Color.WHITE);
			}
		}

	}

	/**
	 * ��ȡ��ǰ�ؿ�����
	 */
	private GetSong initCurrentSong() {
		GetSong song = new GetSong();

		// ��ȡ��ǰ�ؿ��ĸ������ֺ��ļ�����
		String temp[] = Const.SONG_INFO[mCurrentIndex];
		song.setSongFileName(temp[Const.INDEX_FILE_NAME]);
		song.setSongName(temp[Const.INDEX_SONG_NAME]);

		return song;
	}

	/**
	 * �����������
	 */
	private char getRandomWord() {
		String word = "";
		int hight, low;

		Random random = new Random();

		hight = (176 + Math.abs(random.nextInt(30)));
		low = (161 + Math.abs(random.nextInt(70)));

		byte[] wordByte = new byte[2];
		wordByte[0] = (Integer.valueOf(hight)).byteValue();
		wordByte[1] = (Integer.valueOf(low)).byteValue();
		try {
			word = new String(wordByte, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return word.charAt(0);
	}

	/**
	 * ���������������
	 */
	private String[] generateAllWord() {

		String allWord[] = new String[COUNTS_WORDS];
		Random random = new Random();
		// �����������
		for (int i = 0; i < mCurrentSong.getSongNameLenth(); i++) {
			allWord[i] = mCurrentSong.getSongNameChar()[i] + "";
		}
		// �����������
		for (int i = mCurrentSong.getSongNameLenth(); i < COUNTS_WORDS; i++) {
			allWord[i] = getRandomWord() + "";
		}
		// ��������˳��,����һ�����ͺ������������������������
		for (int i = COUNTS_WORDS - 1; i >= 0; i--) {
			int randomIndex = random.nextInt(i + 1);
			String temp;
			temp = allWord[randomIndex];
			allWord[randomIndex] = allWord[i];
			allWord[i] = temp;
		}
		return allWord;
	}

	/**
	 * ���ô�
	 */
	private void setSelectWord(WordButton wordButton) {
		for (int i = 0; i < mCurrentSong.getSongNameLenth(); i++) {
			// �����Ϊ�գ�����������
			if (mSelWords.get(i).mWordString.length() == 0) {
				mSelWords.get(i).mViewButton.setText(wordButton.mWordString);
				mSelWords.get(i).mWordString = wordButton.mWordString;

				// ��¼ѡ�����ֿ�����
				mSelWords.get(i).mIndex = wordButton.mIndex;
				// ���ô�ѡ���ֿ�ɼ���
				setAllWordVisible(wordButton);

				break;
			}
		}
	}

	/**
	 * ���ô�ѡ���ֿ�ɼ���
	 */
	private void setAllWordVisible(WordButton wordButton) {
		wordButton.mIsVisible = false;
		wordButton.mViewButton.setVisibility(View.INVISIBLE);
	}

	/**
	 * �����ѡ���֣����ڴ�ѡ���ֿ�����ʾ���������
	 */
	private void cleanSelectWord(WordButton wordButton) {
		wordButton.mWordString = "";
		wordButton.mViewButton.setText("");

		mAllWords.get(wordButton.mIndex).mViewButton
				.setVisibility(View.VISIBLE);
		mAllWords.get(wordButton.mIndex).mIsVisible = true;

	}

	/**
	 * ����
	 */
	private int checkAnswer() {
		// ��鳤��
		for (int i = 0; i < mSelWords.size(); i++) {
			if (mSelWords.get(i).mWordString.length() == 0) {
				return ANSWER_LACK;
			}
		}

		// ������
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < mSelWords.size(); i++) {
			strBuf.append(mSelWords.get(i).mWordString);
		}
		return (strBuf.toString().equals(mCurrentSong.getSongName())) ? ANSWER_RIGHT
				: ANSWER_WRONG;
	}

	/**
	 * ������˸
	 */
	private void sparkWord() {
		// ��ʱ��
		TimerTask task = new TimerTask() {
			boolean change = false;
			int spardTimes = 0;

			@Override
			public void run() {
				// �Խ���UI�ĸ���һ��Ҫ��UI�߳�
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (++spardTimes > SPARD_TIME) {
							return;
						}

						// ִ����˸
						for (int i = 0; i < mSelWords.size(); i++) {
							mSelWords.get(i).mViewButton
									.setTextColor(change ? Color.RED
											: Color.WHITE);
						}

						change = !change;
					}
				});
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 1, 150);
	}

	/**
	 * ��������¼�
	 */
	private void handlePassEvent() {
		mPassEvent = (LinearLayout) this.findViewById(R.id.layout_pass_event);
		mPassEvent.setVisibility(View.VISIBLE);

		// ֹͣδ��ɵĶ���
		mViewDisc.clearAnimation();

		// ��ͣ����
		MyPlayer.stopSong(MainActivity.this);

		// �����Ч
		MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_TONE_COIN);

		// �������
		mCurrentCoins += Const.PASS_AWARD_COINS;
		mTextCurrentCoin.setText(mCurrentCoins + "");

		// �ؿ�������1
		mTextCurrentIndex.setText((++mCurrentIndex + 1) + "");

		// ��ʾ���ػ���ؿ�����
		mTextPassIndex = (TextView) findViewById(R.id.text_pass_level);
		mTextPassIndex.setText((mCurrentIndex + 1) + "");

		// ��ʾ��ǰ�ؿ���������
		mTextCurrentPassSongName = (TextView) findViewById(R.id.text_pass_song_name);
		mTextCurrentPassSongName.setText(mCurrentSong.getSongName());

		// ��һ�ذ�������
		ImageButton btnPass = (ImageButton) findViewById(R.id.btn_pass_next);
		btnPass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ������Ч
				MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_TONE_ENTER);
				if (judegIsPassed()) {
					// ����ͨ�ؽ���
					Tools.startIntent(MainActivity.this, AllPassView.class);
				} else {
					// ��ʼ��һ��
					mPassEvent.setVisibility(View.INVISIBLE);

					// ������һ������
					initCurrentData();
				}
			}
		});

	}

	/**
	 * �ж��Ƿ�����һ��
	 */
	private boolean judegIsPassed() {
		return mCurrentIndex == Const.SONG_INFO.length;
	}

	/**
	 * �������ļ��л�ȡɾ������ʱ��ҵĿ۳�����
	 */
	private int getDeletWordCoin() {
		return this.getResources().getInteger(R.integer.pay_delete_word);
	}

	/**
	 * �������ļ��л�ȡ��ʾ����ʱ��ҵĿ۳�����
	 */
	private int getTipWordCoin() {
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}

	/**
	 * ���ӻ������ӵ�еĽ������
	 */
	private void handleCoins(int data) {

		mCurrentCoins += data;
		mTextCurrentCoin.setText(mCurrentCoins + "");

	}

	/**
	 * ɾ��һ����ѡ����
	 */
	private void deletOneWord() {

		if (mCurrentCoins - getDeletWordCoin() >= 0) {
			if (findNotAnswer() != null) {
				setAllWordVisible(findNotAnswer());

				// ���ٽ������
				handleCoins(-getDeletWordCoin());
			}
		} else {
			showConfirmDialog(ID_DIALOG_LACK_COINS_WORD);
		}
	}

	/**
	 * ���һ����ȷ����
	 */
	private void tipOneWord() {
		boolean tipWord = false;

		if (mCurrentCoins - getTipWordCoin() >= 0) {
			for (int i = 0; i < mSelWords.size(); i++) {
				// �����ѡ���ֿ�Ϊ�գ�������һ����ȷ��
				if (mSelWords.get(i).mWordString.length() == 0) {
					onWordButtonClickListener(findIsAnswer(i));
					tipWord = true;

					// ���ٽ������
					handleCoins(-getTipWordCoin());
					break;
				}
			}
		} else {
			showConfirmDialog(ID_DIALOG_LACK_COINS_WORD);
		}
		// ���û���ҵ��������𰸵����ֿ�
		if (!tipWord) {
			// ��˸������ʾ�û�
			sparkWord();
		}

	}

	/**
	 * �ҵ�һ�����Ǵ𰸵����ֿ�, �ҵ�ǰ���ֿ��ǿɼ���
	 */
	private WordButton findNotAnswer() {
		Random random = new Random();
		WordButton buf = null;
		int count = 0;
		while (true) {
			// �����ѡһ�����ֿ�
			int index = random.nextInt(COUNTS_WORDS);
			buf = mAllWords.get(index);

			if (buf.mIsVisible && !isTheAnswerWord(buf)) {
				return buf;
			}
			count++;
			if (count > 1000) {
				return null;
			}
		}
	}

	/**
	 * �ҵ�һ�����е�����,index��Ҫ�������ֿ������
	 */
	private WordButton findIsAnswer(int index) {
		WordButton buf = null;

		for (int i = 0; i < COUNTS_WORDS; i++) {
			buf = mAllWords.get(i);

			// ����һ����ȷ����
			if (buf.mWordString.equals(mCurrentSong.getSongNameChar()[index]
					+ "")) {
				return buf;
			}
		}

		return null;
	}

	/**
	 * �ж�ĳ�������Ƿ�Ϊ��
	 */
	private boolean isTheAnswerWord(WordButton word) {
		boolean result = false;

		for (int i = 0; i < mCurrentSong.getSongNameLenth(); i++) {
			if (word.mWordString.equals("" + mCurrentSong.getSongNameChar()[i])) {
				result = true;
				break;
			}
		}

		return result;
	}

	/**
	 * ɾ��ĳ�����ֿ��¼�
	 */
	private void handleDeletWord() {
		mBtnDelet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_TONE_ENTER);
				showConfirmDialog(ID_DIALOG_DELET_WORD);
			}
		});
	}

	/**
	 * ��ʾ�𰸵�һ�������¼�
	 */
	private void handleTipWord() {
		mBtnTip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_TONE_ENTER);
				showConfirmDialog(ID_DIALOG_TIP_WORD);
			}
		});
	}

	/**
	 * �Զ���AlertDialog�¼���Ӧ
	 */
	// ɾ�������
	private iDialogButtonListener mBtnConfirmDeletWordListener = new iDialogButtonListener() {

		@Override
		public void onClick() {
			deletOneWord();
		}
	};

	// ����ʾ
	private iDialogButtonListener mBtnConfirmTipWordListener = new iDialogButtonListener() {

		@Override
		public void onClick() {
			tipOneWord();
		}
	};

	// ��Ҳ���
	private iDialogButtonListener mBtnConfirmLackCoinsListener = new iDialogButtonListener() {

		@Override
		public void onClick() {

		}
	};

	/**
	 * ��ʾ�Ի���
	 */
	private void showConfirmDialog(int id) {
		switch (id) {
		case ID_DIALOG_DELET_WORD:
			Tools.showDialog(MainActivity.this, "ȷ�ϻ���" + getDeletWordCoin()
					+ "�����ȥ��һ�������", mBtnConfirmDeletWordListener);
			break;
		case ID_DIALOG_TIP_WORD:
			Tools.showDialog(MainActivity.this, "ȷ�ϻ���" + getTipWordCoin()
					+ "����һ��һ��������ʾ", mBtnConfirmTipWordListener);
			break;
		case ID_DIALOG_LACK_COINS_WORD:
			Tools.showDialog(MainActivity.this, "��Ҳ��㣬ȥ�̵겹�䣿",
					mBtnConfirmLackCoinsListener);
			break;
		default:
			break;
		}
	}
}
