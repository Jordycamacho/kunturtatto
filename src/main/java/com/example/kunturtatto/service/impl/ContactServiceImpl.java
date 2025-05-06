package com.example.kunturtatto.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.example.kunturtatto.exception.EmailException;
import com.example.kunturtatto.model.Appointment;
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

    private final JavaMailSender javaMailSender;

    @Override
    @Transactional
    public void sendContactEmail(ContactRequest request) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setFrom(myEmail);
            helper.setTo(myEmail);
            helper.setSubject("Nuevo mensaje de contacto: " + request.getSubject());

            String htmlContent = buildContactEmailHtml(request);
            helper.setText(htmlContent, true);
        };

        try {
            javaMailSender.send(messagePreparator);
            log.info("Email de contacto enviado correctamente a {}", myEmail);
        } catch (MailException e) {
            log.error("Error al enviar email de contacto", e);
            throw new EmailException("Error al enviar el email de contacto");
        }
    }

    @Override
    public void sendAppointmentCompletion(Appointment appointment) {
        String subject = "¡Gracias por tu visita! - Kuntur Tattoo";
        sendHtmlEmail(
                appointment.getCustomerEmail(),
                subject,
                buildAppointmentCompletionHtml(appointment));
    }

    public void sendAppointmentConfirmation(Appointment appointment) {
        String subject = "Confirmación de Cita - Kuntur Tattoo";
        sendHtmlEmail(
                appointment.getCustomerEmail(),
                subject,
                buildAppointmentConfirmationHtml(appointment));
    }

    @Override
    public void sendAppointmentUpdateNotification(Appointment appointment) {
        String subject = "Actualización de Cita - Kuntur Tattoo";
        sendHtmlEmail(
                appointment.getCustomerEmail(),
                subject,
                buildAppointmentUpdateHtml(appointment));
    }

    @Override
    public void sendAppointmentCancellation(Appointment appointment) {
        String subject = "Cancelación de Cita - Kuntur Tattoo";
        sendHtmlEmail(
                appointment.getCustomerEmail(),
                subject,
                buildAppointmentCancellationHtml(appointment));
    }

    /* format Mails */
    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(myEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
        };

        try {
            javaMailSender.send(messagePreparator);
            log.info("Email enviado a {}", to);
        } catch (MailException e) {
            log.error("Error al enviar email a {}", to, e);
            throw new EmailException("Error al enviar el email");
        }
    }

    /* Views html */
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
                            <p>Kuntur Tattoo Studio<br>%s</p>
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
                        .button { display: inline-block; padding: 10px 20px; background: #2196F3; color: white; text-decoration: none; border-radius: 5px; }
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

                            <p style="text-align: center; margin-top: 30px;">
                                <a href="mailto:%s" class="button">Confirmar cambios</a>
                            </p>

                            <p>Si no solicitaste este cambio, por favor contáctanos inmediatamente.</p>
                        </div>
                        <div class="footer">
                            <p>Kuntur Tattoo Studio<br>
                            © %s Kuntur Tattoo. Todos los derechos reservados.</p>
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
                            <p>Kuntur Tattoo Studio<br>
                            © %s Kuntur Tattoo. Todos los derechos reservados.</p>
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
                            <h2>¡Gracias por elegir Kuntur Tattoo!</h2>
                        </div>
                        <div class="content">
                            <p>Hola %s,</p>

                            <div class="thank-you">
                                <p>¡Esperamos que hayas tenido una excelente experiencia con nosotros!</p>
                            </div>

                            <p>En Kuntur Tattoo valoramos mucho a nuestros clientes y nos encantaría saber tu opinión sobre tu experiencia.</p>

                            <p>Aquí tienes algunos detalles de tu visita:</p>
                            <ul>
                                <li><strong>Fecha:</strong> %s</li>
                                <li><strong>Diseño:</strong> %s</li>
                            </ul>

                            <p>Si tienes alguna pregunta sobre el cuidado de tu nuevo tatuaje o necesitas cualquier otra cosa, no dudes en contactarnos.</p>

                        </div>
                        <div class="footer">
                            <p>Kuntur Tattoo Studio<br>
                            <p>© %s Kuntur Tattoo. Todos los derechos reservados.</p>
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