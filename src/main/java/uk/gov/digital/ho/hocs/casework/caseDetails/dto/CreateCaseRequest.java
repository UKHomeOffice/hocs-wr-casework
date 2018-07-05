package uk.gov.digital.ho.hocs.casework.caseDetails.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.digital.ho.hocs.casework.caseDetails.model.CaseType;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateCaseRequest {

    @JsonProperty("caseType")
    private CaseType caseType;

    @JsonProperty("documentSummaries")
    private List<DocumentSummary> documents;

}
