package Service.impl;

import io.mosip.credential.service.CredentialService;
import io.mosip.credential.service.impl.CredentialServiceImpl;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.credential.constant.QrVersion;
import io.mosip.credential.dto.IdResponseDTO1;
import io.mosip.credential.dto.ResponseDTO;
import io.mosip.credential.model.EventModel;
import io.mosip.credential.service.CredentialRestClientService;
//import io.mosip.print.service.UinCardGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import io.mosip.credential.spi.CbeffUtil;
import io.mosip.credential.spi.QrCodeGenerator;
import io.mosip.credential.util.CryptoCoreUtil;
import io.mosip.credential.util.JsonUtil;
//import io.mosip.print.util.TemplateGenerator;
import io.mosip.credential.util.Utilities;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest(classes = TestBootApplication.class)
@RunWith(SpringRunner.class)
public class PrintServiceTest {

	@InjectMocks
	private CredentialServiceImpl credentialService;

	@Mock
	private Environment env;

	@Mock
	private CredentialRestClientService<Object> restClientService;

	/** The id response. */
	private IdResponseDTO1 idResponse = new IdResponseDTO1();

	/** The response. */
	private ResponseDTO response = new ResponseDTO();

	/** The template generator. */
	//@Mock
	//private TemplateGenerator templateGenerator;

	/** The uin card generator. */
	//@Mock
	//private UinCardGenerator<byte[]> uinCardGenerator;

	/** The utility. */
	@Mock
	private Utilities utility;

	@Mock
	private QrCodeGenerator<QrVersion> qrCodeGenerator;

	@Mock
	private CryptoCoreUtil cryptoCoreUtil;

	@Mock
	private CryptoUtil cryptoUtil;

	@Mock
	private CbeffUtil cbeffutil;

	private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	private EventModel eventModel;

	private String decryptedJson = "json";


	@Before
	public void setUp() throws Exception {

		when(env.getProperty("mosip.print.datetime.pattern")).thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		byte[] qrcode = "QRCODE GENERATED".getBytes();
		Mockito.when(qrCodeGenerator.generateQrCode(any(), any())).thenReturn(qrcode);
		ClassLoader classLoader = getClass().getClassLoader();
		File printTextFile = new File(classLoader.getResource("printTextFileJson.json").getFile());
		File mappingFile = new File(classLoader.getResource("RegistrationProcessorIdentity.json").getFile());
		File credentialFile = new File(classLoader.getResource("credential.json").getFile());

		String mappingFileJson = FileUtils.readFileToString(mappingFile, StandardCharsets.UTF_8);
		String printTextFileJson = FileUtils.readFileToString(printTextFile, StandardCharsets.UTF_8);
		String credentialFileJson = FileUtils.readFileToString(credentialFile, StandardCharsets.UTF_8);
		/*
		 * JSONObject crednetialJson = new JSONObject(credentialFileJson); String
		 * credential = crednetialJson.getString("credential"); String protectionKey =
		 * crednetialJson.getString("protectionKey"); Event event = new Event();
		 * event.setDataShareUri(null); event.setData(data); eventModel.setEvent(event);
		 */
		eventModel = JsonUtil.readValue(credentialFileJson, EventModel.class);
		// PowerMockito.when(Utilities.class, "getJson", "", any()).thenReturn(value);

		Mockito.when(utility.getConfigServerFileStorageURL()).thenReturn("configUrl");
		Mockito.when(utility.getGetRegProcessorIdentityJson()).thenReturn(mappingFileJson);
		Mockito.when(utility.getRegistrationProcessorPrintTextFile()).thenReturn(printTextFileJson);
		Mockito.when(cryptoCoreUtil.decrypt(any())).thenReturn(decryptedJson);

	}

	@Test
	@Ignore
	public void testQrcodegeneration() throws Exception {
		// JSONObject jsonObject = new JSONObject(decryptedJson1);
		// Mockito.when(printServiceImpl.decryptAttribute(any(), any(),
		// any())).thenReturn(jsonObject);
		credentialService.generateCard(eventModel);
	}

}
