package com.madrobot.util.pdf;

class IndirectObject extends Base {

	private int mByteOffset;
	private EnclosedContent mContent;
	private Dictionary mDictionaryContent;
	private IndirectIdentifier mID;
	private boolean mInUse;
	private Stream mStreamContent;

	IndirectObject() {
		clear();
	}

	void addContent(String Value) {
		mContent.addContent(Value);
	}

	void addDictionaryContent(String Value) {
		mDictionaryContent.addContent(Value);
	}

	void addStreamContent(String Value) {
		mStreamContent.addContent(Value);
	}

	@Override
	public void clear() {
		mID = new IndirectIdentifier();
		mByteOffset = 0;
		mInUse = false;
		mContent = new EnclosedContent();
		mContent.setBeginKeyword("obj", false, true);
		mContent.setEndKeyword("endobj", false, true);
		mDictionaryContent = new Dictionary();
		mStreamContent = new Stream();
	}

	int getByteOffset() {
		return mByteOffset;
	}

	String getContent() {
		return mContent.getContent();
	}

	String getDictionaryContent() {
		return mDictionaryContent.getContent();
	}

	int getGeneration() {
		return mID.getGeneration();
	}

	String getIndirectReference() {
		return mID.toPDFString() + " R";
	}

	boolean getInUse() {
		return mInUse;
	}

	int getNumberID() {
		return mID.getNumber();
	}

	public String getStreamContent() {
		return mStreamContent.getContent();
	}

	protected String render() {
		StringBuilder sb = new StringBuilder();
		sb.append(mID.toPDFString());
		sb.append(" ");
		// j-a-s-d: this can be performed in inherited classes DictionaryObject
		// and StreamObject
		if (mDictionaryContent.hasContent()) {
			mContent.setContent(mDictionaryContent.toPDFString());
			if (mStreamContent.hasContent())
				mContent.addContent(mStreamContent.toPDFString());
		}
		sb.append(mContent.toPDFString());
		return sb.toString();
	}

	void setByteOffset(int Value) {
		mByteOffset = Value;
	}

	void setContent(String Value) {
		mContent.setContent(Value);
	}

	void setDictionaryContent(String Value) {
		mDictionaryContent.setContent(Value);
	}

	void setGeneration(int Value) {
		mID.setGeneration(Value);
	}

	void setInUse(boolean Value) {
		mInUse = Value;
	}

	void setNumberID(int Value) {
		mID.setNumber(Value);
	}

	void setStreamContent(String Value) {
		mStreamContent.setContent(Value);
	}

	@Override
	public String toPDFString() {
		return render();
	}

}
