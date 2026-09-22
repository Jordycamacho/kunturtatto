package com.example.kunturtatto.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class PublicFormGuardTest {

    private final PublicFormGuard guard = new PublicFormGuard();

    @Test
    void dropsAPostWithoutTheFormClock() {
        assertTrue(guard.looksAutomated(new MockHttpServletRequest()));
    }

    @Test
    void acceptsAFormFilledByAPerson() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("formStartedAt", String.valueOf(System.currentTimeMillis() - 10_000));

        assertFalse(guard.looksAutomated(request));
    }

    @Test
    void dropsAFilledHoneypot() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("formStartedAt", String.valueOf(System.currentTimeMillis() - 10_000));
        request.setParameter("website", "http://spam.example");

        assertTrue(guard.looksAutomated(request));
    }

    @Test
    void blocksTheSixthSendFromTheSameAddress() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("203.0.113.10");

        for (int i = 0; i < 5; i++) {
            guard.record(request, "CONSULTA");
        }

        assertTrue(guard.isRateLimited(request, "CONSULTA"));
        assertFalse(guard.isRateLimited(request, "CONTACTO"));
    }
}
