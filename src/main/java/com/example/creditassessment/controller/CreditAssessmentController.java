package com.example.creditassessment.controller;

import com.example.creditassessment.dto.CreditAssessmentInitiateRequest;
import com.example.creditassessment.dto.CreditAssessmentInitiateResponse;
import com.example.creditassessment.dto.CreditAssessmentStatusResponse;
import com.example.creditassessment.service.CreditAssessmentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/credit-assessment")
public class CreditAssessmentController {

    private static final Logger logger = LogManager.getLogger(CreditAssessmentController.class);

    private final CreditAssessmentService creditAssessmentService;

    @Autowired
    public CreditAssessmentController(CreditAssessmentService creditAssessmentService) {
        this.creditAssessmentService = creditAssessmentService;
    }

    /**
     * Initiate a credit assessment.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/initiate")
    public ResponseEntity<CreditAssessmentInitiateResponse> initiateAssessment(
            @RequestBody CreditAssessmentInitiateRequest request) {
        logger.info("Initiating credit assessment for customer: {}", request.getCustomerId());
        CreditAssessmentInitiateResponse response = creditAssessmentService.initiateAssessment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Check the status of a credit assessment by assessmentId.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/status/{assessmentId}")
    public ResponseEntity<CreditAssessmentStatusResponse> getAssessmentStatus(
            @PathVariable String assessmentId) {
        logger.info("Checking status for assessmentId: {}", assessmentId);
        CreditAssessmentStatusResponse response = creditAssessmentService.getAssessmentStatus(assessmentId);
        return ResponseEntity.ok(response);
    }
}
