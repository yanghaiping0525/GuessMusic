package com.yang.guessmusic.view.adapter;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.yang.guessmusic.R;
import com.yang.guessmusic.bean.WordButton;
import com.yang.guessmusic.observe.WordButtonClickListener;

public class MyGridViewAdapter extends BaseAdapter {
	private List<WordButton> data = new ArrayList<>();
	private LayoutInflater inflater;
	private Context mContext;
	private Animation mScaleAnimation;
	private WordButtonClickListener mListener;

	public MyGridViewAdapter(Context context) {
		super();
		mContext = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public MyGridViewAdapter(Context context, List<WordButton> data) {
		super();
		this.data = data;
		mContext = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(List<WordButton> data) {
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final WordButton wordButton;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.word_button, parent, false);
			wordButton = data.get(position);
			//按位置添加索引,用于定位
			wordButton.setIndex(position);
			wordButton.setButton((Button) convertView);
			//设置载入动画
			mScaleAnimation = AnimationUtils.loadAnimation(mContext,
					R.anim.scale);
			mScaleAnimation.setStartOffset(position * 50);
			convertView.setTag(wordButton);
		} else {
			wordButton = (WordButton) convertView.getTag();
		}
		wordButton.getButton().setText(wordButton.getWord());
		convertView.startAnimation(mScaleAnimation);
		wordButton.getButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onButtonClick(wordButton);
				}
			}
		});
		return convertView;
	}

	public void setOnWordButtonClickListener(WordButtonClickListener listener) {
		mListener = listener;
	}

}
