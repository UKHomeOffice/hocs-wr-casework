package uk.gov.digital.ho.hocs.casework.search;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.casework.RequestData;
import uk.gov.digital.ho.hocs.casework.audit.AuditService;
import uk.gov.digital.ho.hocs.casework.casedetails.model.CaseData;
import uk.gov.digital.ho.hocs.casework.casedetails.repository.CaseDataRepository;
import uk.gov.digital.ho.hocs.casework.search.dto.SearchRequest;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static uk.gov.digital.ho.hocs.casework.HocsCaseApplication.isNullOrEmpty;

@Service
@Slf4j
class SearchService {

    private final AuditService auditService;
    private final CaseDataRepository caseDataRepository;
    private final RequestData requestData;

    @Autowired
    public SearchService(AuditService auditService, CaseDataRepository caseDataRepository, RequestData requestData) {
        this.auditService = auditService;
        this.caseDataRepository = caseDataRepository;
        this.requestData = requestData;
    }

    @Transactional
    public Set<CaseData> findCases(SearchRequest searchRequest) {
        auditService.writeSearchEvent(searchRequest);
        log.info("Starting Search, User: {}", requestData.username());


        Set<CaseData> results = new HashSet<>(findByCaseReference(searchRequest.getCaseReference()));

        // If we find an exact match by Case Reference then don't search further.
        if (results.isEmpty()) {
            results.addAll(findByNameOrDob(searchRequest.getCaseData()));
        }

        log.info("Completed Search, Found: {}, User: {}", results.size(), requestData.username());
        return results;
    }

    private static String getFieldString(Map<String, String> stageData, String key) {
        String ret = "";
        if (stageData != null && stageData.containsKey(key)) {
            String val = stageData.get(key);
            if (val != null) {
                ret = val;
            }
        }
        return ret;
    }

    private Set<CaseData> findByCaseReference(String caseReference) {
        Set<CaseData> returnResults = new HashSet<>();

        if (!isNullOrEmpty(caseReference)) {
            Set<CaseData> results = caseDataRepository.findByCaseReference(caseReference);
            if (results != null && !results.isEmpty()) {
                returnResults.addAll(results);
            }
        }
        return returnResults;
    }

    private Set<CaseData> findByNameOrDob(Map<String, String> searchMap) {
        Set<CaseData> returnResults = new HashSet<>();

        if (searchMap != null && !searchMap.isEmpty()) {
            Set<CaseData> results = caseDataRepository.findByNameOrDob(getFieldString(searchMap, "first-name"), getFieldString(searchMap, "last-name"), getFieldString(searchMap, "date-of-birth"));
            if (results != null && !results.isEmpty()) {
                returnResults.addAll(results);
            }
        }
        return returnResults;
    }
}