
package com.madrobot.util.pdf;

 class CrossReferenceTable extends List {

	private int mObjectNumberStart;
	
	 CrossReferenceTable() {
		super();
		clear();
	}
	
	 void addObjectXRefInfo(int ByteOffset, int Generation, boolean InUse) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%010d", ByteOffset));
		sb.append(" ");
		sb.append(String.format("%05d", Generation));
		if (InUse) {
			sb.append(" n");
		} else {
			sb.append(" f");
		}
		sb.append("\n");
		mList.add(sb.toString());
	}
	
	 @Override
	public void clear() {
		super.clear();
		addObjectXRefInfo(0, 65536, false); // free objects linked list head
		mObjectNumberStart = 0;
	}
	
	int getObjectNumberStart() {
		return mObjectNumberStart;
	}
	
	 private String getObjectsXRefInfo() {
		return renderList();
	}

	private String render() {
		StringBuilder sb = new StringBuilder();
		sb.append("xref");
		sb.append("\n");
		sb.append(mObjectNumberStart);
		sb.append(" ");
		sb.append(mList.size());
		sb.append("\n");
		sb.append(getObjectsXRefInfo());
		return sb.toString(); 
	}	
	
	void setObjectNumberStart(int Value) {
		mObjectNumberStart = Value;
	}

	@Override
	public String toPDFString() {
		return render();
	}

}
