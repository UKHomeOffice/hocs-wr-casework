package uk.gov.digital.ho.hocs.casework.audit;

import lombok.Getter;
import uk.gov.digital.ho.hocs.casework.caseDetails.CaseDetails;
import uk.gov.digital.ho.hocs.casework.caseDetails.StageDetails;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit")
@Getter
class AuditEntry {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private final String username;

    @Column(name = "query_data")
    private String queryData;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name ="case_id", referencedColumnName = "id")
    private AuditCaseData caseInstance;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name ="stage_id", referencedColumnName = "id")
    private AuditStageData stageInstance;

    @Column(name = "created", nullable = false)
    private final LocalDateTime created;

    @Column(name = "event_action", nullable = false)
    private final String eventAction;

    AuditEntry(String username,
               CaseDetails caseInstance,
               StageDetails stageInstance,
               AuditAction eventAction) {
        this.username = username;
        if(caseInstance != null) {
            this.caseInstance = AuditCaseData.from(caseInstance);
        }
        if(stageInstance != null) {
            this.stageInstance = AuditStageData.from(stageInstance);
        }
        this.created = LocalDateTime.now();
        this.eventAction = eventAction.toString();
    }

    AuditEntry(String username,
               String queryData,
               AuditAction eventAction) {
        this.username = username;
        this.queryData = queryData;
        this.created = LocalDateTime.now();
        this.eventAction = eventAction.toString();
    }
}
