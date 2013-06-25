package com.madrobot.util.pdf;

class IndirectIdentifier extends Base {

	private int mGeneration;
	private int mNumber;

	IndirectIdentifier() {
		clear();
	}

	@Override
	void clear() {
		mNumber = 0;
		mGeneration = 0;
	}

	int getGeneration() {
		return mGeneration;
	}

	int getNumber() {
		return mNumber;
	}

	void setGeneration(int Generation) {
		this.mGeneration = Generation;
	}

	void setNumber(int Number) {
		this.mNumber = Number;
	}

	@Override
	String toPDFString() {
		return Integer.toString(mNumber) + " " + Integer.toString(mGeneration);
	}

}
