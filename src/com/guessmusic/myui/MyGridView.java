package com.guessmusic.myui;

import java.util.ArrayList;

import com.guessmusic.R;
import com.guessmusic.model.WordButton;
import com.guessmusic.model.iWordButtonClickListener;
import com.guessmusic.tools.MyPlayer;
import com.guessmusic.tools.Tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class MyGridView extends GridView {

	// ��������
	private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();

	private MyGridAdapter mAdapter;

	private Context mContext;

	private Animation mScaleAnim;

	private iWordButtonClickListener mWordButtonClickListener; // �����ӿ�

	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;

		// ��������Դ
		mAdapter = new MyGridAdapter();
		this.setAdapter(mAdapter);
	}

	public void updateData(ArrayList<WordButton> list) {
		mArrayList = list;

		// ������������Դ
		this.setAdapter(mAdapter);
	}

	class MyGridAdapter extends BaseAdapter {

		@Override
		// �����������е���������
		public int getCount() {
			return mArrayList.size();
		}

		@Override
		// ���ص�ǰѡ��Ķ���
		public Object getItem(int position) {
			return mArrayList.get(position);
		}

		@Override
		// ���ص�ǰѡ�������
		public long getItemId(int position) {
			return position;
		}

		@Override
		// ���Ƶ�ԪView
		public View getView(int position, View convertView, ViewGroup parent) {

			final WordButton btnWord; // ����ѡ��ťʵ����

			if (convertView == null) {
				convertView = Tools.getView(mContext,
						R.layout.self_ui_gridview_item);

				btnWord = mArrayList.get(position); // ��ȡ�����еĶ���

				// ���ض���
				mScaleAnim = AnimationUtils.loadAnimation(mContext,
						R.anim.word_scale);
				// ���ö����ӳ�ʱ��
				mScaleAnim.setStartOffset(position * 70);
				btnWord.mIndex = position;
				if (btnWord.mViewButton == null) {
					btnWord.mViewButton = (Button) convertView
							.findViewById(R.id.btn_gridview_item);
					// ���ð�ť�����¼�
					btnWord.mViewButton
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									// ������Ч
									MyPlayer.playTone(mContext,
											MyPlayer.INDEX_TONE_ENTER);
									mWordButtonClickListener
											.onWordButtonClickListener(btnWord);
								}
							});
				}

				convertView.setTag(btnWord);

			} else {
				btnWord = (WordButton) convertView.getTag();
			}

			btnWord.mViewButton.setText(btnWord.mWordString);

			// ��������
			convertView.startAnimation(mScaleAnim);

			return convertView;
		}

	}

	/**
	 * ע��ӿڼ�����
	 */
	public void registOnWordButtonClick(iWordButtonClickListener listener) {
		mWordButtonClickListener = listener;
	}

}
