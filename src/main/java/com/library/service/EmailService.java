package com.library.service;
import com.library.dto.EmailTemplateDTO;
import com.library.entity.EmailTemplate;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.EmailTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for email management (Phase 8).
 */
@Service
@Transactional
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final EmailTemplateRepository emailTemplateRepository;

    public EmailService(EmailTemplateRepository emailTemplateRepository) {
        this.emailTemplateRepository = emailTemplateRepository;
    }

    public void sendEmail(String to, String subject, String body) {
        logger.info("Sending email to: {}", to);
    }

    public EmailTemplateDTO getTemplate(EmailTemplate.TemplateType type) {
        EmailTemplate template = emailTemplateRepository.findByType(type)
            .orElseThrow(() -> new ResourceNotFoundException("Template not found"));
        return EmailTemplateDTO.from(template);
    }

    public EmailTemplateDTO saveTemplate(EmailTemplate.TemplateType type, String name, String subject, String body) {
        logger.info("Saving email template: {}", name);
        EmailTemplate template = new EmailTemplate(type, name, subject, body);
        EmailTemplate saved = emailTemplateRepository.save(template);
        return EmailTemplateDTO.from(saved);
    }
}
