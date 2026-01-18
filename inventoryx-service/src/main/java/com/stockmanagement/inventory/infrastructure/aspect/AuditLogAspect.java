package com.stockmanagement.inventory.infrastructure.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockmanagement.inventory.application.annotation.Auditable;
import com.stockmanagement.inventory.domain.model.AuditLog;
import com.stockmanagement.inventory.domain.model.valueobject.UserId;
import com.stockmanagement.inventory.domain.repository.AuditLogRepository;
import com.stockmanagement.inventory.domain.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Around("@annotation(auditable)")
    public Object logAudit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        String action = auditable.action();
        String resource = auditable.resource();
        String userIdStr = null;
        String status = "SUCCESS";
        String details = "";
        String errorMessage = null;

        try {
            // Capture User ID from Security Context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                String username = null;
                if (authentication
                        .getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
                    username = ((org.springframework.security.core.userdetails.UserDetails) authentication
                            .getPrincipal()).getUsername();
                } else if (authentication.getPrincipal() instanceof String) {
                    username = (String) authentication.getPrincipal();
                }

                if (username != null) {
                    // We need UserId, but SecurityContext usually has UserDetails which might not
                    // have ID ideally.
                    // But our CustomUserDetailsService loads UserDetails. ideally we should put
                    // UserId in it or look it up.
                    // Lookup by username is safe.
                    userIdStr = userRepository
                            .findByUsername(
                                    new com.stockmanagement.inventory.domain.model.valueobject.Username(username))
                            .map(u -> u.getId().value().toString())
                            .orElse(null);
                }
            }

            // Capture Arguments as Details
            try {
                if (joinPoint.getArgs().length > 0) {
                    details = objectMapper.writeValueAsString(joinPoint.getArgs()[0]); // First arg usually command
                }
            } catch (JsonProcessingException e) {
                details = "Could not serialize arguments";
            }

            Object result = joinPoint.proceed();
            return result;

        } catch (Exception e) {
            status = "FAILURE";
            errorMessage = e.getMessage();
            throw e;
        } finally {
            try {
                // Try to capture User ID again (useful for Login success case where context is
                // updated inside method)
                if (userIdStr == null) {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication != null && authentication.isAuthenticated()
                            && !"anonymousUser".equals(authentication.getPrincipal())) {
                        String username = null;
                        if (authentication
                                .getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
                            username = ((org.springframework.security.core.userdetails.UserDetails) authentication
                                    .getPrincipal()).getUsername();
                        } else if (authentication.getPrincipal() instanceof String) {
                            username = (String) authentication.getPrincipal();
                        }

                        if (username != null) {
                            userIdStr = userRepository
                                    .findByUsername(new com.stockmanagement.inventory.domain.model.valueobject.Username(
                                            username))
                                    .map(u -> u.getId().value().toString())
                                    .orElse(null);
                        }
                    }
                }

                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
                String ipAddress = request.getRemoteAddr();
                String userAgent = request.getHeader("User-Agent");

                String finalDetails = details + (errorMessage != null ? " | Error: " + errorMessage : "");

                AuditLog logEntry = AuditLog.builder()
                        .id(UUID.randomUUID())
                        .userId(userIdStr != null ? new UserId(UUID.fromString(userIdStr)) : null)
                        // ... rest of builder
                        .action(action)
                        .resource(resource)
                        .details(finalDetails.length() > 500 ? finalDetails.substring(0, 500) + "..." : finalDetails)
                        .ipAddress(ipAddress)
                        .userAgent(userAgent)
                        .status(status)
                        .build();

                auditLogRepository.save(logEntry);

            } catch (Exception ex) {
                log.error("Failed to save audit log", ex);
            }
        }
    }
}
