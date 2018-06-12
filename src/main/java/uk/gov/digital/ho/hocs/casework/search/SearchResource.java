package uk.gov.digital.ho.hocs.casework.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.digital.ho.hocs.casework.caseDetails.model.CaseData;
import uk.gov.digital.ho.hocs.casework.search.dto.SearchRequest;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
class SearchResource {

    private final SearchService searchService;

    @Autowired
    public SearchResource(SearchService searchService) {

        this.searchService = searchService;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity<Set<CaseData>> search(@RequestBody SearchRequest searchRequest, @RequestHeader("X-Auth-Username") String username) {
        Set<CaseData> searchResponses = searchService.findCases(searchRequest, username);
        return ResponseEntity.ok(searchResponses);
    }
}