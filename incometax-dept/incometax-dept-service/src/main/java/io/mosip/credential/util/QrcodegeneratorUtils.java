package io.mosip.credential.util;

import io.mosip.credential.constant.QrVersion;
import io.mosip.credential.constant.QrcodeExceptionConstants;
import io.mosip.credential.exception.InvalidInputException;

/**
 * Utils class for QR code generator
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public class QrcodegeneratorUtils {
	/**
	 * Constructor for this class
	 */
	private QrcodegeneratorUtils() {

	}

	/**
	 * Verify the input send by user
	 * 
	 * @param data    data send by user
	 * @param version {@link QrVersion} send by user
	 */
	public static void verifyInput(String data, QrVersion version) {
		if (data == null) {
			throw new NullPointerException(
            );
		} else if (data.trim().isEmpty()) {
			throw new InvalidInputException(QrcodeExceptionConstants.INVALID_INPUT_DATA_EMPTY.getErrorCode(),
					QrcodeExceptionConstants.INVALID_INPUT_DATA_EMPTY.getErrorMessage());
		} else if (version == null) {
			throw new NullPointerException(
            );
		}
	}
}
