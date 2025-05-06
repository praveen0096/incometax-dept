package io.mosip.credential.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.credential.constant.ApiName;
import io.mosip.credential.dto.DataShare;
import io.mosip.credential.dto.DataShareResponseDto;
import io.mosip.credential.dto.ErrorDTO;
import io.mosip.credential.exception.ApiNotAccessibleException;
import io.mosip.credential.exception.DataShareException;
import io.mosip.credential.logger.PrintLogger;
import io.mosip.credential.service.CredentialRestClientService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataShareUtil {

	@Autowired
	CredentialRestClientService<Object> restUtil;

	@Autowired
	private ObjectMapper mapper;
	
	
	Logger printLogger = PrintLogger.getLogger(DataShareUtil.class);

	private static final String GET_DATA = "getDataShare";
	
	private static final String DATASHARE = "DataShareUtil";
	
	private static final String CREDENTIALFILE = "credentialfile";

	public DataShare getDataShare(byte[] data, String policyId, String partnerId)
			throws IOException, DataShareException, ApiNotAccessibleException {
		try {

			LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
			map.add("name", CREDENTIALFILE);
			map.add("filename", CREDENTIALFILE);

			ByteArrayResource contentsAsResource = new ByteArrayResource(data) {
				@Override
				public String getFilename() {
					return CREDENTIALFILE;
				}
			};
			map.add("file", contentsAsResource);
		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(policyId);
		pathsegments.add(partnerId);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(
				map, headers);
		String responseString = (String) restUtil.postApi(ApiName.CREATEDATASHARE, pathsegments, "", "", requestEntity,
				String.class);
		DataShareResponseDto responseObject = mapper.readValue(responseString, DataShareResponseDto.class);
		if (responseObject == null) {
			throw new DataShareException();
		}
		if (responseObject != null && responseObject.getErrors() != null && !responseObject.getErrors().isEmpty()) {
			ErrorDTO error = responseObject.getErrors().get(0);

			throw new DataShareException();
		} else {

			return responseObject.getDataShare();
		}
		} catch (Exception e) {

			if (e.getCause() instanceof HttpClientErrorException) {

				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				throw new ApiNotAccessibleException(
						httpClientException.getResponseBodyAsString());
			} else if (e.getCause() instanceof HttpServerErrorException) {
				HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();
				throw new ApiNotAccessibleException(httpServerException.getResponseBodyAsString());
			} else {
				throw new DataShareException(e);
			}

		}

	}
}
