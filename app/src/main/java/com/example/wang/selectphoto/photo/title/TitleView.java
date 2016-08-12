package com.example.wang.selectphoto.photo.title;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wang.selectphoto.photo.util.BasicTool;

/**
 * Created by wang on 2016/7/22.
 * title
 */
public class TitleView extends RelativeLayout implements View.OnClickListener{

    private LinearLayout leftBody;
    private LinearLayout centerBody;
    private LinearLayout rightBody;
    private AngleView back;
    private TextView title, confirm;
    private Context mContext;
    private OnTitleClickListener onTitleClickListener;

    public TitleView(Context context) {
        super(context);
        mContext = context;
        init();
        regEvent();
    }

    private void init() {
        setBackgroundColor(Color.BLACK);
        setPadding(BasicTool.dip2px(mContext, 10), 0, BasicTool.dip2px(mContext, 10), 0);
        leftBody = new LinearLayout(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.addRule(ALIGN_PARENT_LEFT);
        leftBody.setLayoutParams(params);
        leftBody.setGravity(Gravity.CENTER_VERTICAL);
        back = new AngleView(mContext);
        back.setStrokeWidth(1.2f);
        back.setColor(Color.WHITE);
        leftBody.addView(back, new LinearLayout.LayoutParams(BasicTool.dip2px(mContext, 10), BasicTool.dip2px(mContext, 20)));
        addView(leftBody);

        centerBody = new LinearLayout(mContext);
        centerBody.setGravity(CENTER_VERTICAL);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.addRule(CENTER_IN_PARENT);
        centerBody.setLayoutParams(params);
        title = new TextView(mContext);
        title.setTextSize(18);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        centerBody.addView(title, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        addView(centerBody);

        rightBody = new LinearLayout(mContext);
        rightBody.setOrientation(LinearLayout.HORIZONTAL);
        rightBody.setGravity(Gravity.CENTER_VERTICAL);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.addRule(ALIGN_PARENT_RIGHT);
        rightBody.setLayoutParams(params);
        confirm = new TextView(mContext);
        confirm.setTextSize(18);
        confirm.setText("确定");
        confirm.setTextColor(Color.WHITE);
        rightBody.addView(confirm, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(rightBody);
    }

    private void regEvent() {
        leftBody.setOnClickListener(this);
        rightBody.setOnClickListener(this);
    }

    public void setTitle(String name) {
        title.setText(name);
    }

    public String getTitleName() {
        return title.getText().toString();
    }

    public void setTitleColor(int color) {
        title.setTextColor(color);
    }

    public LinearLayout getLeftBody() {
        return leftBody;
    }

    public void setLeftBody(LinearLayout leftBody) {
        this.leftBody = leftBody;
    }

    public LinearLayout getCenterBody() {
        return centerBody;
    }

    public void setCenterBody(LinearLayout centerBody) {
        this.centerBody = centerBody;
    }

    public LinearLayout getRightBody() {
        return rightBody;
    }

    public void setRightBody(LinearLayout rightBody) {
        this.rightBody = rightBody;
    }

    @Override
    public void onClick(View v) {
        if (onTitleClickListener != null) {
            if (v == leftBody) {
                onTitleClickListener.back();
            } else if (v == rightBody) {
                onTitleClickListener.confirm();
            }
        }
    }

    public OnTitleClickListener getOnTitleClickListener() {
        return onTitleClickListener;
    }

    public void setOnTitleClickListener(OnTitleClickListener onTitleClickListener) {
        this.onTitleClickListener = onTitleClickListener;
    }
}
