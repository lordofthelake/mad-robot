package com.madrobot.reflect;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for primitives.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2.1
 */
public final class PrimitiveUtils {
    private final static Map BOX = new HashMap();
    private final static Map NAMED_PRIMITIVE = new HashMap();
    private final static Map REPRESENTING_CHAR = new HashMap();
    private final static Map UNBOX = new HashMap();
    
    static {
        final Class[][] boxing = new Class[][]{
            { Byte.TYPE, Byte.class},
            { Character.TYPE, Character.class},
            { Short.TYPE, Short.class},
            { Integer.TYPE, Integer.class},
            { Long.TYPE, Long.class},
            { Float.TYPE, Float.class},
            { Double.TYPE, Double.class},
            { Boolean.TYPE, Boolean.class},
            { Void.TYPE, Void.class},
        };
        final Character[] representingChars = { 
            new Character('B'), 
            new Character('C'), 
            new Character('S'), 
            new Character('I'), 
            new Character('J'), 
            new Character('F'), 
            new Character('D'), 
            new Character('Z'),
            null
         };
        for (int i = 0; i < boxing.length; i++) {
            final Class primitiveType = boxing[i][0];
            final Class boxedType = boxing[i][1];
            BOX.put(primitiveType, boxedType);
            UNBOX.put(boxedType, primitiveType);
            NAMED_PRIMITIVE.put(primitiveType.getName(), primitiveType);
            REPRESENTING_CHAR.put(primitiveType, representingChars[i]);
        }
    }
    
    
    /**
     * Get the boxed type for a primitive.
     * 
     * @param type the primitive type
     * @return the boxed type or null
     */
    static public Class box(final Class type) {
        return (Class)BOX.get(type);
    }

	/**
     * Check for a boxed type.
     * 
     * @param type the type to check
     * @return <code>true</code> if the type is boxed
     * @since 1.4
     */
    static public boolean isBoxed(final Class type) {
        return UNBOX.containsKey(type);
    }

	/**
     * Get the primitive type by name.
     * 
     * @param name the name of the type
     * @return the Java type or <code>null</code>
     * @since 1.4
     */
    static public Class primitiveType(final String name) {
        return (Class)NAMED_PRIMITIVE.get(name);
    }

	/**
     * Get the representing character of a primitive type.
     * 
     * @param type the primitive type
     * @return the representing character or 0
     * @since 1.4
     */
    static public char representingChar(final Class type) {
        Character ch = (Character)REPRESENTING_CHAR.get(type);
        return ch == null ? 0 : ch.charValue();
    }


    private static byte[] toByta(final long data) {
		return new byte[] { (byte) (data >> 56 & 0xff),
				(byte) (data >> 48 & 0xff), (byte) (data >> 40 & 0xff),
				(byte) (data >> 32 & 0xff), (byte) (data >> 24 & 0xff),
				(byte) (data >> 16 & 0xff), (byte) (data >> 8 & 0xff),
				(byte) (data >> 0 & 0xff), };
	}
    
    public static byte[] toByteArray(final double data) {
		return toByta(Double.doubleToRawLongBits(data));
	}

    public static double toDouble(final byte[] data) {
		if (data == null || data.length != 8)
			return 0x0;
		// ---------- simple:
		return Double.longBitsToDouble(toLong(data));
	}

    private static long toLong(final byte[] data) {
		if (data == null || data.length != 8)
			return 0x0;
		// ----------
		return (long) (0xff & data[0]) << 56 | (long) (0xff & data[1]) << 48
				| (long) (0xff & data[2]) << 40 | (long) (0xff & data[3]) << 32
				| (long) (0xff & data[4]) << 24 | (long) (0xff & data[5]) << 16
				| (long) (0xff & data[6]) << 8 | (long) (0xff & data[7]) << 0;
	}

    /**
     * Get the primitive type for a boxed one.
     * 
     * @param type the boxed type
     * @return the primitive type or null
     */
    static public Class unbox(final Class type) {
        return (Class)UNBOX.get(type);
    }
}
