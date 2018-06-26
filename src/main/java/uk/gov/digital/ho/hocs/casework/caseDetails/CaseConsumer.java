package uk.gov.digital.ho.hocs.casework.caseDetails;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.sqs.SqsConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.hocs.casework.caseDetails.dto.AddDocumentToCaseRequest;

@Component
public class CaseConsumer extends RouteBuilder {


    private final String caseQueue;
    private String dlq;
    private final int maximumRedeliveries;
    private final int redeliveryDelay;
    private final int backOffMultiplier;
    private CaseDataService caseService;

    @Autowired
    public CaseConsumer(CaseDataService caseService,
                        @Value("${case.queue}") String caseQueue,
                        @Value("${case.queue.dlq}") String dlq,
                        @Value("${case.queue.maximumRedeliveries}") int maximumRedeliveries,
                        @Value("${case.queue.redeliveryDelay}") int redeliveryDelay,
                        @Value("${case.queue.backOffMultiplier}") int backOffMultiplier) {
        this.caseService = caseService;
        this.caseQueue = caseQueue;
        this.dlq = dlq;
        this.maximumRedeliveries = maximumRedeliveries;
        this.redeliveryDelay = redeliveryDelay;
        this.backOffMultiplier = backOffMultiplier;
    }

    @Override
    public void configure() {

        onException(JsonMappingException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Unable to process the message: ${body}");

        onException(InvalidPayloadException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "payload exception: ${body}")
                .useOriginalMessage()
                .to(dlq);

        onException(RuntimeException.class)
                .log(LoggingLevel.ERROR, "Failed to run the command after configured back-off.")
                .useOriginalMessage()
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .maximumRedeliveries(maximumRedeliveries)
                .redeliveryDelay(redeliveryDelay)
                .backOffMultiplier(backOffMultiplier)
                .asyncDelayedRedelivery()
                .logRetryStackTrace(true);


        from(caseQueue)
                .setProperty(SqsConstants.RECEIPT_HANDLE, header(SqsConstants.RECEIPT_HANDLE))
                .log("Add Document to Case Request received: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, AddDocumentToCaseRequest.class)
                .log("Add Document to Case unmarshalled")
                .bean(caseService, "addDocumentToCase")
                .log("Add Document to Case Request processed")
                .setHeader(SqsConstants.RECEIPT_HANDLE, exchangeProperty(SqsConstants.RECEIPT_HANDLE));
    }

}