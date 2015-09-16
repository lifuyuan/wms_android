package com.quaie.wms.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.quaie.wms.R;

/**
 * Created by fuyuan on 2015/9/16.
 */
public class TitleBuilder {

    private View viewTitle;
    private TextView tvTitle;
    private ImageView ivRight;

    public TitleBuilder(Activity context) {
        viewTitle = context.findViewById(R.id.rl_titlebar);
        tvTitle = (TextView) viewTitle.findViewById(R.id.titlebar_tv);
        ivRight = (ImageView) viewTitle.findViewById(R.id.titlebar_iv_right);
    }

    public TitleBuilder(View context) {
        viewTitle = context.findViewById(R.id.rl_titlebar);
        tvTitle = (TextView) viewTitle.findViewById(R.id.titlebar_tv);
        ivRight = (ImageView) viewTitle.findViewById(R.id.titlebar_iv_right);
    }

    // title

    public TitleBuilder setTitleBgRes(int resid) {
        viewTitle.setBackgroundResource(resid);
        return this;
    }

    public TitleBuilder setTitleText(String text) {
        tvTitle.setVisibility(TextUtils.isEmpty(text) ? View.GONE
                : View.VISIBLE);
        tvTitle.setText(text);
        return this;
    }

    public TitleBuilder setRightImage(int resId) {
        ivRight.setVisibility(resId > 0 ? View.VISIBLE : View.GONE);
        ivRight.setImageResource(resId);
        return this;
    }

    public TitleBuilder setRightOnClickListener(View.OnClickListener listener) {
        if (ivRight.getVisibility() == View.VISIBLE) {
            ivRight.setOnClickListener(listener);
        }
        return this;
    }

    public View build() {
        return viewTitle;
    }
}
