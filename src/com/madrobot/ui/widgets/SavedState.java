package com.madrobot.ui.widgets;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View.BaseSavedState;

 class SavedState extends BaseSavedState {
	public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
		@Override
		public SavedState createFromParcel(Parcel in) {
			return new SavedState(in);
		}

		@Override
		public SavedState[] newArray(int size) {
			return new SavedState[size];
		}
	};

	int currentPage = -1;

	private SavedState(Parcel in) {
		super(in);
		currentPage = in.readInt();
	}

	SavedState(Parcelable superState) {
		super(superState);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		super.writeToParcel(out, flags);
		out.writeInt(currentPage);
	}
}