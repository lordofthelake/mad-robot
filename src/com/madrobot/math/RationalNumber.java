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
package com.madrobot.math;

import java.text.NumberFormat;

public class RationalNumber extends Number
{
	private static final NumberFormat nf = NumberFormat.getInstance();

	private static final long serialVersionUID = -1;
	public static final RationalNumber factoryMethod(long n, long d)
	{
		// safer than constructor - handles values outside min/max range.
		// also does some simple finding of common denominators.

		if ((n > Integer.MAX_VALUE) || (n < Integer.MIN_VALUE)
				|| (d > Integer.MAX_VALUE) || (d < Integer.MIN_VALUE))
		{
			while (((n > Integer.MAX_VALUE) || (n < Integer.MIN_VALUE)
					|| (d > Integer.MAX_VALUE) || (d < Integer.MIN_VALUE))
					&& (Math.abs(n) > 1) && (Math.abs(d) > 1))
			{
				// brutal, inprecise truncation =(
				// use the sign-preserving right shift operator.
				n >>= 1;
				d >>= 1;
			}

			if (d == 0){
				throw new NumberFormatException("Invalid value, numerator: "
						+ n + ", divisor: " + d);
			}
		}

		long gcd = gcd(n, d);
		d = d / gcd;
		n = n / gcd;

		return new RationalNumber((int) n, (int) d);
	}

	/**
	 * Return the greatest common divisor
	 */
	private static long gcd(long a, long b)
	{

		if (b == 0){
			return a;
		} else{
			return gcd(b, a % b);
		}
	}

	public final int divisor;

	public final int numerator;

	public RationalNumber(int numerator, int divisor)
	{
		this.numerator = numerator;
		this.divisor = divisor;
	}

	@Override
	public double doubleValue()
	{
		return (double) numerator / (double) divisor;
	}

	@Override
	public float floatValue()
	{
		return (float) numerator / (float) divisor;
	}

	@Override
	public int intValue()
	{
		return numerator / divisor;
	}

	public boolean isValid()
	{
		return divisor != 0;
	}

	@Override
	public long longValue()
	{
		return (long) numerator / (long) divisor;
	}

	public RationalNumber negate()
	{
		return new RationalNumber(-numerator, divisor);
	}

	public String toDisplayString()
	{
		if ((numerator % divisor) == 0){
			return "" + (numerator / divisor);
		}
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
		return nf.format((double) numerator / (double) divisor);
	}

	@Override
	public String toString()
	{
		if (divisor == 0){
			return "Invalid rational (" + numerator + "/" + divisor + ")";
		}
		if ((numerator % divisor) == 0){
			return nf.format(numerator / divisor);
		}
		return numerator + "/" + divisor + " ("
				+ nf.format((double) numerator / divisor) + ")";
	}
}
