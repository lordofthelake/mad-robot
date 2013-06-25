
package com.madrobot.util.pdf;

 class EnclosedContent extends Base {

	private String mBegin;
	protected StringBuilder mContent;
	private String mEnd;
	
	 EnclosedContent() {
		clear();
	}
	
	 void addContent(String Value) {
		mContent.append(Value);
	}

	 void addNewLine() {
		mContent.append("\n");
	}
	
	 void addSpace() {
		mContent.append(" ");
	}

	 @Override
	public void clear() {
		mContent = new StringBuilder();
	}
	
	 String getContent() {
		return mContent.toString();
	}

	 boolean hasContent() {
		return mContent.length() > 0;
	}
	
	 void setBeginKeyword(String Value, boolean NewLineBefore, boolean NewLineAfter) {
		if (NewLineBefore)
			mBegin = "\n" + Value;
		else
			mBegin = Value;
		if (NewLineAfter)
			mBegin += "\n";
	}

	 void setContent(String Value) {
		clear();
		mContent.append(Value);
	}
	
	void setEndKeyword(String Value, boolean NewLineBefore, boolean NewLineAfter) {		
		if (NewLineBefore)
			mEnd = "\n" + Value;
		else
			mEnd = Value;
		if (NewLineAfter)
			mEnd += "\n";
	}
	
	@Override
	public String toPDFString() {
		return mBegin + mContent.toString() + mEnd;
	}

}
