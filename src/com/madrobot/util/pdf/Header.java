package com.madrobot.util.pdf;

class Header extends Base {

	private String mRenderedHeader;
	private String mVersion;

	Header() {
		clear();
	}

	@Override
	void clear() {
		setVersion(1, 4);
	}

	int getPDFStringSize() {
		return mRenderedHeader.length();
	}

	private void render() {
		mRenderedHeader = "%PDF-" + mVersion + "\n\n";
	}

	void setVersion(int Major, int Minor) {
		mVersion = Integer.toString(Major) + "." + Integer.toString(Minor);
		render();
	}

	@Override
	String toPDFString() {
		return mRenderedHeader;
	}

}
