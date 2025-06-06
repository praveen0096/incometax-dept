package io.mosip.credential.exception;


/**
 * The Class FieldNotFoundException.
 */
public class FieldNotFoundException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instantiates a new field not found exception.
	 */
	public FieldNotFoundException() {
		super();
	}
	
	/**
	 * Instantiates a new field not found exception.
	 *
	 * @param errorMessage the error message
	 */
	public FieldNotFoundException(String errorMessage) {
		super(PlatformErrorMessages.PRT_SYS_NO_SUCH_FIELD_EXCEPTION.getCode() + EMPTY_SPACE, errorMessage);
	}

	/**
	 * Instantiates a new field not found exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public FieldNotFoundException(String message, Throwable cause) {
		super(PlatformErrorMessages.PRT_SYS_NO_SUCH_FIELD_EXCEPTION.getCode() + EMPTY_SPACE, message, cause);
	}

}
