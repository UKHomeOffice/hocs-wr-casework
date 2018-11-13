package uk.gov.digital.ho.hocs.casework.audit;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.digital.ho.hocs.casework.audit.model.StageAuditEntry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
interface StageAuditRepository extends CrudRepository<StageAuditEntry, String> {

    @Query(value = "select asd.*,\n" +
            "       aud.created_by_user   as created_by_user,\n" +
            "       aud.created_timestamp as created_timestamp,\n" +
            "       aud.updated_by_user   as updated_by_user,\n" +
            "       aud.updated_timestamp as updated_timestamp\n" +
            "from audit_stage_data asd\n" +
            "       join (select max(sd.id) as id, sd.uuid as uuid\n" +
            "             from audit_stage_data sd\n" +
            "                    join (select max(c.id) as id, c.uuid as uuid\n" +
            "                          from audit_case_data c\n" +
            "                          where c.type in ?3\n" +
            "                            and c.timestamp between ?1 and ?2\n" +
            "                          group by c.uuid) scd on sd.case_uuid = scd.uuid and sd.timestamp between ?1 and ?2\n" +
            "             group by sd.uuid) isd on asd.id = isd.id\n" +
            "       join audit_user_data aud on asd.case_uuid = aud.case_uuid\n" +
            "order by asd.timestamp desc", nativeQuery = true)
    Set<StageAuditEntry> getAllByTimestampBetweenAndCorrespondenceTypeIn(LocalDateTime start, LocalDateTime end, List<String> correspondenceTypes);
}