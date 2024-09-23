package com.gdsc.studyjamapi.common;

public class Constants {
  public static final int EXPIRATION_TIME = 1000 * 60 * 60 * 24;
  public static final String DEFAULT_SECRET_KEY = "QSBkZWZhdWx0IGtleSB3aXRoIDMyIGNoYXJhY3RlcnM=";
  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String BEARER_PREFIX = "Bearer ";
  public static final String PASSWORD_PATTERN =
      "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d\\w\\W]*$";

  public static class ErrorMessage {
    public static final String REQUIRED_FULL_NAME = "The fullName is required.";
    public static final String REQUIRED_EMAIL = "The email is required.";
    public static final String REQUIRED_PASSWORD = "The password is required.";
    public static final String REQUIRED_ROLE = "The role is required.";
    public static final String WEAK_PASSWORD =
        "The password must contain at least one lowercase letter, one uppercase letter and one number.";
    public static final String SHORT_PASSWORD =
        "The length of the password must be at least 8 characters.";
    public static final String EMAIL_ALREADY_EXISTS = "This email already exists.";
    public static final String EMAIL_NOT_FOUND = "Not found this email.";
    public static final String NOT_FOUND_MESSAGE_TEMPLATE = "%s not found";
  }
}
