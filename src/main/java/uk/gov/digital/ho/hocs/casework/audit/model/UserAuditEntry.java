package uk.gov.digital.ho.hocs.casework.audit.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_user_data")
@NoArgsConstructor
public class UserAuditEntry implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "uuid")
    @Getter
    private UUID uuid;

    @Column(name = "case_uuid")
    @Getter
    private UUID caseUUID;

    @Column(name = "created_by_user")
    @Getter
    private String createdByUser;

    @Column(name = "created_timestamp")
    @Getter
    private LocalDateTime createdTimestamp;

    @Column(name = "updated_by_user")
    @Getter
    private String updatedByUser;

    @Column(name = "updated_timestamp")
    @Getter
    private LocalDateTime updatedTimestamp;

    private UserAuditEntry(UUID caseUUID, String username, LocalDateTime createdTimestamp) {
        this.uuid = UUID.randomUUID();
        this.caseUUID = caseUUID;
        this.createdByUser = username;
        this.createdTimestamp = createdTimestamp;

    }

    public static UserAuditEntry from(UUID caseUUID, String username, LocalDateTime timestamp) {
        return new UserAuditEntry(caseUUID,username,timestamp);
    }

    public void update(String username, LocalDateTime timestamp){
        this.updatedByUser = username;
        this.updatedTimestamp = timestamp;
    }
}
