
package com.madrobot.util.pdf;

import java.util.ArrayList;

 abstract class List extends Base {

	protected ArrayList<String> mList;

	 List() {
		mList = new ArrayList<String>();
	}
	
	 @Override
	 void clear() {
		mList.clear();
	}
	
	String renderList() {
		StringBuilder sb = new StringBuilder();
		int x = 0;
		while (x < mList.size()) {
			sb.append(mList.get(x).toString());
			x++;
		}
		return sb.toString();
	}
}
