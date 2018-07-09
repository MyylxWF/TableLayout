package com.commonlib.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * myylx on 2018/7/5.
 */

public class TableLayout extends LinearLayout {

    public static final String TAG = "TableLayout";

    public static final int LINE_TYPE_EQUAL = 1;//宽度:平分整个空间
    public static final int LINE_TYPE_TEXTLENGTH = 2;//宽度:字体宽度

    private Context context;

    final ArrayList<TextView> tvList = new ArrayList<>();
    final ArrayList<Integer> textLengthList = new ArrayList<>();

    private int screentWidth;
    private float density;
    private int lineHeight;//scroll line h
    private int bottomLineHeight;//most bottom line h

    private int currentIndex;
    private int lineType = LINE_TYPE_EQUAL;//line type

    private int textColor = 0xFF666666;//default text color
    private int textSelectColor = 0xFF0000FF;// select text color
    private int lineColor = 0xFF0000FF;// scroll line color
    private int bottomLineColor = 0xFFDDDDDD;//most bottom line color

    private int textSize;// default text size
    private int textSelectSize;//select text size

    private FrameLayout.LayoutParams vLineLp;
    private FrameLayout.LayoutParams ffLp;

    private View lineView;
    private View bottomLineView;
    private ViewPager vp;
    private FrameLayout lineFrameLayout;

    private String[] tabs;


    public TableLayout(Context context) {
        this(context, null);
    }

