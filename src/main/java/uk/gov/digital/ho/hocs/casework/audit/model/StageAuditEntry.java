package uk.gov.digital.ho.hocs.casework.audit.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.digital.ho.hocs.casework.casedetails.model.StageData;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_stage_data")
@NoArgsConstructor
public class StageAuditEntry implements Serializable {

    @Column(name = "type")
    @Getter
    private String type;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name ="uuid")
    @Getter
    private UUID uuid;

    @Column(name = "timestamp")
    @Getter
    private LocalDateTime timestamp;

    @Column(name ="data")
    @Setter
    @Getter
    private String data;

    @Column(name = "case_uuid")
    @Getter
    private UUID caseUUID;

    private StageAuditEntry(UUID stageUUID, String type, String data, UUID caseUUID, LocalDateTime timestamp) {
        this.uuid = stageUUID;
        this.type = type;
        this.data = data;
        this.caseUUID = caseUUID;
        this.timestamp = timestamp;
    }

    public static StageAuditEntry from(StageData stageData)
    {
        return new StageAuditEntry(stageData.getUuid(), stageData.getType(), stageData.getData(), stageData.getCaseUUID(), stageData.getTimestamp());
    }
}
