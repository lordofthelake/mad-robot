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
package com.madrobot.reflect;

import java.io.Serializable;


/**
 * A basic immutable Object pair.
 * 
 * <p>
 * #ThreadSafe# if the objects are threadsafe
 * </p>
 * 
 * @since Lang 3.0
 * @author Matt Benson
 * @version $Id: Pair.java 967237 2010-07-23 20:08:57Z mbenson $
 */
final class Pair<L, R> implements Serializable {
	/** Serialization version */
	private static final long serialVersionUID = 4954918890077093841L;

	/**
	 * Static creation method for a Pair<L, R>.
	 * 
	 * @param <L>
	 * @param <R>
	 * @param left
	 * @param right
	 * @return Pair<L, R>(left, right)
	 */
	public static <L, R> Pair<L, R> of(L left, R right) {
		return new Pair<L, R>(left, right);
	}

	/** Left object */
	public final L left;

	/** Right object */
	public final R right;

	/**
	 * Create a new Pair instance.
	 * 
	 * @param left
	 * @param right
	 */
	public Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == this){
			return true;
		}
		if(obj instanceof Pair<?, ?> == false){
			return false;
		}
		Pair<?, ?> other = (Pair<?, ?>) obj;
		return ObjectUtils.equals(left, other.left) && ObjectUtils.equals(right, other.right);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(left).append(right).toHashCode();
	}

	/**
	 * Returns a String representation of the Pair in the form: (L,R)
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		builder.append(left);
		builder.append(",");
		builder.append(right);
		builder.append(")");
		return builder.toString();
	}
}
