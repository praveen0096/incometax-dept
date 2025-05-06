package io.mosip.credential.controller;

import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;
import io.mosip.credential.service.CredentialService;
import io.mosip.credential.logger.PrintLogger;
import io.mosip.credential.model.EventModel;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/credential")

public class CredentialServiceController {
    @Autowired
    private CredentialService credentialService;

    @Value("${mosip.event.topic}")
    private String topic;

    Logger printLogger = PrintLogger.getLogger(CredentialServiceController.class);


    @PostMapping(path = "/callback/notifyCredential", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthenticateContentAndVerifyIntent(secret = "${mosip.event.secret}", callback = "/v1/credential/credential/callback/notifyCredential", topic = "${mosip.event.topic}")
    public ResponseEntity<String> handleSubscribeEvent(@RequestBody EventModel eventModel) throws Exception {
        printLogger.info("event recieved from websub" + ", id: {}", eventModel.getEvent().getId());
        boolean isPrinted = credentialService.generateCard(eventModel);
        printLogger.info("printing status : {} for event id: {}", isPrinted, eventModel.getEvent().getId());
        return new ResponseEntity<>("request accepted.", HttpStatus.OK);

    }

}