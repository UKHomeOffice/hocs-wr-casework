package uk.gov.digital.ho.hocs.casework.caseDetails;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StageDetailsRepository extends CrudRepository<StageDetails, String> {

    StageDetails findByUuid(UUID uuid);
}