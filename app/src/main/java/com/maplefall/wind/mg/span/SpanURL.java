package com.maplefall.wind.mg.span;

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.URLSpan;

public class SpanURL extends URLSpan {
    private int mLinkColor = 0;
    private boolean mLinkUnderline = true;

    public SpanURL(String url, int linkColor, boolean linkUnderline) {
        super(url);
        mLinkColor = linkColor;
        mLinkUnderline = linkUnderline;
    }

    public SpanURL(Parcel src) {
        super(src);
        mLinkColor = src.readInt();
        mLinkUnderline = src.readInt() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mLinkColor);
        dest.writeInt(mLinkUnderline ? 1 : 0);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(mLinkColor);
        ds.setUnderlineText(mLinkUnderline);
    }
}
