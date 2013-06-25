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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.util.Log;

/**
 * Load .OBJ files
 * <p>
 * 
 * <pre>
 * public class MyRenderer extends GLSurfaceView implements Renderer { 
 * ...
 * ...
 * private Model myModel;
 * public MyRenderer(Context context){
 * 		myModel=OBJFileLoader.loadOBJ("myfile.obj");
 * 		... 
 * }
 * 
 * public void onDrawFrame(GL10 gl) {
 * ...
 * // draw the .obj file
 * model.draw(gl);
 * 
 * }
 * 
 * 
 * }
 * </pre>
 * 
 * </p>
 * @see Model
 * @author elton.kent
 */
public final class OBJFileLoader {

	public static Model loadOBJ(InputStream is) {
		// int numVertices = 0;
		// int numFaces = 0;

		ArrayList<Short> faces = new ArrayList<Short>();
		ArrayList<Short> vtPointer = new ArrayList<Short>();
		ArrayList<Short> vnPointer = new ArrayList<Short>();
		ArrayList<Float> v = new ArrayList<Float>();
		ArrayList<Float> vn = new ArrayList<Float>();
		ArrayList<Float> vt = new ArrayList<Float>();
		ArrayList<ModelPart> parts = new ArrayList<ModelPart>();
		// ArrayList<Material> materials = null;
		BufferedReader reader = null;
		String line = null;
		Material m = null;

		// try to open file
		reader = new BufferedReader(new InputStreamReader(is));
		// new FileInputStream(fileName)));

		try {// try to read lines of the file
			while ((line = reader.readLine()) != null) {
				Log.v("obj", line);
				if (line.startsWith("f")) {// a polygonal face
					// ********************************* Process
					// FLine**************/
					String[] tokens = line.split("[ ]+");
					int c = tokens.length;

					if (tokens[1].matches("[0-9]+")) {// f: v
						if (c == 4) {// 3 faces
							for (int i = 1; i < c; i++) {
								Short s = Short.valueOf(tokens[i]);
								s--;
								faces.add(s);
							}
						} else {// more faces
							ArrayList<Short> polygon = new ArrayList<Short>();
							for (int i = 1; i < tokens.length; i++) {
								Short s = Short.valueOf(tokens[i]);
								s--;
								polygon.add(s);
							}
							faces.addAll(triangulate(polygon));// triangulate
																			// the
																			// polygon
																			// and
																			// add
																			// the
																			// resulting
																			// faces
						}
					}
					if (tokens[1].matches("[0-9]+/[0-9]+")) {// if: v/vt
						if (c == 4) {// 3 faces
							for (int i = 1; i < c; i++) {
								Short s = Short
										.valueOf(tokens[i].split("/")[0]);
								s--;
								faces.add(s);
								s = Short.valueOf(tokens[i].split("/")[1]);
								s--;
								vtPointer.add(s);
							}
						} else {// triangulate
							ArrayList<Short> tmpFaces = new ArrayList<Short>();
							ArrayList<Short> tmpVt = new ArrayList<Short>();
							for (int i = 1; i < tokens.length; i++) {
								Short s = Short
										.valueOf(tokens[i].split("/")[0]);
								s--;
								tmpFaces.add(s);
								s = Short.valueOf(tokens[i].split("/")[1]);
								s--;
								tmpVt.add(s);
							}
							faces.addAll(triangulate(tmpFaces));
							vtPointer.addAll(triangulate(tmpVt));
						}
					}
					if (tokens[1].matches("[0-9]+//[0-9]+")) {// f: v//vn
						if (c == 4) {// 3 faces
							for (int i = 1; i < c; i++) {
								Short s = Short
										.valueOf(tokens[i].split("//")[0]);
								s--;
								faces.add(s);
								s = Short.valueOf(tokens[i].split("//")[1]);
								s--;
								vnPointer.add(s);
							}
						} else {// triangulate
							ArrayList<Short> tmpFaces = new ArrayList<Short>();
							ArrayList<Short> tmpVn = new ArrayList<Short>();
							for (int i = 1; i < tokens.length; i++) {
								Short s = Short
										.valueOf(tokens[i].split("//")[0]);
								s--;
								tmpFaces.add(s);
								s = Short.valueOf(tokens[i].split("//")[1]);
								s--;
								tmpVn.add(s);
							}
							faces.addAll(triangulate(tmpFaces));
							vnPointer.addAll(triangulate(tmpVn));
						}
					}
					if (tokens[1].matches("[0-9]+/[0-9]+/[0-9]+")) {// f:
																	// v/vt/vn

						if (c == 4) {// 3 faces
							for (int i = 1; i < c; i++) {
								Short s = Short
										.valueOf(tokens[i].split("/")[0]);
								s--;
								faces.add(s);
								s = Short.valueOf(tokens[i].split("/")[1]);
								s--;
								vtPointer.add(s);
								s = Short.valueOf(tokens[i].split("/")[2]);
								s--;
								vnPointer.add(s);
							}
						} else {// triangulate
							ArrayList<Short> tmpFaces = new ArrayList<Short>();
							ArrayList<Short> tmpVn = new ArrayList<Short>();
							// Vector<Short> tmpVt=new Vector<Short>();
							for (int i = 1; i < tokens.length; i++) {
								Short s = Short
										.valueOf(tokens[i].split("/")[0]);
								s--;
								tmpFaces.add(s);
								// s=Short.valueOf(tokens[i].split("/")[1]);
								// s--;
								// tmpVt.add(s);
								// s=Short.valueOf(tokens[i].split("/")[2]);
								// s--;
								// tmpVn.add(s);
							}
							faces.addAll(triangulate(tmpFaces));
							vtPointer.addAll(triangulate(tmpVn));
							vnPointer.addAll(triangulate(tmpVn));
						}
					}

				} else if (line.startsWith("vn")) {
					/* ********** Process VN line */
					String[] tokens = line.split("[ ]+"); // split the line at
															// the spaces
					int c = tokens.length;
					for (int i = 1; i < c; i++) { // add the vertex to the
													// vertex array
						vn.add(Float.valueOf(tokens[i]));
					}
				} else if (line.startsWith("vt")) {
					/* ************** Process VT line******* */
					String[] tokens = line.split("[ ]+"); // split the line at
															// the spaces
					int c = tokens.length;
					for (int i = 1; i < c; i++) { // add the vertex to the
													// vertex array
						vt.add(Float.valueOf(tokens[i]));
					}
				} else if (line.startsWith("v")) { // line having geometric
													// position of single vertex
					/* ************ Process V - Line */
					String[] tokens = line.split("[ ]+"); // split the line at
															// the spaces
					int c = tokens.length;
					for (int i = 1; i < c; i++) { // add the vertex to the
													// vertex array
						v.add(Float.valueOf(tokens[i]));
					}
				}
				/*
				 * else if(line.startsWith("usemtl")){ try{//start of new group
				 * if(faces.size()!=0){//if not this is not the start of the
				 * first group TDModelPart model=new TDModelPart(faces,
				 * vtPointer, vnPointer, m,vn); parts.add(model); } String
				 * mtlName=line.split("[ ]+",2)[1]; //get the name of the
				 * material for(int i=0; i<materials.size(); i++){//suppose .mtl
				 * file already parsed m=materials.get(i);
				 * if(m.getName().equals(mtlName)){//if found, return from loop
				 * break; } m=null;//if material not found, set to null }
				 * faces=new Vector<Short>(); vtPointer=new Vector<Short>();
				 * vnPointer=new Vector<Short>(); } catch (Exception e) { //
				 * TODO: handle exception } } else
				 * if(line.startsWith("mtllib")){
				 * materials=MTLParser.loadMTL(line.split("[ ]+")[1]); for(int
				 * i=0; i<materials.size(); i++){ Material mat=materials.get(i);
				 * Log.v("materials",mat.toString()); } }
				 */
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (faces != null) {// if not this is not the start of the first group
			ModelPart model = new ModelPart(faces, vtPointer, vnPointer, m, vn);
			parts.add(model);
		}
		Model t = new Model(v, vn, vt, parts);
		t.buildVertexBuffer();
		Log.v("models", t.toString());
		return t;
	}

	public static Model loadOBJ(String fileName) throws FileNotFoundException {
		return loadOBJ(new FileInputStream(fileName));
	}
	
	private static ArrayList<Short> triangulate(ArrayList<Short> polygon){
		ArrayList<Short> triangles=new ArrayList<Short>();
		for(int i=1; i<polygon.size()-1; i++){
			triangles.add(polygon.get(0));
			triangles.add(polygon.get(i));
			triangles.add(polygon.get(i+1));
		}
		return triangles;
	}

}
