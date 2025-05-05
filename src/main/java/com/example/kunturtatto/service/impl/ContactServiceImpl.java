package com.example.kunturtatto.service.impl;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
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

    public void sendAppointmentConfirmation(Appointment appointment) {
        String subject = "Confirmación de Cita - Kuntur Tattoo";
        String content = """
                Hola %s,

                Tu cita ha sido confirmada para el %s a las %s.

                Diseño: %s
                Tamaño: %s cm
                Zona: %s

                Por favor llega 15 minutos antes.

                ¡Gracias!
                """.formatted(
                appointment.getCustomerName(),
                appointment.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                appointment.getTime(),
                appointment.getDesign() != null ? appointment.getDesign().getTitle() : "Personalizado",
                appointment.getTattooSize(),
                appointment.getBodyPart());

        sendEmail(appointment.getCustomerEmail(), subject, content);
    }

    public void sendAppointmentUpdateNotification(Appointment appointment) {
        String subject = "Actualización de Cita - Kuntur Tattoo";
        String content = """
                Hola %s,

                Los detalles de tu cita han sido actualizados:

                Nueva fecha: %s
                Nueva hora: %s
                Diseño: %s

                Si tienes alguna pregunta, contáctanos.
                """.formatted(
                appointment.getCustomerName(),
                appointment.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                appointment.getTime(),
                appointment.getDesign() != null ? appointment.getDesign().getTitle() : "Personalizado");

        sendEmail(appointment.getCustomerEmail(), subject, content);
    }

    public void sendAppointmentCancellation(Appointment appointment) {
        String subject = "Cancelación de Cita - Kuntur Tattoo";
        String content = """
                Hola %s,

                Tu cita programada para el %s a las %s ha sido cancelada.

                Si esto fue un error o deseas reprogramar, por favor contáctanos.

                ¡Lamentamos cualquier inconveniente!
                """.formatted(
                appointment.getCustomerName(),
                appointment.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                appointment.getTime());

        sendEmail(appointment.getCustomerEmail(), subject, content);
    }

    private void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        try {
            javaMailSender.send(message);
            log.info("Email enviado a {}", to);
        } catch (MailException e) {
            log.error("Error al enviar email a {}", to, e);
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
}