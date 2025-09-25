package com.example.creditassessment.service;

import com.example.creditassessment.dto.CreditAssessmentInitiateRequest;
import com.example.creditassessment.dto.CreditAssessmentInitiateResponse;
import com.example.creditassessment.dto.CreditAssessmentStatusResponse;

public interface CreditAssessmentService {
    CreditAssessmentInitiateResponse initiateAssessment(CreditAssessmentInitiateRequest request);
    CreditAssessmentStatusResponse getAssessmentStatus(String assessmentId);
    boolean validateApplicantData(CreditAssessmentInitiateRequest request);
}
