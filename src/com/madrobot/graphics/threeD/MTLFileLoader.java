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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Material File loader (Wavefront format material file)
 * @author elton.kent
 *
 */
public final class MTLFileLoader {

	public static ArrayList<Material> loadMTL(String file) {
		BufferedReader reader = null;
		ArrayList<Material> materials = new ArrayList<Material>();
		String line;
		Material currentMtl = null;
		try { // try to open file
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
		} catch (IOException e) {
		}
		if (reader != null) {
			try {// try to read lines of the file
				while ((line = reader.readLine()) != null) {
					if (line.startsWith("newmtl")) {
						if (currentMtl != null)
							materials.add(currentMtl);
						String mtName = line.split("[ ]+", 2)[1];
						currentMtl = new Material(mtName);
					} else if (line.startsWith("Ka")) {
						String[] str = line.split("[ ]+");
						currentMtl.setAmbientColor(Float.parseFloat(str[1]),
								Float.parseFloat(str[2]),
								Float.parseFloat(str[3]));
					} else if (line.startsWith("Kd")) {
						String[] str = line.split("[ ]+");
						currentMtl.setDiffuseColor(Float.parseFloat(str[1]),
								Float.parseFloat(str[2]),
								Float.parseFloat(str[3]));
					} else if (line.startsWith("Ks")) {
						String[] str = line.split("[ ]+");
						currentMtl.setSpecularColor(Float.parseFloat(str[1]),
								Float.parseFloat(str[2]),
								Float.parseFloat(str[3]));
					} else if (line.startsWith("Tr") || line.startsWith("d")) {
						String[] str = line.split("[ ]+");
						currentMtl.setAlpha(Float.parseFloat(str[1]));
					} else if (line.startsWith("Ns")) {
						String[] str = line.split("[ ]+");
						currentMtl.setShine(Float.parseFloat(str[1]));
					} else if (line.startsWith("illum")) {
						String[] str = line.split("[ ]+");
						currentMtl.setIllum(Integer.parseInt(str[1]));
					} else if (line.startsWith("map_Ka")) {
						String[] str = line.split("[ ]+");
						currentMtl.setTextureFile(str[1]);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}
		return materials;
	}
}
