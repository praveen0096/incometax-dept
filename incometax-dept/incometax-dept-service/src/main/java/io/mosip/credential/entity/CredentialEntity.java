package io.mosip.credential.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tax_info",schema ="tax_dept")
public class CredentialEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "date_of_birth", nullable = false)
    private String dateOfBirth;

    @Column(name = "id_type", nullable = false)
    private String idType;

    @Column(name = "id_number", nullable = false)
    private String idNumber;

    @Column(name = "issued_on", nullable = true)
    private LocalDateTime issuedOn;

    @Column(name = "expired_on", nullable = true)
    private LocalDateTime expiredOn;

    @Column(name = "token_id", nullable = false)
    private String tokenId;

    @Column(name = "status_code", nullable = false)
    private String statusCode;

    @Column(name = "cr_by")
    private String createdBy;

    @Column(name = "cr_dtimes", updatable = false)
    private LocalDateTime createDateTime;

    @Column(name = "upd_by")
    private String updatedBy;

    @Column(name = "upd_dtimes")
    private LocalDateTime updateDateTime;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "del_dtimes")
    private LocalDateTime deletedDateTime;


}
