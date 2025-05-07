package io.mosip.credential.repository;

import io.mosip.credential.entity.CredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CredentialRepository extends JpaRepository<CredentialEntity, Long> {
    //@Query("FROM SelfRegistrationEntity WHERE rid=?1")
    //CredentialEntity findByRID(String rid);

    //Optional<CredentialEntity> findByStatus(String status);

    //Optional<CredentialEntity> findByEmailIdAndStatus(String emailId, String status);
}
