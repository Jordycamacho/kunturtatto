package com.example.kunturtatto.service.impl;

import org.springframework.stereotype.Service;

import com.example.kunturtatto.service.RemainderService;

@Service
public class RemainderServiceImpl implements RemainderService {

    @Override
    public void sendMailRemainder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendMailRemainder'");
    }
    /* 
    private static final Logger log = LoggerFactory.getLogger(RemainderServiceImpl.class);

    @Value("${email.sender}")
    private String myEmail;

    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendMailRemainder() {
        List<AppointmentResponse> todayAppointments = appointmentService.getAllAppointments();
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

    private String buildReminderEmailHtml(List<AppointmentResponse> appointments) {
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
                appt.getDesignTitle() != null ? appt.getDesignTitle() : "Sin diseño especificado")));

        sb.append("""
                        </table>
                    </div>
                </body>
                </html>
                """);

        return sb.toString();
    }*/
}
