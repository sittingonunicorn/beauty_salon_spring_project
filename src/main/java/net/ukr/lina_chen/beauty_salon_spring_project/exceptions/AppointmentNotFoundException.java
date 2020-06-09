package net.ukr.lina_chen.beauty_salon_spring_project.exceptions;

public class AppointmentNotFoundException extends  Exception {
    public AppointmentNotFoundException(Long appointmentId) {
        super("appointment with id " + appointmentId + " not found");

    }
}
