package io.mosip.credential.service;

import io.mosip.credential.model.EventModel;
//import org.springframework.beans.factory.annotation.Autowired;


public interface CredentialService {
    //public boolean credentialservice = false;
    boolean generateCard(EventModel eventModel) throws Exception;
    //@Autoired
   // public CredentialRepository credentialRepository;

    //public default credential getCredentialByTransactionId(String transactionId) {
        //return credentialRepository.findByTransactionId(transactionId);
    //}//
}
