package uk.gov.digital.ho.hocs.casework.casedetails.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.digital.ho.hocs.casework.casedetails.model.StageData;

import java.util.UUID;

@Repository
public interface StageDataRepository extends CrudRepository<StageData, String> {

    StageData findByUuid(UUID uuid);
}