package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.v7.appcompat.C0111R;
import android.support.v7.internal.text.AllCapsTransformationMethod;
import android.support.v7.internal.widget.ThemeUtils;
import android.util.AttributeSet;
import android.widget.TextView;

class AppCompatTextHelper {
    private static final int[] TEXT_APPEARANCE_ATTRS;
    private static final int[] VIEW_ATTRS;
    private final TextView mView;

    static {
        VIEW_ATTRS = new int[]{16842804};
        TEXT_APPEARANCE_ATTRS = new int[]{C0111R.attr.textAllCaps};
    }

    AppCompatTextHelper(TextView view) {
        this.mView = view;
    }

    void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        Context context = this.mView.getContext();
        TypedArray a = context.obtainStyledAttributes(attrs, VIEW_ATTRS, defStyleAttr, 0);
        int ap = a.getResourceId(0, -1);
        a.recycle();
        if (ap != -1) {
            TypedArray appearance = context.obtainStyledAttributes(ap, C0111R.styleable.TextAppearance);
            if (appearance.hasValue(C0111R.styleable.TextAppearance_textAllCaps)) {
                setAllCaps(appearance.getBoolean(C0111R.styleable.TextAppearance_textAllCaps, false));
            }
            appearance.recycle();
        }
        a = context.obtainStyledAttributes(attrs, TEXT_APPEARANCE_ATTRS, defStyleAttr, 0);
        if (a.hasValue(0)) {
            setAllCaps(a.getBoolean(0, false));
        }
        a.recycle();
        ColorStateList textColors = this.mView.getTextColors();
        if (textColors != null && !textColors.isStateful()) {
            int disabledTextColor;
            if (VERSION.SDK_INT < 21) {
                disabledTextColor = ThemeUtils.getDisabledThemeAttrColor(context, 16842808);
            } else {
                disabledTextColor = ThemeUtils.getThemeAttrColor(context, 16842808);
            }
            this.mView.setTextColor(ThemeUtils.createDisabledStateList(textColors.getDefaultColor(), disabledTextColor));
        }
    }

    void onSetTextAppearance(Context context, int resId) {
        TypedArray appearance = context.obtainStyledAttributes(resId, TEXT_APPEARANCE_ATTRS);
        if (appearance.hasValue(0)) {
            setAllCaps(appearance.getBoolean(0, false));
        }
        appearance.recycle();
    }

    void setAllCaps(boolean allCaps) {
        this.mView.setTransformationMethod(allCaps ? new AllCapsTransformationMethod(this.mView.getContext()) : null);
    }
}
