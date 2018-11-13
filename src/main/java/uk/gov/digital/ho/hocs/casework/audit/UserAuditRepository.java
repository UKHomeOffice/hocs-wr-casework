package uk.gov.digital.ho.hocs.casework.audit;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.digital.ho.hocs.casework.audit.model.UserAuditEntry;

import java.util.UUID;

@Repository
interface UserAuditRepository extends CrudRepository<UserAuditEntry, String> {

    UserAuditEntry findByCaseUUID(UUID caseUUID);

}