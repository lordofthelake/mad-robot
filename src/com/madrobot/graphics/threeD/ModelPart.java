/*******************************************************************************
 * Copyright (c) 2011 MadRobot.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *  Elton Kent - initial API and implementation
 ******************************************************************************/
package com.madrobot.graphics.threeD;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;


public class ModelPart {
	private static short[] toPrimitiveArrayS(ArrayList<Short> vector){
		short[] s;
		s=new short[vector.size()];
		for (int i=0; i<vector.size(); i++){
			s[i]=vector.get(i);
		}
		return s;
	}
	ShortBuffer faceBuffer;
	ArrayList<Short> faces;
	Material material;
	private FloatBuffer normalBuffer;
	ArrayList<Short> vnPointer;
	
	ArrayList<Short> vtPointer;
	public ModelPart(ArrayList<Short> faces, ArrayList<Short> vtPointer,
			ArrayList<Short> vnPointer, Material material, ArrayList<Float> vn) {
		super();
		this.faces = faces;
		this.vtPointer = vtPointer;
		this.vnPointer = vnPointer;
		this.material = material;
		
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vnPointer.size() * 4*3);
		byteBuf.order(ByteOrder.nativeOrder());
		normalBuffer = byteBuf.asFloatBuffer();
		for(int i=0; i<vnPointer.size(); i++){
			float x=vn.get(vnPointer.get(i)*3);
			float y=vn.get(vnPointer.get(i)*3+1);
			float z=vn.get(vnPointer.get(i)*3+2);
			normalBuffer.put(x);
			normalBuffer.put(y);
			normalBuffer.put(z);
		}
		normalBuffer.position(0);
		

		ByteBuffer fBuf = ByteBuffer.allocateDirect(faces.size() * 2);
		fBuf.order(ByteOrder.nativeOrder());
		faceBuffer = fBuf.asShortBuffer();
		faceBuffer.put(toPrimitiveArrayS(faces));
		faceBuffer.position(0);
	}
	public ShortBuffer getFaceBuffer(){
		return faceBuffer;
	}
	public int getFacesCount(){
		return faces.size();
	}
	public Material getMaterial(){
		return material;
	}
	public FloatBuffer getNormalBuffer(){
		return normalBuffer;
	}
	@Override
	public String toString(){
		String str=new String();
		if(material!=null)
			str+="Material name:"+material.getName();
		else
			str+="Material not defined!";
		str+="\nNumber of faces:"+faces.size();
		str+="\nNumber of vnPointers:"+vnPointer.size();
		str+="\nNumber of vtPointers:"+vtPointer.size();
		return str;
	}
	
	
}
