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
package com.madrobot;

import android.app.Activity;
import android.os.Bundle;

import com.madrobot.di.wizard.xml.XMLWizard;

public class TestActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Person person=new Person();
		person.setAge("12");
		person.setName("elton");
		Asset ass=new Asset("test","test2");
		ass.setCar("carrol");
		ass.setBike("sdfs");
		person.setAsset(ass);
		
		
		XMLWizard xx=new XMLWizard();
		xx.alias("pekkrson", Person.class);
		xx.alias("Ass", Asset.class);
		String xml=xx.toXML(person);
		System.out.println("XML->"+xml);

		Person pe=(Person) xx.fromXML(xml);
		System.out.println(pe.getName());
		System.out.println(pe.list[0].getBike());
	}
}
