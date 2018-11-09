package com.valuestudio.contacts.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.valuestudio.contacts.R;

/**
 * @title LetterListView
 * @description 自定义字母导航ListView
 * @author zuolong
 * @date 2013-7-6
 * @version V1.0
 */
public class LetterListView extends View {

	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	private static final String[] LETTER = { "#", "A", "B", "C", "D", "E", "F",
			"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
			"T", "U", "V", "W", "X", "Y", "Z" };
	private int choose = -1;
	private Paint paint = new Paint();
	/**
	 * 画背景
	 */
	private Paint bgpaint = new Paint();
	private String currentAlpha = "#";
	/**
	 * 是否显示高亮背景
	 */
	private boolean showBkg = false;

	public LetterListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LetterListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LetterListView(Context context) {
		super(context);
	}

	public void setUpdateChoose(String alphaStr, boolean force) {
		if (!currentAlpha.equals(alphaStr) || force) {
			if ("#".equals(alphaStr)) {
				choose = 0;
			} else {
				int intAlpha = (int) alphaStr.toCharArray()[0];
				choose = intAlpha - 65 + 1;
			}
			invalidate();
			currentAlpha = alphaStr;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// if (showBkg) {
		// canvas.drawColor(Color.parseColor("#D7D7D7"));
		// }

		int height = getHeight();
		int width = getWidth();
		int singleHeight = height / LETTER.length;
		// 由于int类型整除以后会有偏差
		int offset = (height - singleHeight * LETTER.length) / 2;
		for (int i = 0; i < LETTER.length; i++) {
			paint.setColor(getResources().getColor(R.color.gray));
			paint.setTextSize(30);
			paint.setTypeface(Typeface.MONOSPACE);
			paint.setAntiAlias(true);

			// 字母背景
			bgpaint.setColor(getResources().getColor(
					android.R.color.transparent));
			bgpaint.setTypeface(Typeface.MONOSPACE);
			bgpaint.setAntiAlias(true);

			if (i == choose) {
				paint.setColor(getResources().getColor(R.color.white));
				paint.setFakeBoldText(true);
				// 设置字母导航字母背景色
				TypedArray a = getContext()
						.obtainStyledAttributes(null,
								R.styleable.ContactsAppView,
								R.attr.ContactsAppStyle, 0);
				int alphaTextColor = a.getResourceId(
						R.styleable.ContactsAppView_themeColor, R.color.blue);
				if (alphaTextColor == R.color.theme_black
						|| alphaTextColor == R.color.theme_white) {
					alphaTextColor = R.color.blue;
				}
				bgpaint.setColor(getResources().getColor(alphaTextColor));
				a.recycle();
			}
			float xPos = width / 2 - paint.measureText(LETTER[i]) / 2;

			// 计算BaseLine偏移值，以使Text位于背景正中
			float baseLineY = singleHeight / 2 - paint.getFontMetrics().bottom
					/ 2 - paint.getFontMetrics().top / 2;
			float yPos = singleHeight * i + baseLineY;
			yPos = yPos + offset;

			// canvas.drawRect(0, singleHeight * i + offset, width, singleHeight
			// * i + singleHeight + offset, bgpaint);
			RectF rect = new RectF();
			rect.left = 0;
			rect.top = singleHeight * i + offset;
			rect.right = width;
			rect.bottom = singleHeight * i + singleHeight + offset;
			float density = getResources().getDisplayMetrics().density;
			canvas.drawRoundRect(rect, 4 * density, 4 * density, bgpaint);

			canvas.drawText(LETTER[i], xPos, yPos, paint);
			paint.reset();
			bgpaint.reset();
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * LETTER.length);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < LETTER.length) {
					listener.onTouchingLetterChanged(LETTER[c]);
					choose = c;
					invalidate();
				}
			}

			break;
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < LETTER.length) {
					listener.onTouchingLetterChanged(LETTER[c]);
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			showBkg = false;
			// choose = -1;
			if (!currentAlpha.equals(LETTER[choose])) {
				setUpdateChoose(currentAlpha, true);
			} else {
				invalidate();
			}
			break;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}
