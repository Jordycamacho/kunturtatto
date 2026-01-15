package com.example.kunturtatto.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.example.kunturtatto.exception.EmailException;
import com.example.kunturtatto.model.Appointment;
import com.example.kunturtatto.model.EmailAuditLog;
import com.example.kunturtatto.repository.EmailAuditLogRepository;
import com.example.kunturtatto.request.ContactRequest;
import com.example.kunturtatto.service.IContactService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactServiceImpl implements IContactService {

    @Value("${email.sender}")
    private String myEmail;

    @Value("${spring.application.name:KunturTattoo}")
    private String applicationName;

    private final JavaMailSender javaMailSender;
    private final EmailAuditLogRepository emailAuditLogRepository;

    @Override
    @Transactional
    public void sendContactEmail(ContactRequest request) {
        long startTime = System.currentTimeMillis();
        String auditId = UUID.randomUUID().toString();
        
        log.info("[EMAIL_SERVICE] Iniciando envío de email de contacto. AuditId={}, emailRemitente={}, asunto={}",
                auditId, request.getEmail(), request.getSubject());
        
        try {
            MimeMessagePreparator messagePreparator = mimeMessage -> {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
                helper.setFrom(myEmail);
                helper.setTo(myEmail);
                helper.setSubject("Nuevo mensaje de contacto: " + request.getSubject());
                String htmlContent = buildContactEmailHtml(request);
                helper.setText(htmlContent, true);
            };

            log.debug("[EMAIL_SERVICE] Preparando email de contacto. AuditId={}", auditId);
            
            javaMailSender.send(messagePreparator);
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            log.info("[EMAIL_SERVICE] Email de contacto enviado exitosamente. AuditId={}, destinatario={}, tiempoEjecucion={}ms",
                    auditId, myEmail, executionTime);
            
            saveAuditLog(
                auditId,
                "CONTACT_EMAIL",
                request.getEmail(),
                myEmail,
                "Nuevo mensaje de contacto: " + request.getSubject(),
                "SUCCESS",
                executionTime,
                null
            );
            
        } catch (MailException e) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            log.error("[EMAIL_SERVICE] Error al enviar email de contacto. AuditId={}, error={}",
                    auditId, e.getMessage(), e);
            
            // Registrar auditoría con error
            saveAuditLog(
                auditId,
                "CONTACT_EMAIL",
                request.getEmail(),
                myEmail,
                "Nuevo mensaje de contacto: " + request.getSubject(),
                "FAILED",
                executionTime,
                e.getMessage()
            );
            
            throw new EmailException("Error al enviar el email de contacto", e);
        }
    }

    @Override
    @Transactional
    public void sendAppointmentCompletion(Appointment appointment) {
        String subject = "¡Gracias por tu visita! - Muthabara";
        sendAppointmentEmail(
            appointment, 
            subject, 
            buildAppointmentCompletionHtml(appointment),
            "APPOINTMENT_COMPLETION"
        );
    }

    @Override
    @Transactional
    public void sendAppointmentConfirmation(Appointment appointment) {
        String subject = "Confirmación de Cita - Muthabara";
        sendAppointmentEmail(
            appointment, 
            subject, 
            buildAppointmentConfirmationHtml(appointment),
            "APPOINTMENT_CONFIRMATION"
        );
    }

    @Override
    @Transactional
    public void sendAppointmentUpdateNotification(Appointment appointment) {
        String subject = "Actualización de Cita - Muthabara";
        sendAppointmentEmail(
            appointment, 
            subject, 
            buildAppointmentUpdateHtml(appointment),
            "APPOINTMENT_UPDATE"
        );
    }

    @Override
    @Transactional
    public void sendAppointmentCancellation(Appointment appointment) {
        String subject = "Cancelación de Cita - Muthabara";
        sendAppointmentEmail(
            appointment, 
            subject, 
            buildAppointmentCancellationHtml(appointment),
            "APPOINTMENT_CANCELLATION"
        );
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent, String emailType, String auditId) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(myEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            helper.getMimeMessage().addHeader("X-Audit-Id", auditId);
            helper.getMimeMessage().addHeader("X-Email-Type", emailType);
            helper.getMimeMessage().addHeader("X-Application", applicationName);
        };

        try {
            log.debug("[EMAIL_SERVICE] Enviando email. AuditId={}, tipo={}, destinatario={}",
                    auditId, emailType, to);
            
            javaMailSender.send(messagePreparator);
            
            log.info("[EMAIL_SERVICE] Email enviado exitosamente. AuditId={}, tipo={}, destinatario={}",
                    auditId, emailType, to);
                    
        } catch (MailException e) {
            log.error("[EMAIL_SERVICE] Error al enviar email. AuditId={}, tipo={}, destinatario={}, error={}",
                    auditId, emailType, to, e.getMessage(), e);
            throw new EmailException("Error al enviar el email", e);
        }
    }

    private void sendAppointmentEmail(Appointment appointment, String subject, String htmlContent, String emailType) {
        long startTime = System.currentTimeMillis();
        String auditId = UUID.randomUUID().toString();
        
        log.info("[EMAIL_SERVICE] Iniciando envío de email de cita. AuditId={}, tipo={}, cliente={}, citaId={}",
                auditId, emailType, appointment.getCustomerEmail(), appointment.getId());
        
        try {
            sendHtmlEmail(appointment.getCustomerEmail(), subject, htmlContent, emailType, auditId);
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            saveAuditLog(
                auditId,
                emailType,
                myEmail,
                appointment.getCustomerEmail(),
                subject,
                "SUCCESS",
                executionTime,
                null,
                appointment.getId()
            );
            
            log.info("[EMAIL_SERVICE] Email de cita enviado exitosamente. AuditId={}, tipo={}, tiempoEjecucion={}ms",
                    auditId, emailType, executionTime);
                    
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            log.error("[EMAIL_SERVICE] Error al enviar email de cita. AuditId={}, tipo={}, error={}",
                    auditId, emailType, e.getMessage(), e);
            
            saveAuditLog(
                auditId,
                emailType,
                myEmail,
                appointment.getCustomerEmail(),
                subject,
                "FAILED",
                executionTime,
                e.getMessage(),
                appointment.getId()
            );
            
            throw e;
        }
    }

    private void saveAuditLog(String auditId, String emailType, String from, String to, 
                             String subject, String status, long executionTime, String errorMessage) {
        saveAuditLog(auditId, emailType, from, to, subject, status, executionTime, errorMessage, null);
    }

    private void saveAuditLog(String auditId, String emailType, String from, String to, 
                             String subject, String status, long executionTime, String errorMessage, Long appointmentId) {
        try {
            EmailAuditLog auditLog = EmailAuditLog.builder()
                .auditId(auditId)
                .emailType(emailType)
                .sender(from)
                .recipient(to)
                .subject(subject)
                .status(status)
                .executionTime(executionTime)
                .errorMessage(errorMessage)
                .appointmentId(appointmentId)
                .sentAt(LocalDateTime.now())
                .applicationName(applicationName)
                .build();
            
            emailAuditLogRepository.save(auditLog);
            
            log.debug("[EMAIL_AUDIT] Registro de auditoría guardado. AuditId={}, tipo={}, estado={}",
                    auditId, emailType, status);
                    
        } catch (Exception e) {
            log.warn("[EMAIL_AUDIT] Error al guardar registro de auditoría. AuditId={}, error={}",
                    auditId, e.getMessage());
        }
    }

    private String buildContactEmailHtml(ContactRequest request) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { color: #333; border-bottom: 1px solid #eee; padding-bottom: 10px; }
                        .detail { margin: 15px 0; }
                        .label { font-weight: bold; color: #555; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>Nuevo mensaje de contacto</h2>
                        </div>

                        <div class="detail">
                            <span class="label">De:</span> %s
                        </div>

                        <div class="detail">
                            <span class="label">Asunto:</span> %s
                        </div>

                        <div class="detail">
                            <span class="label">Tamaño del tatuaje:</span> %s cm
                        </div>

                        <div class="detail">
                            <span class="label">Parte del cuerpo:</span> %s
                        </div>

                        <div class="detail">
                            <span class="label">Referencias:</span> %s
                        </div>

                        <div class="detail">
                            <span class="label">Mensaje:</span>
                            <p>%s</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                request.getEmail(),
                request.getSubject(),
                request.getTattooCm(),
                request.getBody(),
                request.getLinksReference(),
                request.getMessage());
    }

    private String buildAppointmentConfirmationHtml(Appointment appointment) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: 'Arial', sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #e1e1e1; border-radius: 5px; }
                        .header { text-align: center; padding-bottom: 20px; border-bottom: 1px solid #eee; }
                        .logo { max-width: 150px; }
                        .content { padding: 20px 0; }
                        .appointment-details { background: #f9f9f9; padding: 15px; border-radius: 5px; margin: 20px 0; }
                        .detail { margin-bottom: 10px; }
                        .label { font-weight: bold; color: #555; }
                        .footer { text-align: center; padding-top: 20px; border-top: 1px solid #eee; font-size: 12px; color: #777; }
                        .button { display: inline-block; padding: 10px 20px; background: #4CAF50; color: white; text-decoration: none; border-radius: 5px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>Confirmación de Cita</h2>
                            <p>¡Tu cita ha sido confirmada!</p>
                        </div>
                        <div class="content">
                            <p>Hola %s,</p>
                            <p>Gracias por reservar con nosotros. Aquí están los detalles de tu cita:</p>

                            <div class="appointment-details">
                                <div class="detail"><span class="label">Fecha:</span> %s</div>
                                <div class="detail"><span class="label">Hora:</span> %s</div>
                                <div class="detail"><span class="label">Diseño:</span> %s</div>
                            </div>

                            <p><strong>Por favor, llega 15 minutos antes de tu cita.</strong></p>
                            <p>Si necesitas modificar o cancelar tu cita, por favor contáctanos con al menos 24 horas de anticipación.</p>

                            <p style="text-align: center; margin-top: 30px;">
                                <a href="mailto:%s" class="button">Contactar</a>
                            </p>
                        </div>
                        <div class="footer">
                            <p>Muthabara<br>%s</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        appointment.getCustomerName(),
                        appointment.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        appointment.getTime(),
                        appointment.getDesign() != null ? appointment.getDesign().getTitle() : "Personalizado",
                        myEmail,
                        LocalDate.now().getYear());
    }

    private String buildAppointmentUpdateHtml(Appointment appointment) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: 'Arial', sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #e1e1e1; border-radius: 5px; }
                        .header { text-align: center; padding-bottom: 20px; border-bottom: 1px solid #eee; }
                        .content { padding: 20px 0; }
                        .appointment-details { background: #f9f9f9; padding: 15px; border-radius: 5px; margin: 20px 0; }
                        .detail { margin-bottom: 10px; }
                        .label { font-weight: bold; color: #555; }
                        .changes { background: #fff8e1; padding: 15px; border-left: 4px solid #FFC107; margin: 20px 0; }
                        .footer { text-align: center; padding-top: 20px; border-top: 1px solid #eee; font-size: 12px; color: #777; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>Actualización de Cita</h2>
                            <p>Los detalles de tu cita han cambiado</p>
                        </div>
                        <div class="content">
                            <p>Hola %s,</p>
                            <p>Queríamos informarte que los detalles de tu cita han sido actualizados:</p>

                            <div class="appointment-details">
                                <div class="detail"><span class="label">Nueva fecha:</span> %s</div>
                                <div class="detail"><span class="label">Nueva hora:</span> %s</div>
                                <div class="detail"><span class="label">Diseño:</span> %s</div>
                            </div>

                            <div class="changes">
                                <p><strong>Importante:</strong> Por favor toma nota de los cambios realizados.</p>
                                <p>Si estos cambios no te funcionan, contáctanos para reprogramar.</p>
                            </div>

                            <p>Si no solicitaste este cambio, por favor contáctanos inmediatamente.</p>
                        </div>
                        <div class="footer">
                            <p>Muthabara<br>
                            © %s Muthabara. Todos los derechos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        appointment.getCustomerName(),
                        appointment.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        appointment.getTime(),
                        appointment.getDesign() != null ? appointment.getDesign().getTitle() : "Personalizado",
                        myEmail,
                        LocalDate.now().getYear());
    }

    private String buildAppointmentCancellationHtml(Appointment appointment) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: 'Arial', sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #e1e1e1; border-radius: 5px; }
                        .header { text-align: center; padding-bottom: 20px; border-bottom: 1px solid #eee; color: #d32f2f; }
                        .content { padding: 20px 0; }
                        .appointment-details { background: #ffebee; padding: 15px; border-radius: 5px; margin: 20px 0; }
                        .detail { margin-bottom: 10px; }
                        .label { font-weight: bold; color: #555; }
                        .footer { text-align: center; padding-top: 20px; border-top: 1px solid #eee; font-size: 12px; color: #777; }
                        .button { display: inline-block; padding: 10px 20px; background: #d32f2f; color: white; text-decoration: none; border-radius: 5px; }
                        .reschedule { background: #e8f5e9; padding: 15px; border-left: 4px solid #4CAF50; margin: 20px 0; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>Cancelación de Cita</h2>
                            <p>Tu cita ha sido cancelada</p>
                        </div>
                        <div class="content">
                            <p>Hola %s,</p>
                            <p>Lamentamos informarte que tu cita ha sido cancelada:</p>

                            <div class="appointment-details">
                                <div class="detail"><span class="label">Fecha:</span> %s</div>
                                <div class="detail"><span class="label">Hora:</span> %s</div>
                                <div class="detail"><span class="label">Diseño:</span> %s</div>
                            </div>

                            <div class="reschedule">
                                <p><strong>¿Fue un error o deseas reprogramar?</strong></p>
                                <p>Si deseas reagendar tu cita, estamos disponibles para ayudarte.</p>
                            </div>

                            <p style="text-align: center; margin-top: 30px;">
                                <a href="mailto:%s" class="button">Contactar para reprogramar</a>
                            </p>

                            <p>Lamentamos cualquier inconveniente que esto pueda causarte y esperamos poder atenderte en el futuro.</p>
                        </div>
                        <div class="footer">
                            <p>Muthabara<br>
                            © %s Muthabara. Todos los derechos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        appointment.getCustomerName(),
                        appointment.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        appointment.getTime(),
                        appointment.getDesign() != null ? appointment.getDesign().getTitle() : "Personalizado",
                        myEmail,
                        LocalDate.now().getYear());
    }

    private String buildAppointmentCompletionHtml(Appointment appointment) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: 'Arial', sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #e1e1e1; border-radius: 5px; }
                        .header { text-align: center; padding-bottom: 20px; border-bottom: 1px solid #eee; }
                        .logo { max-width: 150px; }
                        .content { padding: 20px 0; }
                        .thank-you { text-align: center; font-size: 18px; margin: 20px 0; color: #4CAF50; }
                        .footer { text-align: center; padding-top: 20px; border-top: 1px solid #eee; font-size: 12px; color: #777; }
                        .social { margin: 20px 0; text-align: center; }
                        .social a { margin: 0 10px; color: #555; text-decoration: none; }
                        .review-button { display: inline-block; padding: 10px 20px; background: #FF9800; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>¡Gracias por elegirnos</h2>
                        </div>
                        <div class="content">
                            <p>Hola %s,</p>

                            <div class="thank-you">
                                <p>¡Esperamos que hayas tenido una excelente experiencia con nosotros!</p>
                            </div>

                            <p>En Muthabara valoramos mucho a nuestros clientes y nos encantaría saber tu opinión sobre tu experiencia.</p>

                            <p>Aquí tienes algunos detalles de tu visita:</p>
                            <ul>
                                <li><strong>Fecha:</strong> %s</li>
                                <li><strong>Diseño:</strong> %s</li>
                            </ul>

                            <p>Si tienes alguna pregunta sobre el cuidado de tu nuevo tatuaje o necesitas cualquier otra cosa, no dudes en contactarnos.</p>

                        </div>
                        <div class="footer">
                            <p>Muthabara<br>
                            <p>© %s Muthabara. Todos los derechos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        appointment.getCustomerName(),
                        appointment.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        appointment.getDesign() != null ? appointment.getDesign().getTitle() : "Personalizado",
                        myEmail,
                        LocalDate.now().getYear());
    }
}