package net.ukr.lina_chen.beauty_salon_spring_project.controller.utility;

public interface RegexContainer {
    String NAME_REGEX_UA = "[А-ЩҐЄІЇЮЯ][а-щґєіїьюя']{1,19}[а-щґєіїьюя]";
    String NAME_REGEX_LAT = "[A-Z][a-z]{1,20}";
    String PASSWORD_REGEX = "[a-zA-Z0-9]{8,20}";
    String EMAIL_REGEX = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
}

