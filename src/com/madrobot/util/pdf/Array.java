package com.madrobot.util.pdf;

class Array extends EnclosedContent {

	Array() {
		super();
		setBeginKeyword("[ ", false, false);
		setEndKeyword("]", false, false);
	}

	void addItem(String s) {
		addContent(s);
		addSpace();
	}

	void addItemsFromStringArray(String[] content) {
		for (String s : content)
			addItem(s);
	}

}
