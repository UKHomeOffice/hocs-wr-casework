package uk.gov.digital.ho.hocs.casework.casedetails;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.hocs.casework.casedetails.dto.UpdateDocumentFromQueueRequest;
import uk.gov.digital.ho.hocs.casework.casedetails.exception.EntityCreationException;
import uk.gov.digital.ho.hocs.casework.casedetails.exception.EntityNotFoundException;
import uk.gov.digital.ho.hocs.casework.casedetails.model.DocumentStatus;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseConsumerTest extends CamelTestSupport {

    private static final String caseQueue = "direct:case-queue";
    private static final String dlq = "mock:case-queue-dlq";
    private ObjectMapper mapper = new ObjectMapper();

    private static final UUID caseUUID = UUID.randomUUID();
    private static final UUID docUUID = UUID.randomUUID();


    @Mock
    private DocumentDataService mockDataService;

    @Override
    public RouteBuilder createRouteBuilder() {
        return new CaseConsumer(
                mockDataService,
                caseQueue,
                dlq,
                0, 0, 0);
    }

    @Test
    public void shouldCallAddDocumentToCaseService() throws JsonProcessingException, EntityCreationException, EntityNotFoundException {

        UpdateDocumentFromQueueRequest document = new UpdateDocumentFromQueueRequest(caseUUID,
                docUUID, "PDF Link", "Orig Link", DocumentStatus.UPLOADED);

        String json = mapper.writeValueAsString(document);
        template.sendBody(caseQueue, json);

        verify(mockDataService, times(1)).updateDocumentFromQueue(any(UpdateDocumentFromQueueRequest.class));
    }

    @Test
    public void shouldNotProcessMessgeWhenMarshellingFails() throws JsonProcessingException, InterruptedException, EntityCreationException, EntityNotFoundException {
        getMockEndpoint(dlq).setExpectedCount(1);
        String json = mapper.writeValueAsString("{invalid:invalid}");
        template.sendBody(caseQueue, json);
        verify(mockDataService, never()).updateDocumentFromQueue(any(UpdateDocumentFromQueueRequest.class));
        getMockEndpoint(dlq).assertIsSatisfied();
    }

    @Test
    public void shouldTransferToDLQOnFailure() throws JsonProcessingException, InterruptedException, EntityCreationException, EntityNotFoundException {

        UpdateDocumentFromQueueRequest document = new UpdateDocumentFromQueueRequest(caseUUID,
                docUUID, "PDF Link", "Orig Link", DocumentStatus.UPLOADED);

        doThrow(EntityCreationException.class)
                .when(mockDataService)
                .updateDocumentFromQueue(any(UpdateDocumentFromQueueRequest.class));

        getMockEndpoint(dlq).setExpectedCount(1);
        String json = mapper.writeValueAsString(document);
        template.sendBody(caseQueue, json);
        getMockEndpoint(dlq).assertIsSatisfied();
    }

}