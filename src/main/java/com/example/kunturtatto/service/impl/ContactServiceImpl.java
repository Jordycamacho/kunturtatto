package com.example.kunturtatto.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.kunturtatto.exception.EmailException;
import com.example.kunturtatto.model.Appointment;
import com.example.kunturtatto.request.ContactRequest;
import com.example.kunturtatto.service.IAppointmentService;
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
    private final IAppointmentService appointmentService;

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
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendMailRemainder() {
        List<Appointment> todayAppointments = appointmentService.getTodaysAppointments();
        if (todayAppointments.isEmpty()) {
            return;
        }

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setFrom(myEmail);
            helper.setTo(myEmail);
            helper.setSubject("Recordatorio de Citas del Día");

            String htmlContent = buildReminderEmailHtml(todayAppointments);
            helper.setText(htmlContent, true);
        };

        try {
            javaMailSender.send(messagePreparator);
            log.info("Recordatorio de citas enviado correctamente");
        } catch (MailException e) {
            log.error("Error al enviar recordatorio de citas", e);
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

    private String buildReminderEmailHtml(List<Appointment> appointments) {
        StringBuilder sb = new StringBuilder("""
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; }
                        .container { max-width: 600px; margin: 0 auto; }
                        .header { color: #333; border-bottom: 1px solid #eee; }
                        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                        th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }
                        th { background-color: #f5f5f5; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>Recordatorio de Citas</h2>
                            <p>Tienes %d citas programadas para hoy:</p>
                        </div>

                        <table>
                            <tr>
                                <th>Hora</th>
                                <th>Email</th>
                                <th>Diseño</th>
                            </tr>
                """.formatted(appointments.size()));

        appointments.forEach(appt -> sb.append("""
                            <tr>
                                <td>%s</td>
                                <td>%s</td>
                                <td>%s</td>
                            </tr>
                """.formatted(
                appt.getTime(),
                appt.getCustomerEmail(),
                appt.getDesign() != null ? appt.getDesign() : "Sin diseño especificado")));

        sb.append("""
                        </table>
                    </div>
                </body>
                </html>
                """);

        return sb.toString();
    }
}