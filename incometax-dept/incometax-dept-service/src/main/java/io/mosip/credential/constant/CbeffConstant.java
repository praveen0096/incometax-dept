/**
 * 
 */
package io.mosip.credential.constant;

/**
 * @author Ramadurai Pandian
 *
 *         A Constant Class to hold all the default identifier values
 */
public class CbeffConstant {

	public static final long FORMAT_OWNER = 257;

	// Format Type

	public static final long FORMAT_TYPE_IRIS = 9;

	public static final long FORMAT_TYPE_FACE = 8;

	public static final long FORMAT_TYPE_FINGER = 7;

	public static final long FORMAT_TYPE_FINGER_MINUTIAE = 2;

	// Format Identifier

	public static final int FINGER_FORMAT_IDENTIFIER = 0x46495200;

	public static final int IRIS_FORMAT_IDENTIFIER = 0x49495200;

	public static final int FACE_FORMAT_IDENTIFIER = 0x46495200;

	// TODO Actual face identifier waiting for Face ISO image
	// public static final int FACE_FORMAT_IDENTIFIER = 0x46414300;

}
