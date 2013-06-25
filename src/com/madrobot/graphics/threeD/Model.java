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
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Representation of .OBJ files
 * 
 * @author elton.kent
 * @see OBJFileLoader
 */
public class Model {
	private static float[] toPrimitiveArrayF(ArrayList<Float> vector) {
		float[] f;
		f = new float[vector.size()];
		for (int i = 0; i < vector.size(); i++) {
			f[i] = vector.get(i);
		}
		return f;
	}
	ArrayList<ModelPart> parts;
	ArrayList<Float> v;
	FloatBuffer vertexBuffer;
	ArrayList<Float> vn;

	ArrayList<Float> vt;

	public Model(ArrayList<Float> v, ArrayList<Float> vn, ArrayList<Float> vt,
			ArrayList<ModelPart> parts) {
		super();
		this.v = v;
		this.vn = vn;
		this.vt = vt;
		this.parts = parts;
	}

	public void buildVertexBuffer() {
		ByteBuffer vBuf = ByteBuffer.allocateDirect(v.size() * 4);
		vBuf.order(ByteOrder.nativeOrder());
		vertexBuffer = vBuf.asFloatBuffer();
		vertexBuffer.put(toPrimitiveArrayF(v));
		vertexBuffer.position(0);
	}

	/**
	 * Draw the model with the given GL context
	 * 
	 * @param gl
	 */
	public void draw(GL10 gl) {
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		for (int i = 0; i < parts.size(); i++) {
			ModelPart t = parts.get(i);
			Material m = t.getMaterial();
			if (m != null) {
				FloatBuffer a = m.getAmbientColorBuffer();
				FloatBuffer d = m.getDiffuseColorBuffer();
				FloatBuffer s = m.getSpecularColorBuffer();
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, a);
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, s);
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, d);
			}
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, t.getNormalBuffer());
			gl.glDrawElements(GL10.GL_TRIANGLES, t.getFacesCount(),
					GL10.GL_UNSIGNED_SHORT, t.getFaceBuffer());
			// gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			// gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		}
	}

	@Override
	public String toString() {
		String str = new String();
		str += "Number of parts: " + parts.size();
		str += "\nNumber of vertexes: " + v.size();
		str += "\nNumber of vns: " + vn.size();
		str += "\nNumber of vts: " + vt.size();
		str += "\n/////////////////////////\n";
		for (int i = 0; i < parts.size(); i++) {
			str += "Part " + i + '\n';
			str += parts.get(i).toString();
			str += "\n/////////////////////////";
		}
		return str;
	}
}