    public TableLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TableLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setOrientation(VERTICAL);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screentWidth = displayMetrics.widthPixels;
        density = displayMetrics.density;
        textSize = 16;
        textSelectSize = 18;
        lineHeight = (int) (density * 2 + 0.5);
        bottomLineHeight = (int) (density * 1 + 0.5);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode != MeasureSpec.EXACTLY) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (48 * density + 0.5), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setViewAndTab(ViewPager vp, String[] tabS) {
        this.vp = vp;
        this.tabs = tabS;
        if (tabS == null || tabS.length > 6) {
            throw new RuntimeException("tabs=null or tabs.length most<=6");
        }

        //init tab
        initTab();

        //init line
        initLine();

        //init text lenght
        initTextLenght();

        //init vp
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // judge left or rigth
                if (currentIndex <= position) {
                    ffLp.leftMargin = (int) (positionOffset * (screentWidth * 1.0 / tabs.length) + currentIndex * (screentWidth / tabs.length));
                } else {
                    ffLp.leftMargin = (int) (-(1 - positionOffset) * (screentWidth * 1.0 / tabs.length) + currentIndex * (screentWidth / tabs.length));
                }
                lineFrameLayout.setLayoutParams(ffLp);
            }

            @Override
            public void onPageSelected(int position) {
                //alter text color
                for (int i = 0; i < tvList.size(); i++) {
                    tvList.get(i).setTextColor(position == i ? textSelectColor : textColor);
                    tvList.get(i).setTextSize(position == i ? textSelectSize : textSize);
                }
                // select LINE_TYPE_TEXTLENGTH set text length
                if (lineType == LINE_TYPE_TEXTLENGTH) {
                    vLineLp.width = textLengthList.get(position);
                    lineView.setLayoutParams(vLineLp);
                }
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initTab() {

        // create tabs parent
        LinearLayout tabsLayout = new LinearLayout(context);
        tabsLayout.setOrientation(HORIZONTAL);
        tabsLayout.setWeightSum(tabs.length);
        addView(tabsLayout);
        LinearLayout.LayoutParams tabsLp = (LayoutParams) tabsLayout.getLayoutParams();
        tabsLp.width = LayoutParams.MATCH_PARENT;
        measure(0, 0);
        tabsLp.height = getMeasuredHeight() - lineHeight - bottomLineHeight;
        tabsLayout.setLayoutParams(tabsLp);

        //tab
        LinearLayout.LayoutParams tvParams;
        for (int i = 0; i < tabs.length; i++) {
            TextView tv = new TextView(context);
            tv.setText(tabs[i]);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(textColor);
            tabsLayout.addView(tv);
            tvParams = (LayoutParams) tv.getLayoutParams();
            tvParams.weight = 1;
            tvParams.width = 0;
            tvParams.height = LayoutParams.MATCH_PARENT;
            //tag is index currentItem tag
            tv.setTag(i);
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (int) v.getTag();
                    vp.setCurrentItem(tag, false);
                }
            });
            tv.setLayoutParams(tvParams);
            tvList.add(tv);
        }
        //defalut 0
        tvList.get(0).setTextColor(textSelectColor);
        tvList.get(0).setTextSize(textSelectSize);
    }

    private void initLine() {
        //all line parent layout
        FrameLayout frameLayout = new FrameLayout(context);
        addView(frameLayout);
        LinearLayout.LayoutParams fLp = (LayoutParams) frameLayout.getLayoutParams();
        fLp.height = LayoutParams.WRAP_CONTENT;
        fLp.width = LayoutParams.MATCH_PARENT;
        frameLayout.setLayoutParams(fLp);

        //single line parent layout
        lineFrameLayout = new FrameLayout(context);
        frameLayout.addView(lineFrameLayout);
        ffLp = (FrameLayout.LayoutParams) lineFrameLayout.getLayoutParams();
        ffLp.width = screentWidth / tabs.length;
        ffLp.height = LayoutParams.WRAP_CONTENT;
        lineFrameLayout.setLayoutParams(ffLp);

        // scroll line view
        lineView = new View(context);
        lineView.setBackgroundColor(lineColor);
        lineFrameLayout.addView(lineView);
        vLineLp = (FrameLayout.LayoutParams) lineView.getLayoutParams();
        vLineLp.height = lineHeight;
        vLineLp.width = screentWidth / tabs.length;
        vLineLp.gravity = Gravity.CENTER_HORIZONTAL;
        lineView.setLayoutParams(vLineLp);

        // bottom line view
        bottomLineView = new View(context);
        bottomLineView.setBackgroundColor(bottomLineColor);
        addView(bottomLineView);
        LinearLayout.LayoutParams bLineLp = (LayoutParams) bottomLineView.getLayoutParams();
        bLineLp.height = bottomLineHeight;
        bLineLp.width = LayoutParams.MATCH_PARENT;
        bottomLineView.setLayoutParams(bLineLp);
    }

    public void initTextLenght() {
        TextPaint tp = new TextPaint();
        tp.setTextSize(textSelectSize);
        for (String tab : tabs) {
            textLengthList.add((int) ((tp.measureText(tab) + 0.5) * density));
        }
    }


    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        lineView.setBackgroundColor(lineColor);
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
        vLineLp.height = lineHeight;
        lineView.setLayoutParams(vLineLp);
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        for (int i = 0; i < tvList.size(); i++) {
            tvList.get(i).setTextColor(currentIndex == i ? textSelectColor : textColor);
            tvList.get(i).setTextSize(currentIndex == i ? textSelectSize : textSize);
        }
    }

    public void setTextSelectColor(int textSelectColor) {
        this.textSelectColor = textSelectColor;
        for (int i = 0; i < tvList.size(); i++) {
            tvList.get(i).setTextColor(currentIndex == i ? textSelectColor : textColor);
            tvList.get(i).setTextSize(currentIndex == i ? textSelectSize : textSize);
        }
    }

    public void setBottomLineColor(int bottomLineColor) {
        this.bottomLineColor = bottomLineColor;
        bottomLineView.setBackgroundColor(bottomLineColor);
    }

    public void setLineType(int lineType) {
        this.lineType = lineType;
        if (lineType == LINE_TYPE_EQUAL) {
            vLineLp.width = screentWidth / tabs.length;
        } else if (lineType == LINE_TYPE_TEXTLENGTH) {
            vLineLp.width = textLengthList.get(currentIndex);
        }
        lineView.setLayoutParams(vLineLp);
    }
}
