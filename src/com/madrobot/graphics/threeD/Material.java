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


/**
 * @see MTLFileLoader
 * @author elton.kent
 *
 */
public class Material {
	float alpha;
	float[] ambientColor; //ambient color
	float[] diffuseColor;
	int illum;
	String name;
	float shine;
	float[] specularColor;
	String textureFile;
	
	public Material(String name){
		this.name=name;
	}
	public float getAlpha() {
		return alpha;
	}

	public float[] getAmbientColor() {
		return ambientColor;
	}

	public FloatBuffer getAmbientColorBuffer(){
		FloatBuffer f;
		ByteBuffer b = ByteBuffer.allocateDirect(12);
		b.order(ByteOrder.nativeOrder());
		f = b.asFloatBuffer();
		f.put(ambientColor);
		f.position(0);
		return f;
	}
	public float[] getDiffuseColor() {
		return diffuseColor;
	}
	public FloatBuffer getDiffuseColorBuffer(){
		FloatBuffer f;
		ByteBuffer b = ByteBuffer.allocateDirect(12);
		b.order(ByteOrder.nativeOrder());
		f = b.asFloatBuffer();
		f.put(diffuseColor);
		f.position(0);
		return f;
	}

	public int getIllum() {
		return illum;
	}
	public String getName() {
		return name;
	}

	public float getShine() {
		return shine;
	}

	public float[] getSpecularColor() {
		return specularColor;
	}
	public FloatBuffer getSpecularColorBuffer(){
		FloatBuffer f;
		ByteBuffer b = ByteBuffer.allocateDirect(12);
		b.order(ByteOrder.nativeOrder());
		f = b.asFloatBuffer();
		f.put(specularColor);
		f.position(0);
		return f;
	}

	public String getTextureFile() {
		return textureFile;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public void setAmbientColor(float r, float g, float b) {
		ambientColor = new float[3];
		ambientColor[0]=r;
		ambientColor[1]=g;
		ambientColor[2]=b;
	}

	public void setDiffuseColor(float r, float g, float b) {
		diffuseColor = new float[3];
		diffuseColor[0]=r;
		diffuseColor[1]=g;
		diffuseColor[2]=b;
	}

	public void setIllum(int illum) {
		this.illum = illum;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setShine(float shine) {
		this.shine = shine;
	}

	public void setSpecularColor(float r, float g, float b) {
		specularColor = new float[3];
		specularColor[0]=r;
		specularColor[1]=g;
		specularColor[2]=b;
	}

	public void setTextureFile(String textureFile) {
		this.textureFile = textureFile;
	}
	@Override
	public String toString(){
		String str=new String();
		str+="Material name: "+name;	
		str+="\nAmbient color: "+ambientColor.toString();
		str+="\nDiffuse color: "+diffuseColor.toString();	
		str+="\nSpecular color: "+specularColor.toString();
		str+="\nAlpha: "+alpha;
		str+="\nShine: "+shine;
		return str;
	}
}
