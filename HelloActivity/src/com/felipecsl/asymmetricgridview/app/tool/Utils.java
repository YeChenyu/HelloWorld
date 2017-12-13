package com.felipecsl.asymmetricgridview.app.tool;

import com.example.android.helloactivity.R;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;
import android.view.WindowManager;


public class Utils {
	public static int dpToPx(final Context context, final float dp) {
		// Took from
		// http://stackoverflow.com/questions/8309354/formula-px-to-dp-dp-to-px-android
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) ((dp * scale) + 0.5f);
	}

	public static int getScreenWidth(final Context context) {
		if (context == null)
			return 0;
		return getDisplayMetrics(context).widthPixels;
	}

	/**
	 * Returns a valid DisplayMetrics object
	 * 
	 * @param context
	 *            valid context
	 * @return DisplayMetrics object
	 */
	public static DisplayMetrics getDisplayMetrics(final Context context) {
		final WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		final DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		return metrics;
	}

	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier(
				"status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
	
	/**
	 * 代码实现selector
	 * 
	 * @param pressed
	 * @param normal
	 * @return
	 */
	public static StateListDrawable getStateListDrawable(int pressed, int normal, Context context) {
		StateListDrawable drawable = new StateListDrawable();
		drawable.addState(new int[] { android.R.attr.state_pressed },
				getGradientDrawable(pressed, context));
		drawable.addState(new int[] { -android.R.attr.state_pressed },
				getGradientDrawable(normal, context));
		return drawable;
	}

	/**
	 * 代码实现shape
	 * 
	 * @param color
	 * @return
	 */

	public static GradientDrawable getGradientDrawable(int color, Context context) {
		int coner = (int) (context.getResources().getDimension(
				R.dimen.main_radio_button_width) / 2);
		int roundRadius = Utils.dpToPx(context, coner); // 4dp
																		// 圆角半径
		GradientDrawable mDrawable = new GradientDrawable(); // 创建drawable
		mDrawable.setColor(context.getResources().getColor(color));
		mDrawable.setCornerRadius(roundRadius);
		return mDrawable;
	}
}
