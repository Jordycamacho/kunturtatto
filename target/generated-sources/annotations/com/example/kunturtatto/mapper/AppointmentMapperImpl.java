package com.example.kunturtatto.mapper;

import com.example.kunturtatto.model.Appointment;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.request.AppointmentRequest;
import com.example.kunturtatto.request.AppointmentResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-15T16:08:09+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.7 (Oracle Corporation)"
)
@Component
public class AppointmentMapperImpl implements AppointmentMapper {

    @Override
    public Appointment toEntity(AppointmentRequest request) {
        if ( request == null ) {
            return null;
        }

        Appointment.AppointmentBuilder appointment = Appointment.builder();

        appointment.customerName( request.getCustomerName() );
        appointment.customerEmail( request.getCustomerEmail() );
        appointment.customerPhone( request.getCustomerPhone() );
        appointment.date( request.getDate() );
        appointment.time( request.getTime() );
        appointment.price( request.getPrice() );
        appointment.tattooSize( request.getTattooSize() );
        appointment.bodyPart( request.getBodyPart() );
        appointment.referenceLinks( request.getReferenceLinks() );
        appointment.customDescription( request.getCustomDescription() );

        return appointment.build();
    }

    @Override
    public AppointmentResponse toResponse(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }

        AppointmentResponse.AppointmentResponseBuilder appointmentResponse = AppointmentResponse.builder();

        appointmentResponse.designTitle( appointmentDesignTitle( appointment ) );
        appointmentResponse.id( appointment.getId() );
        appointmentResponse.customerName( appointment.getCustomerName() );
        appointmentResponse.customerEmail( appointment.getCustomerEmail() );
        appointmentResponse.customerPhone( appointment.getCustomerPhone() );
        appointmentResponse.date( appointment.getDate() );
        appointmentResponse.time( appointment.getTime() );
        appointmentResponse.price( appointment.getPrice() );
        appointmentResponse.status( appointment.getStatus() );
        appointmentResponse.tattooSize( appointment.getTattooSize() );
        appointmentResponse.bodyPart( appointment.getBodyPart() );
        appointmentResponse.referenceLinks( appointment.getReferenceLinks() );
        appointmentResponse.customDescription( appointment.getCustomDescription() );
        appointmentResponse.createdAt( appointment.getCreatedAt() );
        appointmentResponse.updatedAt( appointment.getUpdatedAt() );

        return appointmentResponse.build();
    }

    private String appointmentDesignTitle(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }
        Design design = appointment.getDesign();
        if ( design == null ) {
            return null;
        }
        String title = design.getTitle();
        if ( title == null ) {
            return null;
        }
        return title;
    }
}
