package com.ando.base.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ArrayRes;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import static com.ando.base.utils.GravityEnum.CENTER;
import static com.ando.base.utils.GravityEnum.END;
import static com.ando.base.utils.GravityEnum.START;


/**
 * 主题工具
 *
 * @author xuexiang
 * @since 2018/11/14 下午1:46
 */
public final class ThemeUtils {

    private ThemeUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    @ColorInt
    public static int getDisabledColor(Context context) {
        final int primaryColor = resolveColor(context, android.R.attr.textColorPrimary);
        final int disabledColor = isColorDark(primaryColor) ? Color.BLACK : Color.WHITE;
        return adjustAlpha(disabledColor, 0.3f);
    }

    @ColorInt
    public static int adjustAlpha(
            @ColorInt int color, @SuppressWarnings("SameParameterValue") float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @ColorInt
    public static int resolveColor(Context context, @AttrRes int attr) {
        return resolveColor(context, attr, 0);
    }

    @ColorInt
    public static int resolveColor(Context context, @AttrRes int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getColor(0, fallback);
        } finally {
            a.recycle();
        }
    }

    public static int getColorFromAttrRes(int attrRes, int defaultValue, Context context) {
        TypedArray a = context.obtainStyledAttributes(new int[]{attrRes});
        try {
            return a.getColor(0, defaultValue);
        } finally {
            a.recycle();
        }
    }

    public static float resolveFloat(Context context, int attrRes) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrRes, typedValue, true);
        return typedValue.getFloat();
    }

    public static float resolveFloat(Context context, int attrRes, float defaultValue){
        TypedArray a = context.obtainStyledAttributes(new int[]{attrRes});
        try {
            return a.getFloat(0, defaultValue);
        } finally {
            a.recycle();
        }
    }

    // Try to resolve the colorAttr attribute.
    public static ColorStateList resolveActionTextColorStateList(
            Context context, @AttrRes int colorAttr, ColorStateList fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{colorAttr});
        try {
            final TypedValue value = a.peekValue(0);
            if (value == null) {
                return fallback;
            }
            if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT
                    && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                return getActionTextStateList(context, value.data);
            } else {
                final ColorStateList stateList = a.getColorStateList(0);
                if (stateList != null) {
                    return stateList;
                } else {
                    return fallback;
                }
            }
        } finally {
            a.recycle();
        }
    }

    // Get the specified color resource, creating a ColorStateList if the resource
    // points to a color value.
    public static ColorStateList getActionTextColorStateList(Context context, @ColorRes int colorId) {
        final TypedValue value = new TypedValue();
        context.getResources().getValue(colorId, value, true);
        if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT
                && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return getActionTextStateList(context, value.data);
        } else {

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                //noinspection deprecation
                return context.getResources().getColorStateList(colorId);
            } else {
                return context.getColorStateList(colorId);
            }
        }
    }

    /**
     * Returns a color associated with a particular resource ID
     *
     * <p>Starting in {@link Build.VERSION_CODES#M}, the returned color will be styled for
     * the specified Context's theme.
     *
     * @param colorId The desired resource identifier, as generated by the aapt tool. This integer
     *                encodes the package, type, and resource entry. The value 0 is an invalid identifier.
     * @return A single color value in the form 0xAARRGGBB.
     */
    @ColorInt
    public static int getColor(Context context, @ColorRes int colorId) {
        return ContextCompat.getColor(context, colorId);
    }

    public static String resolveString(Context context, @AttrRes int attr) {
        TypedValue v = new TypedValue();
        context.getTheme().resolveAttribute(attr, v, true);
        return (String) v.string;
    }

    public static String resolveString(Resources.Theme theme, @AttrRes int attr) {
        TypedValue v = new TypedValue();
        theme.resolveAttribute(attr, v, true);
        return (String) v.string;
    }


    public static Drawable resolveDrawable(Context context, @AttrRes int attr) {
        return resolveDrawable(context, attr, null);
    }

    private static Drawable resolveDrawable(
            Context context,
            @AttrRes int attr,
            @SuppressWarnings("SameParameterValue") Drawable fallback) {
        TypedArray array = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            Drawable drawable = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = array.getDrawable(0);
            } else {
                int id = array.getResourceId(0, -1);
                if (id != -1) {
                    drawable = AppCompatResources.getDrawable(context, id);
                }
            }
            if (drawable == null && fallback != null) {
                drawable = fallback;
            }
            return drawable;
        } finally {
            array.recycle();
        }
    }

    public static int resolveDimension(Context context, @AttrRes int attr) {
        return resolveDimension(context, attr, -1);
    }

    public static int resolveDimension(Context context, @AttrRes int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getDimensionPixelSize(0, fallback);
        } finally {
            a.recycle();
        }
    }

    public static boolean resolveBoolean(Context context, @AttrRes int attr, boolean fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getBoolean(0, fallback);
        } finally {
            a.recycle();
        }
    }

    public static boolean resolveBoolean(Context context, @AttrRes int attr) {
        return resolveBoolean(context, attr, false);
    }

    public static boolean isColorDark(@ColorInt int color) {
        double darkness =
                1
                        - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color))
                        / 255;
        return darkness >= 0.5;
    }

    public static void setBackgroundCompat(View view, Drawable d) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            //noinspection deprecation
            view.setBackgroundDrawable(d);
        } else {
            view.setBackground(d);
        }
    }


    public static ColorStateList getActionTextStateList(Context context, int newPrimaryColor) {
        final int fallBackButtonColor =
                ThemeUtils.resolveColor(context, android.R.attr.textColorPrimary);
        if (newPrimaryColor == 0) {
            newPrimaryColor = fallBackButtonColor;
        }
        int[][] states =
                new int[][]{
                        new int[]{-android.R.attr.state_enabled}, // disabled
                        new int[]{} // enabled
                };
        int[] colors = new int[]{ThemeUtils.adjustAlpha(newPrimaryColor, 0.4f), newPrimaryColor};
        return new ColorStateList(states, colors);
    }

    public static int[] getColorArray(@NonNull Context context, @ArrayRes int array) {
        if (array == 0) {
            return null;
        }
        TypedArray ta = context.getResources().obtainTypedArray(array);
        int[] colors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            colors[i] = ta.getColor(i, 0);
        }
        ta.recycle();
        return colors;
    }

    public static <T> boolean isIn(@NonNull T find, @Nullable T[] ary) {
        if (ary == null || ary.length == 0) {
            return false;
        }
        for (T item : ary) {
            if (item.equals(find)) {
                return true;
            }
        }
        return false;
    }

    //=================//

    private static int gravityEnumToAttrInt(GravityEnum value) {
        switch (value) {
            case CENTER:
                return 1;
            case END:
                return 2;
            default:
                return 0;
        }
    }

    public static GravityEnum resolveGravityEnum(
            Context context, @AttrRes int attr, GravityEnum defaultGravity) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            switch (a.getInt(0, gravityEnumToAttrInt(defaultGravity))) {
                case 1:
                    return CENTER;
                case 2:
                    return END;
                default:
                    return START;
            }
        } finally {
            a.recycle();
        }
    }

}