package net.ukr.lina_chen.beauty_salon_spring_project.exceptions;

public class DoubleTimeRequestException extends Exception {
    public DoubleTimeRequestException(String masterName) {
        super("master " + masterName + " is busy at this time");
    }
}
