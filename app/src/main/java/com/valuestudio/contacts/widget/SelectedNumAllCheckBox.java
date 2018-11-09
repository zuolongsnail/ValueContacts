package com.valuestudio.contacts.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.valuestudio.contacts.R;

public class SelectedNumAllCheckBox extends View {

	private View mSelecteAllView;

	private ImageView mSelectedImageView;
	private TextView mSelectedNumView;

	private boolean mChecked = false;

	public SelectedNumAllCheckBox(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mSelecteAllView = inflater.inflate(R.layout.selected_num_all_checkbox,
				null);
		mSelectedImageView = (ImageView) mSelecteAllView
				.findViewById(R.id.selected_image);
		mSelectedNumView = (TextView) mSelecteAllView
				.findViewById(R.id.selected_text);
		setText("0");
	}

	public View getView() {
		return mSelecteAllView;
	}

	public void setText(String selectedNum) {
		mSelectedNumView.setText(selectedNum);
	}

	public boolean getChecked() {
		return mChecked;
	}

	public void setChecked(boolean checked) {
		this.mChecked = checked;
		if (mChecked) {
			setText("All");
			mSelectedImageView
					.setBackgroundResource(R.drawable.ic_btn_selected_num_all);
		} else {
			mSelectedImageView
					.setBackgroundResource(R.drawable.ic_btn_selected_num);
		}
	}

	public void changeChecked() {
		mChecked = !mChecked;
		if (mChecked) {
			setText("All");
			mSelectedImageView
					.setBackgroundResource(R.drawable.ic_btn_selected_num_all);
		} else {
			setText("0");
			mSelectedImageView
					.setBackgroundResource(R.drawable.ic_btn_selected_num);
		}
	}

}