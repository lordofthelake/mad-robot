
package com.madrobot.util.pdf;

import java.util.ArrayList;

 class Body extends List {

	private int mByteOffsetStart;
	private int mGeneratedObjectsCount;
	private int mObjectNumberStart;
	private ArrayList<IndirectObject> mObjectsList;
	
	 Body() {
		super();
		clear();
	}
	
	 @Override
	public void clear() {
		super.clear();
		mByteOffsetStart = 0;
		mObjectNumberStart = 0;
		mGeneratedObjectsCount = 0;
		mObjectsList = new ArrayList<IndirectObject>();
	}
	
	 int getByteOffsetStart() {
		return mByteOffsetStart;
	}
	
	 IndirectObject getNewIndirectObject() {
		return getNewIndirectObject(getNextAvailableObjectNumber(), 0, true);
	}
	
	 IndirectObject getNewIndirectObject(int Number, int Generation, boolean InUse) {
		IndirectObject iobj = new IndirectObject();
		iobj.setNumberID(Number);
		iobj.setGeneration(Generation);
		iobj.setInUse(InUse);
		return iobj;
	}
	
	 int getNextAvailableObjectNumber() {
		return ++mGeneratedObjectsCount + mObjectNumberStart;
	}

	 int getObjectByteOffset(int x) {
		return mObjectsList.get(x).getByteOffset();
	}
	
	 int getObjectGeneration(int x) {
		return mObjectsList.get(x).getGeneration();
	}
	
	 int getObjectNumberStart() {
		return mObjectNumberStart;
	}
	
	 int getObjectsCount() {
		return mObjectsList.size();
	}

	 void includeIndirectObject(IndirectObject iobj) {		
		mObjectsList.add(iobj);
	}

	 boolean isInUseObject(int x) {
		return mObjectsList.get(x).getInUse();
	}

	 private String render() {
		int x = 0;
		int offset = mByteOffsetStart;
		while (x < mObjectsList.size()) {
			IndirectObject iobj = mObjectsList.get(x);
			String s = iobj.toPDFString()+"\n";
			mList.add(s);
			iobj.setByteOffset(offset);
			offset += s.length();
			x++;
		}
		return renderList();
	}
	
	void setByteOffsetStart(int Value) {
		mByteOffsetStart = Value;
	}
	
	void setObjectNumberStart(int Value) {
		mObjectNumberStart = Value;
	}

	@Override
	public String toPDFString() {		
		return render();
	}

}
