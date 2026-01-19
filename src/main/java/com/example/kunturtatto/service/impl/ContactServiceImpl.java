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

            log.info(
                    "[EMAIL_SERVICE] Email de contacto enviado exitosamente. AuditId={}, destinatario={}, tiempoEjecucion={}ms",
                    auditId, myEmail, executionTime);

            saveAuditLog(
                    auditId,
                    "CONTACT_EMAIL",
                    request.getEmail(),
                    myEmail,
                    "Nuevo mensaje de contacto: " + request.getSubject(),
                    "SUCCESS",
                    executionTime,
                    null);

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
                    e.getMessage());

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
                "APPOINTMENT_COMPLETION");
    }

    @Override
    @Transactional
    public void sendAppointmentConfirmation(Appointment appointment) {
        String subject = "Confirmación de Cita - Muthabara";
        sendAppointmentEmail(
                appointment,
                subject,
                buildAppointmentConfirmationHtml(appointment),
                "APPOINTMENT_CONFIRMATION");
    }

    @Override
    @Transactional
    public void sendAppointmentUpdateNotification(Appointment appointment) {
        String subject = "Actualización de Cita - Muthabara";
        sendAppointmentEmail(
                appointment,
                subject,
                buildAppointmentUpdateHtml(appointment),
                "APPOINTMENT_UPDATE");
    }

    @Override
    @Transactional
    public void sendAppointmentCancellation(Appointment appointment) {
        String subject = "Cancelación de Cita - Muthabara";
        sendAppointmentEmail(
                appointment,
                subject,
                buildAppointmentCancellationHtml(appointment),
                "APPOINTMENT_CANCELLATION");
    }

    // ========= GENERIC EMAIL SENDING METHOD ========= //
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
                    appointment.getId());

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
                    appointment.getId());

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

    // ========= BUILD HTML CONTENT METHODS ========= //
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
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Confirmación de Cita - Muthabara</title>
                    <style>
                        /* Estilos basados en la web Muthabara */
                        :root {
                            --primary-color: #58173a;
                            --primary-hover: #6c094b;
                            --title-color: #f0f0f0;
                            --text-color: #cccccc;
                            --body-color: #000000;
                            --container-color: #0a0a0a;
                            --border-color: #333333;
                        }

                        body {
                            margin: 0;
                            padding: 0;
                            background-color: var(--body-color);
                            font-family: 'Montserrat', Arial, sans-serif;
                            color: var(--text-color);
                            line-height: 1.6;
                        }

                        .email-container {
                            max-width: 600px;
                            margin: 0 auto;
                            background-color: var(--container-color);
                            border-radius: 10px;
                            overflow: hidden;
                        }

                        .email-header {
                            background-color: var(--primary-color);
                            padding: 30px 20px;
                            text-align: center;
                        }

                        .email-header h1 {
                            font-family: 'Cinzel', serif;
                            color: white;
                            margin: 0;
                            font-size: 28px;
                            letter-spacing: 1px;
                        }

                        .email-header p {
                            color: rgba(255, 255, 255, 0.8);
                            margin-top: 10px;
                            font-size: 14px;
                        }

                        .email-body {
                            padding: 30px;
                        }

                        .greeting {
                            font-size: 18px;
                            margin-bottom: 20px;
                        }

                        .appointment-details {
                            background-color: rgba(88, 23, 58, 0.1);
                            border: 1px solid rgba(88, 23, 58, 0.3);
                            border-radius: 8px;
                            padding: 20px;
                            margin: 25px 0;
                        }

                        .detail-row {
                            display: flex;
                            justify-content: space-between;
                            padding: 10px 0;
                            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
                        }

                        .detail-row:last-child {
                            border-bottom: none;
                        }

                        .detail-label {
                            font-weight: 600;
                            color: var(--title-color);
                        }

                        .detail-value {
                            text-align: right;
                            color: var(--primary-color);
                            font-weight: 600;
                        }

                        .important-note {
                            background-color: rgba(255, 152, 0, 0.1);
                            border-left: 4px solid #ff9800;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 0 8px 8px 0;
                        }

                        .action-section {
                            margin: 30px 0;
                            padding: 20px;
                            background-color: rgba(255, 255, 255, 0.05);
                            border-radius: 8px;
                        }

                        .action-title {
                            font-family: 'Cinzel', serif;
                            color: var(--title-color);
                            font-size: 18px;
                            margin-bottom: 15px;
                            display: flex;
                            align-items: center;
                            gap: 10px;
                        }

                        .action-buttons {
                            display: flex;
                            flex-direction: column;
                            gap: 15px;
                            margin-top: 20px;
                        }

                        .action-button {
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            gap: 10px;
                            background-color: var(--primary-color);
                            color: white !important;
                            text-decoration: none;
                            padding: 12px 20px;
                            border-radius: 5px;
                            font-weight: 600;
                            transition: background-color 0.3s ease;
                            text-align: center;
                        }

                        .action-button:hover {
                            background-color: var(--primary-hover) !important;
                        }

                        .action-button.secondary {
                            background-color: #2c3e50;
                        }

                        .action-button.secondary:hover {
                            background-color: #34495e !important;
                        }

                        .contact-info {
                            text-align: center;
                            margin: 25px 0;
                            padding-top: 20px;
                            border-top: 1px solid var(--border-color);
                        }

                        .email-footer {
                            background-color: #111111;
                            padding: 20px;
                            text-align: center;
                            border-top: 1px solid var(--border-color);
                        }

                        .footer-text {
                            font-size: 12px;
                            color: #888;
                            margin: 5px 0;
                        }

                        @media screen and (max-width: 600px) {
                            .email-body {
                                padding: 20px 15px;
                            }

                            .detail-row {
                                flex-direction: column;
                                gap: 5px;
                            }

                            .detail-value {
                                text-align: left;
                            }
                        }
                    </style>
                    <link href="https://fonts.googleapis.com/css2?family=Cinzel:wght@400;500;600;700&family=Montserrat:wght@400;500;600;700&display=swap" rel="stylesheet">
                    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
                </head>
                <body>
                    <div class="email-container">
                        <div class="email-header">
                            <h1>MUTHABARA</h1>
                            <p>Estudio de Tatuajes - Confirmación de Cita</p>
                        </div>

                        <div class="email-body">
                            <div class="greeting">
                                <h2 style="margin: 0; color: var(--title-color);">Hola %s,</h2>
                                <p>Hemos recibido tu solicitud de cita. Aquí están los detalles:</p>
                            </div>

                            <div class="appointment-details">
                                <h3 style="color: var(--title-color); margin-top: 0; text-align: center;">
                                    <i class="far fa-calendar-alt"></i> Detalles de la Cita
                                </h3>

                                <div class="detail-row">
                                    <span class="detail-label">Fecha:</span>
                                    <span class="detail-value">%s</span>
                                </div>

                                <div class="detail-row">
                                    <span class="detail-label">Hora:</span>
                                    <span class="detail-value">%s</span>
                                </div>

                                <div class="detail-row">
                                    <span class="detail-label">Diseño:</span>
                                    <span class="detail-value">%s</span>
                                </div>
                            </div>

                            <div class="important-note">
                                <p><strong><i class="fas fa-exclamation-circle"></i> Importante:</strong> Para confirmar definitivamente esta cita, será necesario completar los siguientes pasos:</p>
                            </div>

                            <div class="action-section">
                                <h3 class="action-title">
                                    <i class="fas fa-edit"></i> Paso 1: Detalles del Diseño
                                </h3>
                                <p>Para poder preparar tu diseño personalizado, necesitamos que completes nuestro formulario de consulta especializado:</p>

                                <div class="action-buttons">
                                    <a href="https://muthabara.cloud/Muthabara/consulta-tatuaje" class="action-button" target="_blank">
                                        <i class="fas fa-pencil-alt"></i> Completar Formulario de Consulta
                                    </a>
                                </div>
                            </div>

                            <div class="action-section">
                                <h3 class="action-title">
                                    <i class="fas fa-euro-sign"></i> Paso 2: Señal de Reserva
                                </h3>
                                <p>Para reservar tu fecha y hora, es necesario realizar una señal de 50€. Esta señal:</p>
                                <ul style="margin: 10px 0 20px 20px;">
                                    <li>Garantiza tu fecha y hora exclusiva</li>
                                    <li>Cubre los trabajos previos de diseño</li>
                                    <li>Se descuenta del total final de tu tatuaje</li>
                                </ul>

                                <div class="action-buttons">
                                    <a href="https://muthabara.cloud/Muthabara/instrucciones-senal" class="action-button secondary" target="_blank">
                                        <i class="fas fa-file-contract"></i> Leer Instrucciones de Señal
                                    </a>
                                </div>
                            </div>

                            <div class="important-note">
                                <p><strong><i class="fas fa-clock"></i> Tiempo límite:</strong> Por favor, completa estos pasos dentro de las próximas 24 horas para asegurar tu cita.</p>
                            </div>

                            <div class="contact-info">
                                <p><strong>¿Necesitas ayuda o tienes preguntas?</strong></p>
                                <p>Contáctanos en:</p>
                                <p>
                                    <a href="mailto:muthabara_art@outlook.com" style="color: var(--primary-color); text-decoration: none;">
                                        <i class="fas fa-envelope"></i> muthabara_art@outlook.com
                                    </a>
                                </p>
                            </div>
                        </div>

                        <div class="email-footer">
                            <p class="footer-text">
                                <i class="far fa-calendar-check"></i> Nos vemos pronto en el estudio
                            </p>
                            <p class="footer-text">
                                Muthabara - Estudio de Tatuajes<br>
                                <a href="https://muthabara.cloud" style="color: #888; text-decoration: none;">muthabara.cloud</a>
                            </p>
                            <p class="footer-text">
                                © %s Muthabara. Todos los derechos reservados.
                            </p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        appointment.getCustomerName(),
                        appointment.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        appointment.getTime(),
                        appointment.getDesign() != null ? appointment.getDesign().getTitle() : "Diseño Personalizado",
                        LocalDate.now().getYear());
    }

    private String buildAppointmentUpdateHtml(Appointment appointment) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Actualización de Cita - Muthabara</title>
                    <style>
                        /* Estilos Muthabara actualizados */
                        :root {
                            --primary-color: #58173a;
                            --primary-hover: #6c094b;
                            --warning-color: #ff9800;
                            --title-color: #f0f0f0;
                            --text-color: #cccccc;
                            --body-color: #000000;
                            --container-color: #0a0a0a;
                            --border-color: #333333;
                        }

                        body {
                            margin: 0;
                            padding: 0;
                            background-color: var(--body-color);
                            font-family: 'Montserrat', Arial, sans-serif;
                            color: var(--text-color);
                            line-height: 1.6;
                        }

                        .email-container {
                            max-width: 600px;
                            margin: 0 auto;
                            background-color: var(--container-color);
                            border-radius: 10px;
                            overflow: hidden;
                        }

                        .email-header {
                            background-color: var(--warning-color);
                            padding: 30px 20px;
                            text-align: center;
                        }

                        .email-header h1 {
                            font-family: 'Cinzel', serif;
                            color: white;
                            margin: 0;
                            font-size: 28px;
                            letter-spacing: 1px;
                        }

                        .email-body {
                            padding: 30px;
                        }

                        .greeting {
                            font-size: 18px;
                            margin-bottom: 20px;
                        }

                        .appointment-details {
                            background-color: rgba(255, 152, 0, 0.1);
                            border: 1px solid rgba(255, 152, 0, 0.3);
                            border-radius: 8px;
                            padding: 20px;
                            margin: 25px 0;
                        }

                        .detail-row {
                            display: flex;
                            justify-content: space-between;
                            padding: 10px 0;
                            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
                        }

                        .detail-row:last-child {
                            border-bottom: none;
                        }

                        .detail-label {
                            font-weight: 600;
                            color: var(--title-color);
                        }

                        .detail-value {
                            text-align: right;
                            color: var(--warning-color);
                            font-weight: 600;
                        }

                        .update-info {
                            background-color: rgba(255, 152, 0, 0.1);
                            border-left: 4px solid var(--warning-color);
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 0 8px 8px 0;
                        }

                        .action-section {
                            margin: 30px 0;
                            padding: 20px;
                            background-color: rgba(255, 255, 255, 0.05);
                            border-radius: 8px;
                        }

                        .action-title {
                            font-family: 'Cinzel', serif;
                            color: var(--title-color);
                            font-size: 18px;
                            margin-bottom: 15px;
                            display: flex;
                            align-items: center;
                            gap: 10px;
                        }

                        .action-buttons {
                            display: flex;
                            flex-direction: column;
                            gap: 15px;
                            margin-top: 20px;
                        }

                        .action-button {
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            gap: 10px;
                            background-color: var(--primary-color);
                            color: white !important;
                            text-decoration: none;
                            padding: 12px 20px;
                            border-radius: 5px;
                            font-weight: 600;
                            transition: background-color 0.3s ease;
                            text-align: center;
                        }

                        .action-button:hover {
                            background-color: var(--primary-hover) !important;
                        }

                        .action-button.warning {
                            background-color: var(--warning-color);
                        }

                        .action-button.warning:hover {
                            background-color: #e68900 !important;
                        }

                        .email-footer {
                            background-color: #111111;
                            padding: 20px;
                            text-align: center;
                            border-top: 1px solid var(--border-color);
                        }

                        @media screen and (max-width: 600px) {
                            .email-body {
                                padding: 20px 15px;
                            }

                            .detail-row {
                                flex-direction: column;
                                gap: 5px;
                            }

                            .detail-value {
                                text-align: left;
                            }
                        }
                    </style>
                    <link href="https://fonts.googleapis.com/css2?family=Cinzel:wght@400;500;600;700&family=Montserrat:wght@400;500;600;700&display=swap" rel="stylesheet">
                    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
                </head>
                <body>
                    <div class="email-container">
                        <div class="email-header">
                            <h1>MUTHABARA</h1>
                            <p>Actualización de Cita</p>
                        </div>

                        <div class="email-body">
                            <div class="greeting">
                                <h2 style="margin: 0; color: var(--title-color);">Hola %s,</h2>
                                <p>Queríamos informarte que los detalles de tu cita han sido actualizados:</p>
                            </div>

                            <div class="appointment-details">
                                <h3 style="color: var(--title-color); margin-top: 0; text-align: center;">
                                    <i class="fas fa-sync-alt"></i> Nuevos Detalles
                                </h3>

                                <div class="detail-row">
                                    <span class="detail-label">Nueva fecha:</span>
                                    <span class="detail-value">%s</span>
                                </div>

                                <div class="detail-row">
                                    <span class="detail-label">Nueva hora:</span>
                                    <span class="detail-value">%s</span>
                                </div>

                                <div class="detail-row">
                                    <span class="detail-label">Diseño:</span>
                                    <span class="detail-value">%s</span>
                                </div>
                            </div>

                            <div class="update-info">
                                <p><strong><i class="fas fa-exclamation-circle"></i> Importante:</strong> Por favor toma nota de los cambios realizados.</p>
                                <p>Si estos cambios no te funcionan, contáctanos para reprogramar.</p>
                            </div>

                            <div class="action-section">
                                <h3 class="action-title">
                                    <i class="fas fa-question-circle"></i> ¿No solicitaste este cambio?
                                </h3>
                                <p>Si no solicitaste este cambio, por favor contáctanos inmediatamente.</p>

                                <div class="action-buttons">
                                    <a href="mailto:%s?subject=Consulta sobre cambios en mi cita" class="action-button warning">
                                        <i class="fas fa-exclamation-triangle"></i> Contactar Inmediatamente
                                    </a>
                                </div>
                            </div>

                            <div class="action-section">
                                <h3 class="action-title">
                                    <i class="fas fa-edit"></i> ¿Necesitas más información?
                                </h3>
                                <p>Recuerda que puedes consultar nuestros formularios y guías:</p>

                                <div class="action-buttons">
                                    <a href="https://muthabara.cloud/Muthabara/consulta-tatuaje" class="action-button" target="_blank">
                                        <i class="fas fa-pencil-alt"></i> Formulario de Consulta
                                    </a>

                                    <a href="https://muthabara.cloud/Muthabara/instrucciones-senal" class="action-button" target="_blank">
                                        <i class="fas fa-file-contract"></i> Instrucciones de Señal
                                    </a>
                                </div>
                            </div>

                            <div style="text-align: center; margin: 25px 0; padding-top: 20px; border-top: 1px solid var(--border-color);">
                                <p><strong>Contacto rápido:</strong></p>
                                <p>
                                    <a href="mailto:%s" style="color: var(--primary-color); text-decoration: none;">
                                        <i class="fas fa-envelope"></i> %s
                                    </a>
                                </p>
                            </div>
                        </div>

                        <div class="email-footer">
                            <p style="margin: 0 0 10px 0; font-size: 14px; color: #888;">
                                Muthabara - Estudio de Tatuajes
                            </p>
                            <p style="margin: 0; font-size: 12px; color: #666;">
                                © %s Muthabara. Todos los derechos reservados.
                            </p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        appointment.getCustomerName(),
                        appointment.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        appointment.getTime(),
                        appointment.getDesign() != null ? appointment.getDesign().getTitle() : "Diseño Personalizado",
                        myEmail,
                        myEmail,
                        myEmail,
                        LocalDate.now().getYear());
    }

    private String buildAppointmentCancellationHtml(Appointment appointment) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Cancelación de Cita - Muthabara</title>
                    <style>
                        /* Estilos Muthabara actualizados */
                        :root {
                            --primary-color: #58173a;
                            --primary-hover: #6c094b;
                            --error-color: #d32f2f;
                            --success-color: #4CAF50;
                            --title-color: #f0f0f0;
                            --text-color: #cccccc;
                            --body-color: #000000;
                            --container-color: #0a0a0a;
                            --border-color: #333333;
                        }

                        body {
                            margin: 0;
                            padding: 0;
                            background-color: var(--body-color);
                            font-family: 'Montserrat', Arial, sans-serif;
                            color: var(--text-color);
                            line-height: 1.6;
                        }

                        .email-container {
                            max-width: 600px;
                            margin: 0 auto;
                            background-color: var(--container-color);
                            border-radius: 10px;
                            overflow: hidden;
                        }

                        .email-header {
                            background-color: var(--error-color);
                            padding: 30px 20px;
                            text-align: center;
                        }

                        .email-header h1 {
                            font-family: 'Cinzel', serif;
                            color: white;
                            margin: 0;
                            font-size: 28px;
                            letter-spacing: 1px;
                        }

                        .email-body {
                            padding: 30px;
                        }

                        .greeting {
                            font-size: 18px;
                            margin-bottom: 20px;
                        }

                        .appointment-details {
                            background-color: rgba(211, 47, 47, 0.1);
                            border: 1px solid rgba(211, 47, 47, 0.3);
                            border-radius: 8px;
                            padding: 20px;
                            margin: 25px 0;
                        }

                        .detail-row {
                            display: flex;
                            justify-content: space-between;
                            padding: 10px 0;
                            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
                        }

                        .detail-row:last-child {
                            border-bottom: none;
                        }

                        .detail-label {
                            font-weight: 600;
                            color: var(--title-color);
                        }

                        .detail-value {
                            text-align: right;
                            color: var(--error-color);
                            font-weight: 600;
                        }

                        .reschedule-box {
                            background-color: rgba(76, 175, 80, 0.1);
                            border-left: 4px solid var(--success-color);
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 0 8px 8px 0;
                        }

                        .action-section {
                            margin: 30px 0;
                            padding: 20px;
                            background-color: rgba(255, 255, 255, 0.05);
                            border-radius: 8px;
                        }

                        .action-title {
                            font-family: 'Cinzel', serif;
                            color: var(--title-color);
                            font-size: 18px;
                            margin-bottom: 15px;
                            display: flex;
                            align-items: center;
                            gap: 10px;
                        }

                        .action-buttons {
                            display: flex;
                            flex-direction: column;
                            gap: 15px;
                            margin-top: 20px;
                        }

                        .action-button {
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            gap: 10px;
                            background-color: var(--error-color);
                            color: white !important;
                            text-decoration: none;
                            padding: 12px 20px;
                            border-radius: 5px;
                            font-weight: 600;
                            transition: background-color 0.3s ease;
                            text-align: center;
                        }

                        .action-button:hover {
                            background-color: #b71c1c !important;
                        }

                        .action-button.success {
                            background-color: var(--success-color);
                        }

                        .action-button.success:hover {
                            background-color: #388e3c !important;
                        }

                        .action-button.secondary {
                            background-color: var(--primary-color);
                        }

                        .action-button.secondary:hover {
                            background-color: var(--primary-hover) !important;
                        }

                        .email-footer {
                            background-color: #111111;
                            padding: 20px;
                            text-align: center;
                            border-top: 1px solid var(--border-color);
                        }

                        @media screen and (max-width: 600px) {
                            .email-body {
                                padding: 20px 15px;
                            }

                            .detail-row {
                                flex-direction: column;
                                gap: 5px;
                            }

                            .detail-value {
                                text-align: left;
                            }
                        }
                    </style>
                    <link href="https://fonts.googleapis.com/css2?family=Cinzel:wght@400;500;600;700&family=Montserrat:wght@400;500;600;700&display=swap" rel="stylesheet">
                    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
                </head>
                <body>
                    <div class="email-container">
                        <div class="email-header">
                            <h1>MUTHABARA</h1>
                            <p>Cancelación de Cita</p>
                        </div>

                        <div class="email-body">
                            <div class="greeting">
                                <h2 style="margin: 0; color: var(--title-color);">Hola %s,</h2>
                                <p>Lamentamos informarte que tu cita ha sido cancelada:</p>
                            </div>

                            <div class="appointment-details">
                                <h3 style="color: var(--title-color); margin-top: 0; text-align: center;">
                                    <i class="fas fa-calendar-times"></i> Detalles Cancelados
                                </h3>

                                <div class="detail-row">
                                    <span class="detail-label">Fecha:</span>
                                    <span class="detail-value">%s</span>
                                </div>

                                <div class="detail-row">
                                    <span class="detail-label">Hora:</span>
                                    <span class="detail-value">%s</span>
                                </div>

                                <div class="detail-row">
                                    <span class="detail-label">Diseño:</span>
                                    <span class="detail-value">%s</span>
                                </div>
                            </div>

                            <div class="reschedule-box">
                                <h3 class="action-title">
                                    <i class="fas fa-question-circle"></i> ¿Fue un error o deseas reprogramar?
                                </h3>
                                <p>Si deseas reagendar tu cita, estamos disponibles para ayudarte.</p>

                                <div class="action-buttons">
                                    <a href="mailto:%s?subject=Reprogramar cita cancelada" class="action-button success">
                                        <i class="fas fa-calendar-plus"></i> Contactar para Reprogramar
                                    </a>
                                </div>
                            </div>

                            <div class="action-section">
                                <h3 class="action-title">
                                    <i class="fas fa-lightbulb"></i> Próximos Pasos
                                </h3>
                                <p>Si quieres comenzar un nuevo proyecto, puedes usar nuestros recursos:</p>

                                <div class="action-buttons">
                                    <a href="https://muthabara.cloud/Muthabara/consulta-tatuaje" class="action-button secondary" target="_blank">
                                        <i class="fas fa-edit"></i> Nuevo Formulario de Consulta
                                    </a>

                                    <a href="https://muthabara.cloud/Muthabara/instrucciones-senal" class="action-button secondary" target="_blank">
                                        <i class="fas fa-file-contract"></i> Instrucciones de Señal
                                    </a>
                                </div>
                            </div>

                            <p style="text-align: center; margin: 30px 0; color: #888;">
                                Lamentamos cualquier inconveniente que esto pueda causarte y esperamos poder atenderte en el futuro.
                            </p>
                        </div>

                        <div class="email-footer">
                            <p style="margin: 0 0 10px 0; font-size: 14px; color: #888;">
                                Muthabara - Estudio de Tatuajes
                            </p>
                            <p style="margin: 0; font-size: 12px; color: #666;">
                                © %s Muthabara. Todos los derechos reservados.
                            </p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        appointment.getCustomerName(),
                        appointment.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        appointment.getTime(),
                        appointment.getDesign() != null ? appointment.getDesign().getTitle() : "Diseño Personalizado",
                        myEmail,
                        LocalDate.now().getYear());
    }

    private String buildAppointmentCompletionHtml(Appointment appointment) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>¡Gracias por tu visita! - Muthabara</title>
                    <style>
                        /* Estilos Muthabara actualizados */
                        :root {
                            --primary-color: #58173a;
                            --primary-hover: #6c094b;
                            --success-color: #4CAF50;
                            --review-color: #FF9800;
                            --title-color: #f0f0f0;
                            --text-color: #cccccc;
                            --body-color: #000000;
                            --container-color: #0a0a0a;
                            --border-color: #333333;
                        }

                        body {
                            margin: 0;
                            padding: 0;
                            background-color: var(--body-color);
                            font-family: 'Montserrat', Arial, sans-serif;
                            color: var(--text-color);
                            line-height: 1.6;
                        }

                        .email-container {
                            max-width: 600px;
                            margin: 0 auto;
                            background-color: var(--container-color);
                            border-radius: 10px;
                            overflow: hidden;
                        }

                        .email-header {
                            background-color: var(--success-color);
                            padding: 30px 20px;
                            text-align: center;
                        }

                        .email-header h1 {
                            font-family: 'Cinzel', serif;
                            color: white;
                            margin: 0;
                            font-size: 28px;
                            letter-spacing: 1px;
                        }

                        .email-body {
                            padding: 30px;
                        }

                        .greeting {
                            font-size: 18px;
                            margin-bottom: 20px;
                        }

                        .thank-you-section {
                            text-align: center;
                            font-size: 20px;
                            color: var(--success-color);
                            padding: 25px;
                            margin: 25px 0;
                            background-color: rgba(76, 175, 80, 0.1);
                            border-radius: 10px;
                            border: 1px solid rgba(76, 175, 80, 0.3);
                        }

                        .visit-details {
                            background-color: rgba(88, 23, 58, 0.1);
                            border: 1px solid rgba(88, 23, 58, 0.3);
                            border-radius: 8px;
                            padding: 20px;
                            margin: 25px 0;
                        }

                        .detail-row {
                            display: flex;
                            justify-content: space-between;
                            padding: 10px 0;
                            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
                        }

                        .detail-row:last-child {
                            border-bottom: none;
                        }

                        .detail-label {
                            font-weight: 600;
                            color: var(--title-color);
                        }

                        .detail-value {
                            text-align: right;
                            color: var(--success-color);
                            font-weight: 600;
                        }

                        .care-reminder {
                            background-color: rgba(255, 152, 0, 0.1);
                            border-left: 4px solid var(--review-color);
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 0 8px 8px 0;
                        }

                        .action-section {
                            margin: 30px 0;
                            padding: 20px;
                            background-color: rgba(255, 255, 255, 0.05);
                            border-radius: 8px;
                        }

                        .action-title {
                            font-family: 'Cinzel', serif;
                            color: var(--title-color);
                            font-size: 18px;
                            margin-bottom: 15px;
                            display: flex;
                            align-items: center;
                            gap: 10px;
                        }

                        .action-buttons {
                            display: flex;
                            flex-direction: column;
                            gap: 15px;
                            margin-top: 20px;
                        }

                        .action-button {
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            gap: 10px;
                            background-color: var(--primary-color);
                            color: white !important;
                            text-decoration: none;
                            padding: 12px 20px;
                            border-radius: 5px;
                            font-weight: 600;
                            transition: background-color 0.3s ease;
                            text-align: center;
                        }

                        .action-button:hover {
                            background-color: var(--primary-hover) !important;
                        }

                        .action-button.review {
                            background-color: var(--review-color);
                        }

                        .action-button.review:hover {
                            background-color: #e68900 !important;
                        }

                        .social-links {
                            display: flex;
                            justify-content: center;
                            gap: 20px;
                            margin: 30px 0;
                            flex-wrap: wrap;
                        }

                        .social-link {
                            color: var(--text-color);
                            text-decoration: none;
                            font-size: 14px;
                            display: flex;
                            align-items: center;
                            gap: 8px;
                            padding: 8px 16px;
                            background-color: rgba(255, 255, 255, 0.05);
                            border-radius: 5px;
                            transition: all 0.3s ease;
                        }

                        .social-link:hover {
                            color: var(--primary-color);
                            background-color: rgba(88, 23, 58, 0.1);
                        }

                        .email-footer {
                            background-color: #111111;
                            padding: 20px;
                            text-align: center;
                            border-top: 1px solid var(--border-color);
                        }

                        @media screen and (max-width: 600px) {
                            .email-body {
                                padding: 20px 15px;
                            }

                            .detail-row {
                                flex-direction: column;
                                gap: 5px;
                            }

                            .detail-value {
                                text-align: left;
                            }

                            .social-links {
                                flex-direction: column;
                                align-items: center;
                            }
                        }
                    </style>
                    <link href="https://fonts.googleapis.com/css2?family=Cinzel:wght@400;500;600;700&family=Montserrat:wght@400;500;600;700&display=swap" rel="stylesheet">
                    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
                </head>
                <body>
                    <div class="email-container">
                        <div class="email-header">
                            <h1>MUTHABARA</h1>
                            <p>¡Gracias por tu visita!</p>
                        </div>

                        <div class="email-body">
                            <div class="greeting">
                                <h2 style="margin: 0; color: var(--title-color);">Hola %s,</h2>
                            </div>

                            <div class="thank-you-section">
                                <p style="margin: 0; font-size: 24px;">
                                    <i class="fas fa-heart"></i> ¡Esperamos que hayas tenido una excelente experiencia con nosotros!
                                </p>
                            </div>

                            <div class="visit-details">
                                <h3 style="color: var(--title-color); margin-top: 0; text-align: center;">
                                    <i class="far fa-calendar-check"></i> Resumen de tu Visita
                                </h3>

                                <div class="detail-row">
                                    <span class="detail-label">Fecha:</span>
                                    <span class="detail-value">%s</span>
                                </div>

                                <div class="detail-row">
                                    <span class="detail-label">Diseño:</span>
                                    <span class="detail-value">%s</span>
                                </div>
                            </div>

                            <div class="care-reminder">
                                <p><strong><i class="fas fa-first-aid"></i> Recordatorio de cuidados:</strong></p>
                                <p>Recuerda seguir las instrucciones de cuidado que te dimos para asegurar la perfecta cicatrización de tu tatuaje.</p>
                            </div>

                            <div class="action-section">
                                <h3 class="action-title">
                                    <i class="fas fa-comment-medical"></i> ¿Necesitas ayuda con los cuidados?
                                </h3>
                                <p>Si tienes alguna pregunta sobre el cuidado de tu nuevo tatuaje, no dudes en contactarnos.</p>

                                <div class="action-buttons">
                                    <a href="mailto:%s?subject=Consulta sobre cuidados del tatuaje" class="action-button">
                                        <i class="fas fa-question-circle"></i> Consultar sobre Cuidados
                                    </a>
                                </div>
                            </div>

                            <div class="action-section">
                                <h3 class="action-title">
                                    <i class="fas fa-plus-circle"></i> ¿Listo para tu próximo proyecto?
                                </h3>
                                <p>Cuando estés listo para tu próximo tatuaje, recuerda que puedes usar nuestro formulario de consulta:</p>

                                <div class="action-buttons">
                                    <a href="https://muthabara.cloud/Muthabara/consulta-tatuaje" class="action-button" target="_blank">
                                        <i class="fas fa-edit"></i> Formulario de Consulta
                                    </a>
                                </div>
                            </div>

                            <div class="social-links">
                                <a href="https://www.instagram.com/muthabara_ink" class="social-link" target="_blank">
                                    <i class="fab fa-instagram"></i> Síguenos en Instagram
                                </a>
                                <a href="https://muthabara.cloud" class="social-link" target="_blank">
                                    <i class="fas fa-globe"></i> Visita Nuestra Web
                                </a>
                            </div>
                        </div>

                        <div class="email-footer">
                            <p style="margin: 0 0 10px 0; font-size: 14px; color: #888;">
                                Muthabara - Estudio de Tatuajes
                            </p>
                            <p style="margin: 0; font-size: 12px; color: #666;">
                                © %s Muthabara. Todos los derechos reservados.
                            </p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        appointment.getCustomerName(),
                        appointment.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        appointment.getDesign() != null ? appointment.getDesign().getTitle() : "Diseño Personalizado",
                        myEmail,
                        LocalDate.now().getYear());
    }
}