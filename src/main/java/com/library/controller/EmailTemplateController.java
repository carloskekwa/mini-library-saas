package com.library.controller;
import com.library.dto.EmailTemplateDTO;
import com.library.entity.EmailTemplate;
import com.library.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for email templates (Phase 8).
 */
@RestController
@RequestMapping("/api/email-templates")
@Tag(name = "Email Management", description = "APIs for email templates")
public class EmailTemplateController {
    private final EmailService emailService;

    public EmailTemplateController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get email template")
    public ResponseEntity<EmailTemplateDTO> getTemplate(@PathVariable EmailTemplate.TemplateType type) {
        EmailTemplateDTO template = emailService.getTemplate(type);
        return ResponseEntity.ok(template);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Save email template")
    public ResponseEntity<EmailTemplateDTO> saveTemplate(
        @RequestParam EmailTemplate.TemplateType type,
        @RequestParam String name,
        @RequestParam String subject,
        @RequestParam String body) {
        EmailTemplateDTO template = emailService.saveTemplate(type, name, subject, body);
        return ResponseEntity.status(201).body(template);
    }
}
