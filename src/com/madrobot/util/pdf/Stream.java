
package com.madrobot.util.pdf;

 class Stream extends EnclosedContent {

	 Stream() {
		super();
		setBeginKeyword("stream",false,true);
		setEndKeyword("endstream",false,true);
	}

}
