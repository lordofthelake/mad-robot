package com.madrobot.util.pdf;

import java.util.ArrayList;

 class Pages {

	private PDFDocument mDocument;
	private IndirectObject mIndirectObject;
	private Array mKids;
	private Array mMediaBox;
	private ArrayList<Page> mPageList;
	
	 Pages(PDFDocument document, int pageWidth, int pageHeight) {
		mDocument = document;
		mIndirectObject = mDocument.newIndirectObject();
		mPageList = new ArrayList<Page>();
		mMediaBox = new Array();
		String content[] = {"0", "0", Integer.toString(pageWidth), Integer.toString(pageHeight)};
		mMediaBox.addItemsFromStringArray(content);
		mKids = new Array();
	}
	
	 IndirectObject getIndirectObject() {
		return mIndirectObject;
	}
	
	 Page newPage() {
		Page lPage = new Page(mDocument);
		mPageList.add(lPage);
		mKids.addItem(lPage.getIndirectObject().getIndirectReference());
		return lPage;
	}
	
	 void render() {
		mIndirectObject.setDictionaryContent(
				"  /Type /Pages\n  /MediaBox "+mMediaBox.toPDFString()+"\n  /Count "+Integer.toString(mPageList.size()) +"\n  /Kids "+mKids.toPDFString()+"\n"
		);
		for (Page lPage: mPageList)
			lPage.render(mIndirectObject.getIndirectReference());
	}
}
