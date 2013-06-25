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
package com.madrobot.device;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.Contacts;
import android.provider.ContactsContract;

import com.madrobot.beans.IntrospectionException;
import com.madrobot.db.orm.BeanGenerator;

/**
 * Utility for accessing phone contacts
 * 
 */
public final class ContactUtils {

	public static List<Contact> fetchContacts(Context context) {
		ContentResolver cr = context.getContentResolver();
		Cursor namesCursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		try{
			return BeanGenerator.toBeanList(namesCursor, Contact.class);
		} catch(IllegalArgumentException e){
			e.printStackTrace();
		} catch(IllegalAccessException e){
			e.printStackTrace();
		} catch(InstantiationException e){
			e.printStackTrace();
		} catch(IntrospectionException e){
			e.printStackTrace();
		} catch(InvocationTargetException e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Save a number to the contacts
	 * 
	 * @param context
	 *            Application context
	 * @param number
	 *            Phone number to save
	 */
	public static void saveToContact(Context context, String number) {
		Intent intent = new Intent(Contacts.Intents.Insert.ACTION, Contacts.People.CONTENT_URI);
		intent.putExtra(Contacts.Intents.Insert.PHONE, number);
		context.startActivity(intent);
	}
}
