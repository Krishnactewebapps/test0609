package com.example.creditassessment.service;

import com.example.creditassessment.dto.CreditAssessmentInitiateRequest;
import com.example.creditassessment.dto.CreditAssessmentInitiateResponse;
import com.example.creditassessment.dto.CreditAssessmentStatusResponse;
import com.example.creditassessment.model.Applicant;
import com.example.creditassessment.model.LoanApplication;
import com.example.creditassessment.repository.ApplicantRepository;
import com.example.creditassessment.repository.LoanApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CreditAssessmentServiceTest {

    @Mock
    private ApplicantRepository applicantRepository;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CreditAssessmentServiceImpl creditAssessmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        creditAssessmentService = new CreditAssessmentServiceImpl(
                applicantRepository, loanApplicationRepository, notificationService);
    }

    @Test
    void testInitiateAssessment_Success() {
        CreditAssessmentInitiateRequest request = new CreditAssessmentInitiateRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPhoneNumber("1234567890");
        request.setAddress("123 Main St");
        request.setAmount(new BigDecimal("10000"));
        request.setTermMonths(12);

        when(applicantRepository.existsByEmail(anyString())).thenReturn(false);
        when(applicantRepository.save(any(Applicant.class))).thenAnswer(i -> {
            Applicant a = i.getArgument(0);
            a.setId(1L);
            return a;
        });
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenAnswer(i -> {
            LoanApplication la = i.getArgument(0);
            la.setId(1L);
            return la;
        });

        CreditAssessmentInitiateResponse response = creditAssessmentService.initiateAssessment(request);

        assertNotNull(response);
        assertNotNull(response.getAssessmentId());
        assertEquals("INITIATED", response.getStatus());
        verify(notificationService, times(1)).notifyAssessmentInitiated(anyString(), eq("john.doe@example.com"));
    }

    @Test
    void testValidateApplicantData_Valid() {
        CreditAssessmentInitiateRequest request = new CreditAssessmentInitiateRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane.smith@example.com");
        request.setPhoneNumber("9876543210");
        request.setAddress("456 Elm St");
        request.setAmount(new BigDecimal("5000"));
        request.setTermMonths(24);

        when(applicantRepository.existsByEmail("jane.smith@example.com")).thenReturn(false);

        boolean valid = creditAssessmentService.validateApplicantData(request);
        assertTrue(valid);
    }

    @Test
    void testValidateApplicantData_DuplicateEmail() {
        CreditAssessmentInitiateRequest request = new CreditAssessmentInitiateRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane.smith@example.com");
        request.setPhoneNumber("9876543210");
        request.setAddress("456 Elm St");
        request.setAmount(new BigDecimal("5000"));
        request.setTermMonths(24);

        when(applicantRepository.existsByEmail("jane.smith@example.com")).thenReturn(true);

        boolean valid = creditAssessmentService.validateApplicantData(request);
        assertFalse(valid);
    }

    @Test
    void testNotificationSentOnInitiate() {
        CreditAssessmentInitiateRequest request = new CreditAssessmentInitiateRequest();
        request.setFirstName("Alice");
        request.setLastName("Brown");
        request.setEmail("alice.brown@example.com");
        request.setPhoneNumber("5551234567");
        request.setAddress("789 Oak St");
        request.setAmount(new BigDecimal("15000"));
        request.setTermMonths(36);

        when(applicantRepository.existsByEmail(anyString())).thenReturn(false);
        when(applicantRepository.save(any(Applicant.class))).thenAnswer(i -> {
            Applicant a = i.getArgument(0);
            a.setId(2L);
            return a;
        });
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenAnswer(i -> {
            LoanApplication la = i.getArgument(0);
            la.setId(2L);
            return la;
        });

        creditAssessmentService.initiateAssessment(request);
        verify(notificationService, times(1)).notifyAssessmentInitiated(anyString(), eq("alice.brown@example.com"));
    }
}
