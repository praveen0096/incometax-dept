package io.mosip.credential.service.impl;


import io.mosip.credential.service.CredentialService;
import io.mosip.credential.constant.CredentialStatusConstant;
import io.mosip.credential.constant.QrVersion;
import io.mosip.credential.dto.CryptoWithPinRequestDto;
import io.mosip.credential.dto.CryptoWithPinResponseDto;
import io.mosip.credential.exception.CryptoManagerException;
import io.mosip.credential.exception.PlatformErrorMessages;
import io.mosip.credential.util.*;
import io.mosip.vercred.CredentialsVerifier;
import io.mosip.vercred.exception.ProofDocumentNotFoundException;
import io.mosip.vercred.exception.ProofTypeNotFoundException;
import io.mosip.vercred.exception.PubicKeyNotFoundException;
import io.mosip.vercred.exception.UnknownException;
import io.mosip.credential.logger.LogDescription;
import io.mosip.credential.logger.PrintLogger;
import io.mosip.credential.model.CredentialStatusEvent;
import io.mosip.credential.model.EventModel;
import io.mosip.credential.model.StatusEvent;
import io.mosip.credential.entity.CredentialEntity;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import io.mosip.credential.repository.CredentialRepository;
import io.mosip.credential.spi.CbeffUtil;
import io.mosip.credential.spi.QrCodeGenerator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CredentialServiceImpl implements CredentialService {
    @Autowired
    private CredentialRepository credentialRepository;



    private static int passwordLengthPerAttribute = 4;
    /**
     * The Constant FILE_SEPARATOR.
     */
    public static final String FILE_SEPARATOR = File.separator;
    /**
     * The Constant VID_CREATE_ID.
     */
    public static final String VID_CREATE_ID = "registration.processor.id.repo.generate";
    /**
     * The Constant REG_PROC_APPLICATION_VERSION.
     */
    public static final String REG_PROC_APPLICATION_VERSION = "registration.processor.id.repo.vidVersion";
    /**
     * The Constant DATETIME_PATTERN.
     */
    public static final String DATETIME_PATTERN = "mosip.print.datetime.pattern";
    public static final String VID_TYPE = "registration.processor.id.repo.vidType";
    /**
     * The Constant VALUE.
     */
    private static final String VALUE = "value";
    /**
     * The Constant UIN_CARD_TEMPLATE.
     */
    private static final String UIN_CARD_TEMPLATE = "RPR_UIN_CARD_TEMPLATE";
    private static final String UIN_CARD_EMAIL_SUB = "RPR_UIN_CARD_EMAIL_SUB";
    private static final String UIN_CARD_EMAIL = "RPR_UIN_CARD_EMAIL";
    private static final String ACCT_EMAIL_SUB = "RPR_ACCT_NOTIFY_EMAIL_SUB";
    private static final String ACCT_EMAIL = "RPR_ACCT_NOTIFY_EMAIL";
    private static final String BEARER = "Bearer ";
    /**
     * The Constant FACE.
     */
    private static final String FACE = "Face";
    /**
     * The Constant UIN_TEXT_FILE.
     */
    private static final String UIN_TEXT_FILE = "textFile";
    /**
     * The Constant APPLICANT_PHOTO.
     */
    private static final String APPLICANT_PHOTO = "ApplicantPhoto";
    /**
     * The Constant QRCODE.
     */
    private static final String QRCODE = "QrCode";

    /**
     * The Constant UINCARDPASSWORD.
     */
    private static final String UINCARDPASSWORD = "mosip.print.service.uincard.password";

    @Autowired
    private CryptoUtil cryptoUtil;
    /**
     * The print logger.
     */
    Logger printLogger = PrintLogger.getLogger(CredentialServiceImpl.class);
    private final String topic = "CREDENTIAL_STATUS_UPDATE";
    @Autowired
    private WebSubSubscriptionHelper webSubSubscriptionHelper;
    @Autowired
    private DataShareUtil dataShareUtil;
    @Autowired
    private RestApiClient restApiClient;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CryptoCoreUtil cryptoCoreUtil;
    /**
     * The core audit request builder.
     */
    @Autowired
    private AuditLogRequestBuilder auditLogRequestBuilder;
    /**
     * The template generator.
     */
    //@Autowired
    //private TemplateGenerator templateGenerator;
    /**
     * The utilities.
     */
    @Autowired
    private Utilities utilities;
    /**
     * The uin card generator.
     */
    //@Autowired
    //private UinCardGenerator<byte[]> uinCardGenerator;
    /**
     * The qr code generator.
     */
    @Autowired
    private QrCodeGenerator<QrVersion> qrCodeGenerator;
    /**
     * The cbeffutil.
     */
   @Autowired
   private CbeffUtil cbeffutil;
    /**
     * The env.
     */
    @Autowired
    private Environment env;
    @Autowired
    private CredentialsVerifier credentialsVerifier;
    //@Autowired
    //private CredentialRepository credentialRepository;

    @Value("${mosip.datashare.partner.id}")
    private String partnerId;
    @Value("${mosip.datashare.policy.id}")
    private String policyId;
    @Value("${mosip.template-language}")
    private String templateLang;
    @Value("#{T(java.util.Arrays).asList('${mosip.supported-languages:}')}")
    private List<String> supportedLang;
    @Value("${mosip.print.verify.credentials.flag:false}")
    private boolean verifyCredentialsFlag;
    @Value("${token.request.clientId}")
    private String clientId;
    @Value("${mosip.send.uin.email.attachment.enabled:false}")
    private Boolean emailUINEnabled;
    @Value("${mosip.print.service.uincard.pdf.password.enable:false}")
    private boolean isPasswordProtected;
    @Value("${mosip.send.uin.default-emailIds}")
    private String defaultEmailIds;
    @Value("${mosip.idschema.attribute.email:email}")
    private String emailAttribute;
    @Value("${mosip.print.service.mpesa.enabled:false}")
    private Boolean isMpesaEnabled;
    @Value("${mosip.print.service.mpesa.account.creation.url}")
    private String mpesaAccountCreationUrl;
    @Value("${mosip.print.service.mpesa.agent.login.url}")
    private String mpesaAgentLoginUrl;
    @Value("${mosip.print.service.mpesa.account.deposit.url}")
    private String mpesaAccountDepositUrl;
    @Value("${mosip.print.service.mpesa.agent.username}")
    private String mpesaAgentUserName;
    @Value("${mosip.print.service.mpesa.agent.password}")
    private String mpesaAgentPassword;
    @Value("${mosip.print.service.mpesa.password.length}")
    private int mpesaPasswordLength;
    @Value("${mosip.print.service.mpesa.account.deposit.amount}")
    private int mpesaDepositAmount;
    @Value("${mosip.print.service.id}")
    private String serviceId;
    @Value("${mosip.print.service.mpesa.subs.username.suffix}")
    private String subsUserNameSuffix;
    @Value("${mosip.print.service.mpesa.subs.password}")
    private String subsPassword;

    @Autowired
    private NotificationUtil notificationUtil;

    public boolean generateCard(EventModel eventModel) {
        boolean isPrinted = false;
        try {
            printStatusUpdate(eventModel.getEvent().getTransactionId(), CredentialStatusConstant.RECEIVED.name(), null);
            String credential = getCredential(eventModel);
            String decodedCredential = decryptCredential(credential);
            printLogger.debug("VC is printed security valuation: {}", decodedCredential);

            if (!hasPrintCredentialVerified(eventModel, decodedCredential)) {
                return false;
            }

            CredentialEntity credentialEntity = new CredentialEntity();
            credentialEntity.setId(eventModel.getEvent().getTransactionId());

            // Parse credential JSON
            org.json.JSONObject credentialJson = new org.json.JSONObject(decodedCredential);
            org.json.JSONObject credentialSubject = credentialJson.getJSONObject("credentialSubject");

            //credentialEntity.setDateOfBirth(credentialSubject.optString("DateOfBirth"));
            String dob = (String) credentialSubject.get("dateOfBirth");
            credentialEntity.setDateOfBirth(dob);
            credentialEntity.setIdType("TAN");
            String idNumber = credentialSubject.optString("idNumber");
            // Validate that idNumber is exactly 9 digits
            if (idNumber == null || !idNumber.matches("\\d{9}")) {
                // Log warning and generate a random 9-digit fallback
                printLogger.warn("Invalid or missing idNumber: {}. Generating a random 9-digit number.", idNumber);
                idNumber = String.format("%09d", new Random().nextInt(1_000_000_000));
            }
            credentialEntity.setIdNumber(idNumber);
            if (credentialSubject != null && credentialSubject.has("issuedOn") && credentialSubject.get("issuedOn") != null) {
                credentialEntity.setIssuedOn(LocalDateTime.parse(credentialSubject.getString("issuedOn")));
            } else {
                credentialEntity.setIssuedOn(LocalDateTime.now());
            }
            if (credentialSubject.has("expiredOn")) {
                credentialEntity.setExpiredOn(LocalDateTime.parse(credentialSubject.getString("expiredOn")));
            } else {
                credentialEntity.setExpiredOn(LocalDateTime.now().plusYears(1));
            }
            //credentialEntity.setTokenId(credentialSubject.optString("tokenId"));
            String tokenId = credentialSubject.optString("tokenId", null);
            if (tokenId == null || tokenId.trim().isEmpty()) {
                printLogger.warn("Missing or empty tokenId in credentialSubject. Generating fallback tokenId.");
                tokenId = UUID.randomUUID().toString(); // or handle however is appropriate
            }
            credentialEntity.setTokenId(tokenId);
            credentialEntity.setStatusCode("ACTIVE");

            credentialEntity.setCreatedBy("SYSTEM");
            credentialEntity.setCreateDateTime(LocalDateTime.now());
            credentialEntity.setUpdatedBy("MOSIP_SYSTEM");
            credentialEntity.setUpdateDateTime(LocalDateTime.now());
            credentialEntity.setIsDeleted(false);

            // Handle biometric data if available
           // if (credentialSubject.has("biometricData")) {
               // String bioDataString = credentialSubject.getString("biometricData");
              //  credentialEntity.setBiometricData(Base64.getDecoder().decode(bioDataString));
         //   }

            // Save the credential entity to the database
            credentialRepository.save(credentialEntity);
            printLogger.info("Credential successfully saved for transaction ID: {}", eventModel.getEvent().getTransactionId());

            // Generate and store document
            byte[] pdfbytes = getDocuments(decodedCredential,
                    eventModel.getEvent().getData().get("credentialType").toString(),
                    eventModel.getEvent().getData().get("protectionKey").toString(),
                    eventModel.getEvent().getTransactionId(), "UIN", isPasswordProtected,
                    eventModel.getEvent().getId(),
                    (eventModel.getEvent().getData().get("registrationId") == null ?
                            null : eventModel.getEvent().getData().get("registrationId").toString()))
                    .get("uinPdf");

            isPrinted = true;
        } catch (Exception e) {
            printLogger.error("Error generating credential: {}", e.getMessage(), e);
            isPrinted = false;
        }
        return isPrinted;
    }


    /**
     * Decrypts print credential using MOSIP's decryption logic in ref. impl.
     * Print partner system has to use their own decryption logic to decrypt the credential.
     *
     * @param credential
     * @return
     */
    private String decryptCredential(String credential) {
        return cryptoCoreUtil.decrypt(credential);
    }

    /**
     * Verifies Print credentials using credential verifier.
     *
     * @param eventModel
     * @param decodedCredential
     * @return
     */
    private boolean hasPrintCredentialVerified(EventModel eventModel, String decodedCredential) {

        if (verifyCredentialsFlag) {
            printLogger.info("Configured received credentials to be verified. Flag {}", verifyCredentialsFlag);
            try {
                boolean verified = credentialsVerifier.verifyPrintCredentials(decodedCredential);
                if (!verified) {
                    printLogger.error("Received Credentials failed in verifiable credential verify method. So, the credentials will not be printed." +
                            " Id: {}, Transaction Id: {}", eventModel.getEvent().getId(), eventModel.getEvent().getTransactionId());
                    return false;
                }
            } catch (ProofDocumentNotFoundException | ProofTypeNotFoundException e) {
                printLogger.error("Proof document is not available in the received credentials." +
                        " Id: {}, Transaction Id: {}", eventModel.getEvent().getId(), eventModel.getEvent().getTransactionId());
                return false;
            } catch (UnknownException | PubicKeyNotFoundException e) {
                printLogger.error("Received Credentials failed in verifiable credential verify method. So, the credentials will not be printed." +
                        " Id: {}, Transaction Id: {}", eventModel.getEvent().getId(), eventModel.getEvent().getTransactionId());
                return false;
            }
        }
        return true;
    }

    /**
     * Fetch credential from the event if not using datashare URL.
     *
     * @param eventModel
     * @return
     * @throws Exception
     */
    private String getCredential(EventModel eventModel) throws Exception {
        String credential;
        if (eventModel.getEvent().getDataShareUri() == null || eventModel.getEvent().getDataShareUri().isEmpty()) {
            credential = eventModel.getEvent().getData().get("credential").toString();
        } else {
            String dataShareUrl = eventModel.getEvent().getDataShareUri();
            URI dataShareUri = URI.create(dataShareUrl);
            credential = restApiClient.getApi(dataShareUri, String.class);
        }
        return credential;
    }

    /*
     * (non-Javadoc)
     *
     * @see io.mosip.print.service.PrintService#
     * getDocuments(io.mosip.registration.processor.core.constant.IdType,
     * java.lang.String, java.lang.String, boolean)
     */
    private Map<String, byte[]> getDocuments(String credential, String credentialType, String encryptionPin,
                                                    String requestId,
                                                    String cardType,
                                                    boolean isPasswordProtected, String refId, String registrationId) throws ParseException, JSONException {
        printLogger.debug("CredentialServiceImpl::getDocuments()::entry");
        String credentialSubject;
        Map<String, byte[]> byteMap = new HashMap<>();
        String uin = null, residentEmailId = null;
        LogDescription description = new LogDescription();
        String password = null;
        boolean isPhotoSet = false;
        String individualBio = null;
        Map<String, Object> attributes = new LinkedHashMap<>();
        boolean isTransactionSuccessful = false;
        String template = UIN_CARD_TEMPLATE;
        byte[] pdfBytes = null;


        credentialSubject = getCrdentialSubject(credential);
        org.json.JSONObject credentialSubjectJson = new org.json.JSONObject(credentialSubject);
        org.json.JSONObject decryptedJson = decryptAttribute(credentialSubjectJson, encryptionPin, credential);
        return byteMap;
    }

    private String getCrdentialSubject(String credential) throws JSONException {
        org.json.JSONObject jsonObject = null;
        try {
            jsonObject = new org.json.JSONObject(credential);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject.get("credentialSubject").toString();
    }

    private void printStatusUpdate(String requestId, String status, String datashareUrl) {

        CredentialStatusEvent creEvent = new CredentialStatusEvent();
        LocalDateTime currentDtime = DateUtils.getUTCCurrentDateTime();
        StatusEvent sEvent = new StatusEvent();
        sEvent.setId(UUID.randomUUID().toString());
        sEvent.setRequestId(requestId);
        sEvent.setStatus(status);
        if (datashareUrl != null) {
            sEvent.setUrl(datashareUrl);
        }
        sEvent.setTimestamp(Timestamp.valueOf(currentDtime).toString());
        creEvent.setPublishedOn(new DateTime().toString());
        creEvent.setPublisher("CREDENTIAL_SERVICE");
        creEvent.setTopic(topic);
        creEvent.setEvent(sEvent);
        webSubSubscriptionHelper.printStatusUpdateEvent(topic, creEvent);
    }
    public org.json.JSONObject decryptAttribute(org.json.JSONObject data, String encryptionPin, String credential)
            throws ParseException, JSONException {

        // org.json.JSONObject jsonObj = new org.json.JSONObject(credential);
        JSONParser parser = new JSONParser(); // this needs the "json-simple" library
        Object obj = parser.parse(credential);
        JSONObject jsonObj = (JSONObject) obj;

        JSONArray jsonArray = (JSONArray) jsonObj.get("protectedAttributes");
        if (Objects.isNull(jsonArray)) {
            return data;
        }
        for (Object str : jsonArray) {

            CryptoWithPinRequestDto cryptoWithPinRequestDto = new CryptoWithPinRequestDto();
            CryptoWithPinResponseDto cryptoWithPinResponseDto = new CryptoWithPinResponseDto();

            cryptoWithPinRequestDto.setUserPin(encryptionPin);
            cryptoWithPinRequestDto.setData(data.getString(str.toString()));
            try {
                cryptoWithPinResponseDto = cryptoUtil.decryptWithPin(cryptoWithPinRequestDto);
            } catch (InvalidKeyException | NoSuchAlgorithmException |
                     InvalidKeySpecException
                     | InvalidAlgorithmParameterException | IllegalBlockSizeException |
                     BadPaddingException e) {
                printLogger.error("Error while decrypting the data", e);
                throw new CryptoManagerException(PlatformErrorMessages.PRT_INVALID_KEY_EXCEPTION.getCode(),
                        PlatformErrorMessages.PRT_INVALID_KEY_EXCEPTION.getMessage(), e);
            }
            data.put((String) str, cryptoWithPinResponseDto.getData());

        }

        return data;
    }
}





