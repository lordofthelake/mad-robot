package com.madrobot.util.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Entry point to create a PDF document
 * <p>
 * The following sample code demonstrates how to create a PDF <code>
 * <pre>
 * PDFWriter writer = new PDFWriter();
 * writer.addText(10, 800, 18, "Hello from elton");
 * writer.addRawContent("16 1 1 rgb\n");
 * File file=new File("sdcard://test.pdf");
 * file.createNewFile();
 * writer.writeToStream(new FileOutputStream(file), "ISO-8859-1");
 * </pre>
 * </code>
 * </p>
 * 
 * @author elton.kent
 * 
 */
public final class PDFWriter {

	private IndirectObject mCatalog;
	private Page mCurrentPage;
	private PDFDocument mDocument;
	private Pages mPages;

	/**
	 * Creates a new PDF writer instance with default Paper size A4.
	 * 
	 * @see PaperSize
	 */
	public PDFWriter() {
		newDocument(PaperSize.A4_WIDTH, PaperSize.A4_HEIGHT);
	}

	/**
	 * Creates a new PDF writer instance with custom paper size
	 * 
	 * @param pageWidth
	 *            paper width
	 * @param pageHeight
	 *            paper height
	 */
	public PDFWriter(int pageWidth, int pageHeight) {
		newDocument(pageWidth, pageHeight);
	}

	public void addLine(int fromLeft, int fromBottom, int toLeft, int toBottom) {
		mCurrentPage.addLine(fromLeft, fromBottom, toLeft, toBottom);
	}

	/**
	 * Add raw content to the current page
	 * 
	 * @param rawContent
	 */
	public void addRawContent(String rawContent) {
		mCurrentPage.addRawContent(rawContent);
	}

	public void addRectangle(int fromLeft, int fromBottom, int toLeft,
			int toBottom) {
		mCurrentPage.addRectangle(fromLeft, fromBottom, toLeft, toBottom);
	}

	/**
	 * Add text to the current page
	 * 
	 * @param leftPosition
	 *            of the text
	 * @param topPositionFromBottom
	 *            of the text
	 * @param fontSize
	 *            of given text
	 * @param text
	 *            to write
	 */
	public void addText(int leftPosition, int topPositionFromBottom,
			int fontSize, String text) {
		mCurrentPage.addText(leftPosition, topPositionFromBottom, fontSize,
				text, StandardFonts.DEGREES_0_ROTATION);
	}

	public void addText(int leftPosition, int topPositionFromBottom,
			int fontSize, String text, String transformation) {
		mCurrentPage.addText(leftPosition, topPositionFromBottom, fontSize,
				text, transformation);
	}

	/**
	 * Return the PDF content as String
	 * 
	 * @return
	 */
	public String asString() {
		mPages.render();
		return mDocument.toPDFString();
	}

	private void newDocument(int pageWidth, int pageHeight) {
		mDocument = new PDFDocument();
		mCatalog = mDocument.newIndirectObject();
		mDocument.includeIndirectObject(mCatalog);
		mPages = new Pages(mDocument, pageWidth, pageHeight);
		mDocument.includeIndirectObject(mPages.getIndirectObject());
		renderCatalog();
		newPage();
	}

	/**
	 * Creates a new page.
	 * <p>
	 * All subsequent operations will be performed on this page
	 * </p>
	 */
	public void newPage() {
		mCurrentPage = mPages.newPage();
		mDocument.includeIndirectObject(mCurrentPage.getIndirectObject());
		mPages.render();
	}

	private void renderCatalog() {
		mCatalog.setDictionaryContent("  /Type /Catalog\n  /Pages "
				+ mPages.getIndirectObject().getIndirectReference() + "\n");
	}

	/**
	 * @see StandardFonts#SUBTYPE
	 * @see StandardFonts
	 * @param subType
	 * @param baseFont
	 */
	public void setFont(String subType, String baseFont) {
		mCurrentPage.setFont(subType, baseFont);
	}

	public void setFont(String subType, String baseFont, String encoding) {
		mCurrentPage.setFont(subType, baseFont, encoding);
	}

	/**
	 * Write the current PDF document to a OutputStream
	 * 
	 * @param os
	 *            OutputStream
	 * @param encoding
	 *            usually "ISO-8859-1"
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public void writeToStream(OutputStream os, String encoding)
			throws UnsupportedEncodingException, IOException {
		os.write(asString().getBytes(encoding));
		os.close();

	}
}
