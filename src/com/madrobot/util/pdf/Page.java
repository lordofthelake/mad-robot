package com.madrobot.util.pdf;

import java.util.ArrayList;

class Page {

	private PDFDocument mDocument;
	private IndirectObject mIndirectObject;
	private IndirectObject mPageContents;
	private ArrayList<IndirectObject> mPageFonts;

	Page(PDFDocument document) {
		mDocument = document;
		mIndirectObject = mDocument.newIndirectObject();
		mPageFonts = new ArrayList<IndirectObject>();
		setFont(StandardFonts.SUBTYPE, StandardFonts.TIMES_ROMAN,
				StandardFonts.WIN_ANSI_ENCODING);
		mPageContents = mDocument.newIndirectObject();
		mDocument.includeIndirectObject(mPageContents);
	}

	private void addContent(String content) {
		mPageContents.addStreamContent(content);
		String streamContent = mPageContents.getStreamContent();
		mPageContents.setDictionaryContent("  /Length "
				+ Integer.toString(streamContent.length()) + "\n");
		mPageContents.setStreamContent(streamContent);
	}

	void addLine(int fromLeft, int fromBottom, int toLeft, int toBottom) {
		addContent(Integer.toString(fromLeft) + " "
				+ Integer.toString(fromBottom) + " m\n"
				+ Integer.toString(toLeft) + " " + Integer.toString(toBottom)
				+ " l\nS\n");
	}

	void addRawContent(String rawContent) {
		addContent(rawContent);
	}

	void addRectangle(int fromLeft, int fromBottom, int toLeft, int toBottom) {
		addContent(Integer.toString(fromLeft) + " "
				+ Integer.toString(fromBottom) + " " + Integer.toString(toLeft)
				+ " " + Integer.toString(toBottom) + " re\nS\n");
	}

	void addText(int leftPosition, int topPositionFromBottom, int fontSize,
			String text) {
		addText(leftPosition, topPositionFromBottom, fontSize, text,
				StandardFonts.DEGREES_0_ROTATION);
	}

	void addText(int leftPosition, int topPositionFromBottom, int fontSize,
			String text, String transformation) {
		addContent("BT\n" + transformation + " "
				+ Integer.toString(leftPosition) + " "
				+ Integer.toString(topPositionFromBottom) + " Tm\n" + "/F"
				+ Integer.toString(mPageFonts.size()) + " "
				+ Integer.toString(fontSize) + " Tf\n" + "(" + text + ") Tj\n"
				+ "ET\n");
	}

	private String getFontReferences() {
		String result = "";
		int x = 0;
		for (IndirectObject lFont : mPageFonts)
			result += "      /F" + Integer.toString(++x) + " "
					+ lFont.getIndirectReference() + "\n";
		return result;
	}

	IndirectObject getIndirectObject() {
		return mIndirectObject;
	}

	void render(String pagesIndirectReference) {
		mIndirectObject.setDictionaryContent("  /Type /Page\n  /Parent "
				+ pagesIndirectReference + "\n  /Resources <<\n    /Font <<\n"
				+ getFontReferences() + "    >>\n  >>\n  /Contents "
				+ mPageContents.getIndirectReference() + "\n");
	}

	void setFont(String subType, String baseFont) {
		IndirectObject lFont = mDocument.newIndirectObject();
		mDocument.includeIndirectObject(lFont);
		lFont.setDictionaryContent("  /Type /Font\n  /Subtype /" + subType
				+ "\n  /BaseFont /" + baseFont + "\n");
		mPageFonts.add(lFont);
	}

	void setFont(String subType, String baseFont, String encoding) {
		IndirectObject lFont = mDocument.newIndirectObject();
		mDocument.includeIndirectObject(lFont);
		lFont.setDictionaryContent("  /Type /Font\n  /Subtype /" + subType
				+ "\n  /BaseFont /" + baseFont + "\n  /Encoding /" + encoding
				+ "\n");
		mPageFonts.add(lFont);
	}
}
