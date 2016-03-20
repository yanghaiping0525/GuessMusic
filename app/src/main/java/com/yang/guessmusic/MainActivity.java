package com.yang.guessmusic;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yang.guessmusic.bean.Song;
import com.yang.guessmusic.bean.WordButton;
import com.yang.guessmusic.data.Const;
import com.yang.guessmusic.observe.MyAlertDialogClickListener;
import com.yang.guessmusic.observe.WordButtonClickListener;
import com.yang.guessmusic.util.DialogView;
import com.yang.guessmusic.util.FileStorage;
import com.yang.guessmusic.util.MyPlayer;
import com.yang.guessmusic.util.RandomGenerateChineseCharacter;
import com.yang.guessmusic.view.MyGridView;
import com.yang.guessmusic.view.adapter.MyGridViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements WordButtonClickListener {
    //唱盘动画
    private Animation mPanAnim;
    //唱盘拨杆拨入动画
    private Animation mDrivingLeverInAnim;
    //唱盘拨杆拨出动画
    private Animation mDrivingLeverOutAnim;
    //歌曲播放按钮,游戏退出按钮
    private ImageButton mPlayButton, mBackButton;
    //唱盘和拨杆
    private ImageView mPan, mLever;
    private boolean isPlaying;
    //所有文字信息
    private List<WordButton> mWords = new ArrayList<>();
    //已选择的文字信息
    private List<WordButton> mSelectedWords = new ArrayList<>();
    //文字布局
    private MyGridView mGridView;
    private MyGridViewAdapter mAdapter;
    private LayoutInflater mInflater;
    //所选文字显示区域
    private LinearLayout mWordsContainer;
    //当前播放的歌曲
    private Song mCurrentSong;
    private int mCurrentLevel = -1;
    private View mPassView;
    private static final int ANSWER_RIGHT = 100;
    private static final int ANSWER_WRONG = 200;
    private static final int ANSWER_LACK = 300;
    private static final int SPARK_TIME = 6;
    private static final int DIALOG_ELIMINATE = 1;
    private static final int DIALOG_TIPS = 2;
    private static final int DIALOG_COIN_LACK = 3;
    private static final int DIALOG_LEAVE = 4;
    //当前游戏金币数量
    private int mCurrentCoins = Const.TOTAL_COINS;
    //显示游戏金币，关卡，游戏显示当前歌曲名称，通关显示当前关卡
    private TextView mCoinsTv, mCurrentLevelTv, mCurrentSongNameTv,
            mCurrentLevelFloatButtonTv;
    //通关的下一首按钮
    private ImageButton mNextLevelBtn;
    //监听歌曲播放结束事件
    private MyPlayer.MyMusicCompleteListener mMusicListener = new MyPlayer.MyMusicCompleteListener() {

        @Override
        public void onMusicComplete() {
            if (mPanAnim != null)
                mPan.clearAnimation();
        }
    };

    //监听提示事件(消耗30个金币消除一个错误答案)
    private MyAlertDialogClickListener mEliminateWordListener = new MyAlertDialogClickListener() {

        @Override
        public void onClick() {
            eliminateOneErrorWord();
        }
    };

    //监听提示事件(消耗90个金币获得一个正确答案)
    private MyAlertDialogClickListener mTipsListener = new MyAlertDialogClickListener() {

        @Override
        public void onClick() {
            tipsAnswer();
        }
    };

    //监听游戏金币不足事件这里没有处理
    private MyAlertDialogClickListener mCoinLackListener = new MyAlertDialogClickListener() {

        @Override
        public void onClick() {

        }
    };

    //监听游戏退出事件
    private MyAlertDialogClickListener mLeaveListener = new MyAlertDialogClickListener() {

        @Override
        public void onClick() {
            MainActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //载入游戏数据记录
        int[] gameInfo = FileStorage.loadGameInfo(MainActivity.this);
        mCurrentLevel = gameInfo[Const.LOAD_GAME_INFO_LEVEL];
        mCurrentCoins = gameInfo[Const.LOAD_GAME_INFO_COINS];
        initFindViewById();
        setDataAndEvent();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //停止播放动画
        if (mPan != null) {
            mPan.clearAnimation();
        }
        if (mLever != null) {
            mLever.clearAnimation();
        }
        //停止音乐播放
        MyPlayer.stopPlay();
        //保存游戏信息
        FileStorage.saveGameInfo(MainActivity.this, mCurrentLevel,
                mCurrentCoins);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        MyPlayer.releaseMedia();
        //保存游戏信息
        FileStorage.saveGameInfo(MainActivity.this, mCurrentLevel - 1,
                mCurrentCoins);
    }

    private void initFindViewById() {
        mPan = (ImageView) findViewById(R.id.imv_music_pan);
        mLever = (ImageView) findViewById(R.id.imv_misic_lever);
        mBackButton = (ImageButton) findViewById(R.id.btn_bar_back);
        mPlayButton = (ImageButton) findViewById(R.id.btn_play_start);
        mGridView = (MyGridView) findViewById(R.id.mygridview);
        mCoinsTv = (TextView) findViewById(R.id.tv_bar_coins);
        mPassView = findViewById(R.id.pass_view);
        mNextLevelBtn = (ImageButton) findViewById(R.id.btn_nextlevel);
        mCurrentLevelFloatButtonTv = (TextView) findViewById(R.id.tv_game_level);
        mWordsContainer = (LinearLayout) findViewById(R.id.name_container);
        mCurrentLevelTv = (TextView) findViewById(R.id.tv_current_level);
        mCurrentSongNameTv = (TextView) findViewById(R.id.tv_current_song_name);
    }

    private void setDataAndEvent() {
        //显示当前金币数量
        mCoinsTv.setText(mCurrentCoins + "");
        mAdapter = new MyGridViewAdapter(MainActivity.this);
        //监听文字按钮点击事件
        mAdapter.setOnWordButtonClickListener(this);
        mInflater = LayoutInflater.from(this);
        //监听歌曲播放结束事件
        MyPlayer.setOnMusicCompleteListener(mMusicListener);
        //初始化唱盘播放按钮已经唱盘拨杆的动画
        initAnim();
        //获得歌曲信息显示随机文字，并播放音乐
        initCurrentLevelData();
        //处理提示事件(消耗30个金币消除一个错误答案)
        handleEliminateEvent();
        //处理提示事件(消耗90个金币获得一个正确答案)
        handleTipsEvent();
        //监听退出按钮事件
        mBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showConfirmDialog(DIALOG_LEAVE);
            }
        });
        //监听播放按钮事件
        mPlayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                playMusic();
            }

        });
        //监听下一关按钮事件
        mNextLevelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //设置循环
                if (mCurrentLevel == Const.SONG.length - 1) {
                    mCurrentLevel = -1;
                }
                mCoinsTv.setText((mCurrentCoins += 30) + "");
                //隐藏通关界面
                mPassView.setVisibility(View.INVISIBLE);
                //重新加载数据
                initCurrentLevelData();
            }
        });
    }

    private void initCurrentLevelData() {
        //获取歌曲信息
        mCurrentSong = loadSongInfo(++mCurrentLevel % Const.SONG.length);
        //根据歌曲名字长度先获得空文字的相应数量的暂时不显示出来的WordButton
        mSelectedWords = initWordsContainer();
        //设置通关时显示的关卡
        mCurrentLevelFloatButtonTv.setText(mCurrentLevel + 1 + "");
        //歌曲歌曲名称的长度选择不同尺寸的答案选择框
        ViewGroup.LayoutParams params;
        if (mCurrentSong.getSongNameLength() > 8) {
            params = new ViewGroup.LayoutParams(100, 100);
        } else if (mCurrentSong.getSongNameLength() > 5) {
            params = new ViewGroup.LayoutParams(150, 150);
        } else {
            params = new ViewGroup.LayoutParams(180, 180);
        }
        //将答案选择框添加到容器中
        for (int i = 0; i < mSelectedWords.size(); i++) {
            mWordsContainer.addView(mSelectedWords.get(i).getButton(), params);
        }
        //获得包含正确答案随机文字
        mWords = initWords();
        //显示所有文字
        mGridView.updateData(mWords, mAdapter);
        //播放音乐
        playMusic();
    }

    private char[] generateRandomWords() {
        Random random = new Random();
        char[] words = new char[MyGridView.WORD_COUNT];
        for (int i = 0; i < mCurrentSong.getSongNameLength(); i++) {
            //先获得歌曲名字
            words[i] = mCurrentSong.getNameCharacters()[i];
        }
        for (int i = mCurrentSong.getSongNameLength(); i < MyGridView.WORD_COUNT; i++) {
            //获得随机文字
            words[i] = RandomGenerateChineseCharacter.getRandomChar();
        }
        //乱序排列
        for (int i = MyGridView.WORD_COUNT - 1; i >= 0; i--) {
            int index = random.nextInt(i + 1);
            char buf = words[index];
            words[index] = words[i];
            words[i] = buf;
        }
        return words;
    }


    private void initAnim() {
        mPanAnim = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.rotate_pan);
        LinearInterpolator mPanInterpolator = new LinearInterpolator();
        mPanAnim.setInterpolator(mPanInterpolator);
        mPanAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLever.startAnimation(mDrivingLeverOutAnim);
                isPlaying = false;
                mPlayButton.setVisibility(View.VISIBLE);
            }
        });
        mDrivingLeverInAnim = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.rotate_lever_in);
        mDrivingLeverInAnim.setFillAfter(true);
        LinearInterpolator mLeverInInterpolator = new LinearInterpolator();
        mDrivingLeverInAnim.setInterpolator(mLeverInInterpolator);
        mDrivingLeverInAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPan.startAnimation(mPanAnim);
            }
        });
        mDrivingLeverOutAnim = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.rotate_lever_out);
        mDrivingLeverOutAnim.setFillAfter(true);
        LinearInterpolator mLeverOutInterpolator = new LinearInterpolator();
        mDrivingLeverOutAnim.setInterpolator(mLeverOutInterpolator);
    }


    //根据不同事件弹出相应时间对话框
    private void showConfirmDialog(int event) {
        switch (event) {
            case DIALOG_COIN_LACK:
                DialogView.showDialog(MainActivity.this, "金币不足", "进入商城",
                        mCoinLackListener);
                break;
            case DIALOG_ELIMINATE:
                DialogView.showDialog(MainActivity.this, "将会消耗金币", "花掉"
                                + getReduceCoinCountByEliminate() + "个金币消除一个错误文字",
                        mEliminateWordListener);
                break;
            case DIALOG_TIPS:
                DialogView
                        .showDialog(MainActivity.this, "将会消耗金币", "花掉"
                                        + getReduceCoinCountByTips() + "个金币获得一个文字提示",
                                mTipsListener);
                break;
            case DIALOG_LEAVE:
                DialogView.showDialog(MainActivity.this, "退出", "即将退出游戏",
                        mLeaveListener);
        }
    }


    private void playMusic() {
        if (!isPlaying) {
            isPlaying = true;
            MyPlayer.playSong(MainActivity.this, mCurrentSong.getSongFileName());
            mLever.startAnimation(mDrivingLeverInAnim);
            mPlayButton.setVisibility(View.INVISIBLE);
        }
    }

    private Song loadSongInfo(int currentLevel) {
        if (currentLevel < 0)
            throw new RuntimeException("歌曲信息获取失败");
        Song song = new Song();
        String[] info = Const.SONG[currentLevel];
        song.setSongFileName(info[Const.INDEX_SONG_FILE_NAME]);
        song.setSongName(info[Const.INDEX_SONG_NAME]);
        return song;
    }


    private List<WordButton> initWords() {
        List<WordButton> words = new ArrayList<>();
        //获得答案及随机文字
        char[] randomWords = generateRandomWords();
        for (int i = 0; i < MyGridView.WORD_COUNT; i++) {
            WordButton button = new WordButton();
            button.setWord(randomWords[i] + "");
            words.add(button);
        }
        return words;
    }

    private List<WordButton> initWordsContainer() {
        List<WordButton> words = new ArrayList<>();
        for (int i = 0; i < mCurrentSong.getSongNameLength(); i++) {
            View v = mInflater.inflate(R.layout.word_button, null);
            final WordButton wordButton = new WordButton();
            Button button = (Button) v.findViewById(R.id.btn_word_button);
            button.setTextColor(Color.WHITE);
            button.setText("");
            button.setBackgroundResource(R.mipmap.game_wordblank);
            //设置答案区文字按钮的点击事件
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    clearTheAnswer(wordButton);
                }
            });
            wordButton.setButton(button);
            wordButton.setWord("");
            words.add(wordButton);
        }
        return words;
    }

    @Override
    public void onButtonClick(WordButton wordButton) {
        //将所点击的按钮的文字显示在答案区,并隐藏所点按钮
        setSelectedWord(wordButton);
        //判断是否通关
        int answer = checkTheAnswer();
        switch (answer) {
            //答案缺省,显示文字为白色
            case ANSWER_LACK:
                for (int i = 0; i < mSelectedWords.size(); i++) {
                    mSelectedWords.get(i).getButton().setTextColor(Color.WHITE);
                }
                break;
            //答案正确,显示通关界面
            case ANSWER_RIGHT:
                for (int i = 0; i < mSelectedWords.size(); i++) {
                    mSelectedWords.get(i).getButton().setTextColor(Color.GREEN);
                }
                handlePassEvent();
                break;
            //答案错误闪烁文字
            case ANSWER_WRONG:
                sparkTheWords();
                break;
        }
    }

    private void sparkTheWords() {
        TimerTask task = new TimerTask() {
            boolean isColorChange = false;
            int sparkTime = 0;

            @Override
            public void run() {
                //因为要刷新UI这里方便起见运行在主线程
                runOnUiThread(new Runnable() {
                    public void run() {
                        //闪烁3次(变化6次)
                        if (sparkTime++ < SPARK_TIME) {
                            for (int i = 0; i < mSelectedWords.size(); i++) {
                                mSelectedWords
                                        .get(i)
                                        .getButton()
                                        .setTextColor(
                                                !isColorChange ? Color.RED
                                                        : Color.WHITE);
                            }
                            isColorChange = !isColorChange;
                        } else
                            return;
                    }
                });
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, 150);
    }

    private void setSelectedWord(WordButton wordButton) {
        //从前往后遍历答案区的已选按钮
        for (int i = 0; i < mSelectedWords.size(); i++) {
            //如果有位置(答案区的按钮未被赋值)
            if (mSelectedWords.get(i).getWord().length() == 0) {
                wordButton.setVisible(false);
                //显示所点文字
                mSelectedWords.get(i).getButton().setText(wordButton.getWord());
                //给答案区的按钮赋值
                mSelectedWords.get(i).setWord(wordButton.getWord());
                //记录被点击文字按钮的位置,用于再次点击答案区的文字按钮后将选择区的文字按钮重新显示
                mSelectedWords.get(i).setIndex(wordButton.getIndex());
                break;
            }
        }
    }

    private void clearTheAnswer(WordButton wordButton) {
        //将所有按钮的文字设置回白色
        for (int i = 0; i < mSelectedWords.size(); i++) {
            mSelectedWords.get(i).getButton().setTextColor(Color.WHITE);
        }
        //重置被点击按钮
        wordButton.getButton().setText("");
        wordButton.setWord("");
        //将原来对应文字的按钮重新显示
        mWords.get(wordButton.getIndex()).setVisible(true);
    }


    private int checkTheAnswer() {
        //先判断答案是否齐全
        for (int i = 0; i < mSelectedWords.size(); i++) {
            if (mSelectedWords.get(i).getWord().toString().length() == 0) {
                return ANSWER_LACK;
            }
        }
        //如果答案齐全则判断所选文字与歌曲文字是否匹配
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mSelectedWords.size(); i++) {
            sb.append(mSelectedWords.get(i).getWord());
        }
        return sb.toString().equals(mCurrentSong.getSongName()) ? ANSWER_RIGHT
                : ANSWER_WRONG;
    }

    private void handlePassEvent() {
        //保存游戏进度
        FileStorage.saveGameInfo(MainActivity.this, mCurrentLevel - 1,
                mCurrentCoins);
        //刷新游戏进度
        mCurrentLevelTv.setText(mCurrentLevel + 1 + "");
        //刷新歌曲名字
        mCurrentSongNameTv.setText(mCurrentSong.getSongName());
        //停止播放动画
        mPan.clearAnimation();
        //停止歌曲播放
        MyPlayer.stopPlay();
        //播放金币音效
        MyPlayer.playSound(MainActivity.this, MyPlayer.SOUND_COIN);
        //显示通关界面
        mPassView.setVisibility(View.VISIBLE);
        mWordsContainer.removeAllViews();
    }

    private void handleEliminateEvent() {
        ImageButton button = (ImageButton) findViewById(R.id.imbt_eliminate);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showConfirmDialog(DIALOG_ELIMINATE);
            }
        });
    }

    private void handleTipsEvent() {
        ImageButton button = (ImageButton) findViewById(R.id.imbt_tips);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showConfirmDialog(DIALOG_TIPS);
            }
        });
    }

    private boolean handleCoins(int coinsCount) {
        if (mCurrentCoins + coinsCount < 0) {
            return false;
        } else {
            mCurrentCoins += coinsCount;
            mCoinsTv.setText(mCurrentCoins + "");
            return true;
        }
    }

    private int getReduceCoinCountByEliminate() {
        return getResources().getInteger(R.integer.pay_eliminate);
    }

    private int getReduceCoinCountByTips() {
        return getResources().getInteger(R.integer.pay_tips);
    }

    private void eliminateOneErrorWord() {
        if (handleCoins(-getReduceCoinCountByEliminate())) {
            WordButton wordButton = getErrorWord();
            if (wordButton != null) {
                wordButton.setVisible(false);
            }
        } else {
            showConfirmDialog(DIALOG_COIN_LACK);
            return;
        }
    }

    private WordButton getErrorWord() {
        Random random = new Random();
        WordButton wordButton;
        while (true) {
            int index = random.nextInt(MyGridView.WORD_COUNT);
            wordButton = mWords.get(index);
            if (wordButton.isVisible() && !isAnswerWord(wordButton)) {
                return wordButton;
            }
        }

    }

    private boolean isAnswerWord(WordButton wordButton) {
        boolean result = false;
        for (int i = 0; i < mCurrentSong.getSongNameLength(); i++) {
            if (wordButton.getWord().equals(
                    mCurrentSong.getNameCharacters()[i] + "")) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void tipsAnswer() {
        boolean tips = false;
        if (mCurrentCoins + (-getReduceCoinCountByTips()) > 0) {
            for (int i = 0; i < mSelectedWords.size(); i++) {
                if (mSelectedWords.get(i).getWord().length() == 0) {
                    //获得一个正确的答案模拟一次点击事件
                    onButtonClick(getAnswerWord(i));
                    tips = true;
                    //减少金币
                    if (!handleCoins(-getReduceCoinCountByTips())) {
                        return;
                    }
                    break;
                }
            }
        } else {
            showConfirmDialog(DIALOG_COIN_LACK);
        }
        //如果答案显示区已满闪烁提醒
        if (!tips) {
            sparkTheWords();
        }

    }

    private WordButton getAnswerWord(int position) {
        WordButton wordButton;
        String answer = mCurrentSong.getNameCharacters()[position] + "";
        //遍历选择区获得正确答案
        for (int i = 0; i < MyGridView.WORD_COUNT; i++) {
            wordButton = mWords.get(i);
            if (wordButton.getWord().equals(answer)) {
                return wordButton;
            }
        }
        return null;
    }


}

