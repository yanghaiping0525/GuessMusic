package com.yang.guessmusic.view;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import com.yang.guessmusic.bean.WordButton;
import com.yang.guessmusic.view.adapter.MyGridViewAdapter;

public class MyGridView extends GridView{
	public static final int WORD_COUNT = 24;
	
	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void updateData(List<WordButton> data,MyGridViewAdapter adapter){
		adapter.setData(data);
		setAdapter(adapter);
	}

}
