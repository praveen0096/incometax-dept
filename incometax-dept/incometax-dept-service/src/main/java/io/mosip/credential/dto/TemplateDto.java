package io.mosip.credential.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Response Dto for Template details
 * 
 * @author Alok
 * @since 1.0.0
 */
@Data
public class TemplateDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String description;
	private String fileFormatCode;
	private String model;
	private String fileText;
	private String moduleId;
	private String moduleName;
	private String templateTypeCode;
	private String langCode;
	private Boolean isActive;
	
}
