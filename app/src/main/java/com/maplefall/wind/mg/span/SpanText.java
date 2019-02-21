package com.maplefall.wind.mg.span;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatEditText;

import com.maplefall.wind.mg.R;
import com.maplefall.wind.mg.note.EditOperationManager;
import com.maplefall.wind.mg.utils.EmptyUtil;

import java.util.ArrayList;
import java.util.List;

public class SpanText extends AppCompatEditText {

    private int bulletRadius = 3;
    private int bulletGapWidth = 1;
    private int linkColor = R.color.colorPrimary;
    private boolean linkUnderline = true;

    //    private Editable mEdit = getEditableText();  // 不能这样使用 mEdit -> 在从日记列表点击跳转到日记编辑时，
    // 会因为不是实时的 getEditableText()，使得 setSpan 时 mEdit 为空而报错 indexOutOfBound

    private EditOperationManager mEditManager = new EditOperationManager(this);

    private final int SPAN_STYLE = Spanned.SPAN_INCLUSIVE_INCLUSIVE;

    public SpanText(Context context) {
        super(context);
        setSaveEnabled(false);
        addTextChangedListener(mEditManager);
    }

    public SpanText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSaveEnabled(false);
        addTextChangedListener(mEditManager);
    }

    /**
     * 该方法在自定义view的onDraw()执行之前调用
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void undo() {
        mEditManager.undo();
    }

    public void redo() {
        mEditManager.redo();
    }

    public void setBold() {
        setStyle(Typeface.BOLD);
    }

    public void setItalic() {
        setStyle(Typeface.ITALIC);
    }

    private void setStyle(int style) {
        switch (style) {
            case Typeface.NORMAL:
            case Typeface.BOLD:
            case Typeface.ITALIC:
            case Typeface.BOLD_ITALIC:
                break;
            default:
                return;
        }

        int start = getSelectionStart();
        int end = getSelectionEnd();
        if (!containStyle(style, start, end)) {
            if (start < end) {
                getEditableText().setSpan(new StyleSpan(style), start, end, SPAN_STYLE);
            } else if (start == end) {
                SpannableString content = new SpannableString("");
                content.setSpan(new StyleSpan(style), 0, 0, SPAN_STYLE);
                getEditableText().append(content);
            }
        } else {
            if (start < end) {
                removeStyle(style, start, end);
            }
        }
    }

    protected void removeStyle(int style, int start, int end) {
        StyleSpan[] spans = getEditableText().getSpans(start, end, StyleSpan.class);
        for (StyleSpan span : spans) {
            if (span.getStyle() == style) {

                /*
                 * 因为调用removeSpan()方法时，指定span及其相邻具有相同span的文字去除该样式，
                 * 所以先去除该span，然后在添加相邻文字的样式
                 */
                SpanPart part = new SpanPart(getEditableText().getSpanStart(span), getEditableText().getSpanEnd(span));
                getEditableText().removeSpan(span);

                if (part.isValid()) {
                    if (part.getStart() < start) {
                        getEditableText().setSpan(new StyleSpan(style), part.getStart(), start, SPAN_STYLE);
                    }

                    if (part.getEnd() > end) {
                        getEditableText().setSpan(new StyleSpan(style), end, part.getEnd(), SPAN_STYLE);
                    }
                }
                break;
            }
        }
    }

    protected boolean containStyle(int style, int start, int end) {
        if (start > end || (start == end && start == 0)) {
            return false;
        }

        // 在 start 等于 end 的情况下，需要根据前边文字的样式判断 -> 不需要判断这种情况，
        // 因为在取消这种样式的时候，只取消选中文字的样式
        // getSpan() 会获取所有的样式
        StyleSpan[] spans = getEditableText().getSpans(start, end, StyleSpan.class);
        for (StyleSpan span : spans) {
            if (span.getStyle() == style) {
                return true;
            }
        }
        return false;
    }

    public void setBackground() {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        if (!containBackground(start, end)) {
            if (start <= end) {
                getEditableText().setSpan(new BackgroundColorSpan(getResources().getColor(R.color.colorPrimary, null)), start, end, SPAN_STYLE);
            }
        } else {
            if (start >= end) {
                return;
            }
            BackgroundColorSpan[] spans = getEditableText().getSpans(start, end, BackgroundColorSpan.class);
            for (BackgroundColorSpan span : spans) {
                SpanPart part = new SpanPart(getEditableText().getSpanStart(span), getEditableText().getSpanEnd(span));
                getEditableText().removeSpan(span);
                if (part.isValid()) {
                    if (part.getStart() < start) {
                        getEditableText().setSpan(new BackgroundColorSpan(getResources().getColor(R.color.colorPrimary, null)), part.getStart(), start, SPAN_STYLE);
                    }
                    if (end < part.getEnd()) {
                        getEditableText().setSpan(new BackgroundColorSpan(getResources().getColor(R.color.colorPrimary, null)), end, part.getEnd(), SPAN_STYLE);
                    }
                }
            }
        }
    }

    protected boolean containBackground(int start, int end) {
        if (start > end || (start == end && start == 0)) {
            return false;
        }

        BackgroundColorSpan[] spans = getEditableText().getSpans(start, end, BackgroundColorSpan.class);
        return spans.length > 0;
    }

    public void setUnderLine() {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        if (!containUnderline(start, end)) {
            if (start <= end) {
                getEditableText().setSpan(new UnderlineSpan(), start, end, SPAN_STYLE);
            }
        } else {
            if (start < end) {
                UnderlineSpan[] spans = getEditableText().getSpans(start, end, UnderlineSpan.class);
                for (UnderlineSpan span : spans) {
                    SpanPart part = new SpanPart(getEditableText().getSpanStart(span), getEditableText().getSpanEnd(span));
                    getEditableText().removeSpan(span);
                    if (part.isValid()) {
                        if (part.getStart() < start) {
                            getEditableText().setSpan(new UnderlineSpan(), part.getStart(), start, SPAN_STYLE);
                        }

                        if (part.getEnd() > end) {
                            getEditableText().setSpan(new UnderlineSpan(), end, part.getEnd(), SPAN_STYLE);
                        }
                    }
                }
            }
        }
    }

    protected boolean containUnderline(int start, int end) {
        if (start > end || (start == end && start == 0)) {
            return false;
        }

        UnderlineSpan[] spans = getEditableText().getSpans(start, end, UnderlineSpan.class);
        return spans.length > 0;
    }

    public void setStrikeThrough() {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        if (!containStrikeThrough(start, end)) {
            if (start <= end) {
                getEditableText().setSpan(new StrikethroughSpan(), start, end, SPAN_STYLE);
            }
        } else {
            if (start < end) {
                StrikethroughSpan[] spans = getEditableText().getSpans(start, end, StrikethroughSpan.class);
                for (StrikethroughSpan span : spans) {
                    SpanPart part = new SpanPart(getEditableText().getSpanStart(span), getEditableText().getSpanEnd(span));
                    getEditableText().removeSpan(span);
                    if (part.isValid()) {
                        if (part.getStart() < start) {
                            getEditableText().setSpan(new StrikethroughSpan(), part.getStart(), start, SPAN_STYLE);
                        }

                        if (end < part.getEnd()) {
                            getEditableText().setSpan(new StrikethroughSpan(), end, part.getEnd(), SPAN_STYLE);
                        }
                    }
                }
            }
        }
    }

    private boolean containStrikeThrough(int start, int end) {
        if (start > end || (start == end && start == 0)) {
            return false;
        }

        StrikethroughSpan[] spans = getEditableText().getSpans(start, end, StrikethroughSpan.class);
        return spans.length > 0;
    }


    public void setBullet() {
        if (!containBullet()) {
            bulletValid();
        } else {
            bulletInvalid();
        }
    }

    private void bulletValid() {
        String[] lines = TextUtils.split(getEditableText().toString(), "\n");

        for (int i = 0; i < lines.length; i++) {
            if (containBullet(i)) {
                continue;
            }

            int lineStart = 0;
            for (int j = 0; j < i; j++) {
                lineStart = lineStart + lines[j].length() + 1; // \n
            }

            int lineEnd = lineStart + lines[i].length();
            if (lineStart >= lineEnd) {
                continue;
            }

            // Find selection area inside
            int bulletStart = 0;
            int bulletEnd = 0;
            if (lineStart <= getSelectionStart() && getSelectionEnd() <= lineEnd) {
                bulletStart = lineStart;
                bulletEnd = lineEnd;
            } else if (getSelectionStart() <= lineStart && lineEnd <= getSelectionEnd()) {
                bulletStart = lineStart;
                bulletEnd = lineEnd;
            }

            if (bulletStart < bulletEnd) {
                getEditableText().setSpan(new SpanFormat(R.color.colorPrimary, bulletRadius, bulletGapWidth), bulletStart, bulletEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private void bulletInvalid() {
        String[] lines = TextUtils.split(getEditableText().toString(), "\n");

        for (int i = 0; i < lines.length; i++) {
            if (!containBullet(i)) {
                continue;
            }

            int lineStart = 0;
            for (int j = 0; j < i; j++) {
                lineStart = lineStart + lines[j].length() + 1;
            }

            int lineEnd = lineStart + lines[i].length();
            if (lineStart >= lineEnd) {
                continue;
            }

            int bulletStart = 0;
            int bulletEnd = 0;
            if (lineStart <= getSelectionStart() && getSelectionEnd() <= lineEnd) {
                bulletStart = lineStart;
                bulletEnd = lineEnd;
            } else if (getSelectionStart() <= lineStart && lineEnd <= getSelectionEnd()) {
                bulletStart = lineStart;
                bulletEnd = lineEnd;
            }

            if (bulletStart < bulletEnd) {
                BulletSpan[] spans = getEditableText().getSpans(bulletStart, bulletEnd, BulletSpan.class);
                for (BulletSpan span : spans) {
                    getEditableText().removeSpan(span);
                }
            }
        }
    }

    private boolean containBullet() {
        String[] lines = TextUtils.split(getEditableText().toString(), "\n");
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            int lineStart = 0;
            for (int j = 0; j < i; j++) {
                lineStart = lineStart + lines[j].length() + 1;
            }

            int lineEnd = lineStart + lines[i].length();
            if (lineStart >= lineEnd) {
                continue;
            }

            if (lineStart <= getSelectionStart() && getSelectionEnd() <= lineEnd) {
                list.add(i);
            } else if (getSelectionStart() <= lineStart && lineEnd <= getSelectionEnd()) {
                list.add(i);
            }
        }

        for (Integer i : list) {
            if (!containBullet(i)) {
                return false;
            }
        }

        return true;
    }

    protected boolean containBullet(int index) {
        String[] lines = TextUtils.split(getEditableText().toString(), "\n");
        if (index < 0 || index >= lines.length) {
            return false;
        }

        int start = 0;
        for (int i = 0; i < index; i++) {
            start = start + lines[i].length() + 1;
        }

        int end = start + lines[index].length();
        if (start >= end) {
            return false;
        }

        BulletSpan[] spans = getEditableText().getSpans(start, end, BulletSpan.class);
        return spans.length > 0;
    }

    public void setLink(String link) {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        if (start < end) {
            if (!EmptyUtil.isEmpty(link)) {
                removeLink(start, end);
                getEditableText().setSpan(new SpanURL(link, R.color.colorPrimary, linkUnderline), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                removeLink(start, end);
            }
        }
    }

    // Remove all span in selection, not like the boldInvalid()
    protected void removeLink(int start, int end) {
        URLSpan[] spans = getEditableText().getSpans(start, end, URLSpan.class);
        for (URLSpan span : spans) {
            getEditableText().removeSpan(span);
        }
    }

//    public void clearFormats() {
//        setText(getEditableText().toString());
//        setSelection(getEditableText().length());  //焦点回到文本末尾
//    }

    public void fromHtml(String source) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(new SpanParser().fromHtml(source));
        switchToSpanStyle(builder, 0, builder.length());
        setText(builder);
    }

    public String toHtml() {
        return new SpanParser().toHtml(getEditableText());
    }

    private void switchToSpanStyle(Editable editable, int start, int end) {
        BulletSpan[] bulletSpans = editable.getSpans(start, end, BulletSpan.class);
        for (BulletSpan span : bulletSpans) {
            int spanStart = editable.getSpanStart(span);
            int spanEnd = editable.getSpanEnd(span);
            spanEnd = 0 < spanEnd && spanEnd < editable.length() && editable.charAt(spanEnd) == '\n' ? spanEnd - 1 : spanEnd;
            editable.removeSpan(span);
            editable.setSpan(new SpanFormat(R.color.colorPrimary, bulletRadius, bulletGapWidth), spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        URLSpan[] urlSpans = editable.getSpans(start, end, URLSpan.class);
        for (URLSpan span : urlSpans) {
            int spanStart = editable.getSpanStart(span);
            int spanEnd = editable.getSpanEnd(span);
            editable.removeSpan(span);
            editable.setSpan(new SpanURL(span.getURL(), linkColor, linkUnderline), spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
