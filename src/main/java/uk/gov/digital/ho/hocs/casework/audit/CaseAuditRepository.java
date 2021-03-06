package uk.gov.digital.ho.hocs.casework.audit;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.digital.ho.hocs.casework.audit.model.CaseAuditEntry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
interface CaseAuditRepository extends CrudRepository<CaseAuditEntry, String> {

    @Query(value = "select cd.* from audit_case_data cd join (select max(c.id) as id, c.uuid as uuid from audit_case_data c where c.type in ?3 and c.timestamp between ?1 and ?2 group by c.uuid ) scd on cd.id = scd.id order by cd.timestamp desc", nativeQuery = true)
    Set<CaseAuditEntry> getAllByTimestampBetweenAndCorrespondenceTypeIn(LocalDateTime start, LocalDateTime end, List<String> correspondenceTypes);
}