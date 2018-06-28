package uk.gov.digital.ho.hocs.casework.caseDetails;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.casework.RequestData;
import uk.gov.digital.ho.hocs.casework.audit.AuditService;
import uk.gov.digital.ho.hocs.casework.caseDetails.exception.EntityCreationException;
import uk.gov.digital.ho.hocs.casework.caseDetails.exception.EntityNotFoundException;
import uk.gov.digital.ho.hocs.casework.caseDetails.model.DocumentData;
import uk.gov.digital.ho.hocs.casework.caseDetails.model.DocumentStatus;
import uk.gov.digital.ho.hocs.casework.caseDetails.model.DocumentType;

import javax.transaction.Transactional;
import java.util.UUID;

import static uk.gov.digital.ho.hocs.casework.HocsCaseApplication.isNullOrEmpty;

@Slf4j
@Service
public class DocumentService {

    public static final String DOCUMENT_DATA_NOT_FOUND = "DocumentData not found!";
    private final AuditService auditService;
    private final DocumentRepository documentRepository;
    private final RequestData requestData;

    @Autowired
    public DocumentService(AuditService auditService, DocumentRepository documentRepository, RequestData requestData) {
        this.auditService = auditService;
        this.documentRepository = documentRepository;
        this.requestData = requestData;
    }

    @Transactional
    public DocumentData addDocument(UUID caseUUID, UUID documentUUID, String documentDisplayName, DocumentType documentType) throws EntityCreationException {
        log.info("Requesting Create DocumentData: {} for case {}, User: {}", documentUUID, caseUUID, requestData.username());
        if (!isNullOrEmpty(caseUUID) && !isNullOrEmpty(documentUUID)) {
            DocumentData documentData = new DocumentData(caseUUID, documentUUID, documentDisplayName, documentType);
            documentRepository.save(documentData);
            auditService.writeAddDocumentEvent(documentData);
            log.info("Created DocumentData {} for case {}", documentData.getDocumentUUID(), documentData.getCaseUUID());
            return documentData;
        } else {
            throw new EntityCreationException("Failed to create documentData details, CaseUUID or DocumentUUID was null");
        }
    }

    @Transactional
    public DocumentData updateDocument(UUID caseUUID, UUID documentUUID, String s3OrigLink, String s3PdfLink, DocumentStatus status) throws EntityCreationException, EntityNotFoundException {
        log.info("Requesting Update Case DocumentData: {} for case {}, User: {}", documentUUID, caseUUID, requestData.username());
        if (!isNullOrEmpty(caseUUID) && !isNullOrEmpty(documentUUID)) {
            DocumentData documentData = documentRepository.findByDocumentUUID(documentUUID);
            if (documentData != null) {
                documentData.setS3OrigLink(s3OrigLink);
                documentData.setS3PdfLink(s3PdfLink);
                documentData.setStatus(status);
                documentRepository.save(documentData);
                auditService.writeUpdateDocumentEvent(documentData);
                log.info("Update DocumentData {} for case {}", documentData.getDocumentUUID(), documentData.getCaseUUID());
                return documentData;
            } else {
                throw new EntityNotFoundException(DOCUMENT_DATA_NOT_FOUND);
            }
        } else {
            throw new EntityCreationException("Failed to create document details, CaseUUID or DocumentUUID was null");
        }
    }

    @Transactional
    public DocumentData deleteDocument(UUID caseUUID, UUID documentUUID) throws
            EntityCreationException, EntityNotFoundException {
        log.info("Requesting Delete DocumentData: {} for case {}, User: {}", documentUUID, caseUUID, requestData.username());
        if (!isNullOrEmpty(caseUUID) && !isNullOrEmpty(documentUUID)) {
            DocumentData documentData = documentRepository.findByDocumentUUID(documentUUID);
            if (documentData != null) {
                documentData.setDeleted();
                documentRepository.save(documentData);
                auditService.writeDeleteDocumentEvent(documentData);
                log.info("Set Deleted to TRUE for DocumentData {} for case {}", documentData.getDocumentUUID(), documentData.getCaseUUID());
                return documentData;
            } else {
                throw new EntityNotFoundException(DOCUMENT_DATA_NOT_FOUND);
            }
        } else {
            throw new EntityCreationException("Failed to delete document details, CaseUUID or DocumentUUID was null");
        }
    }

    @Transactional
    public DocumentData unDeleteDocument(UUID caseUUID, UUID documentUUID) throws
            EntityCreationException, EntityNotFoundException {
        log.info("Requesting unDelete DocumentData: {} for case {}, User: {}", documentUUID, caseUUID, requestData.username());
        if (!isNullOrEmpty(caseUUID) && !isNullOrEmpty(documentUUID)) {
            DocumentData documentData = documentRepository.findByDocumentUUID(documentUUID);
            if (documentData != null) {
                documentData.setUnDeleted();
                documentRepository.save(documentData);
                auditService.writeUndeleteDocumentEvent(documentData);
                log.info("Set Deleted to FALSE for DocumentData {} for case {}", documentData.getDocumentUUID(), documentData.getCaseUUID());
                return documentData;
            } else {
                throw new EntityNotFoundException(DOCUMENT_DATA_NOT_FOUND);
            }
        } else {
            throw new EntityCreationException("Failed to unDelete document details, CaseUUID or DocumentUUID was null");
        }
    }
}
