package com.example.food.delivery;

public class UserManagementExceptions {
    public static class BaseUserManagementException extends RuntimeException {
        private boolean isSuccess;
        private String status;

        public BaseUserManagementException(String message) {
            super(message);
            this.isSuccess = false;
            this.status = "Error";
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class InvalidInputException extends BaseUserManagementException {
        public InvalidInputException(String message) {
            super(message);
        }
    }


    public static class UnrecognizedTokenException extends RuntimeException {
        public UnrecognizedTokenException(String message) {
            super(message);
        }
    }

    public static class UserNotFoundException extends BaseUserManagementException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class AddressNotFoundException extends BaseUserManagementException {
        public AddressNotFoundException(String message) {
            super(message);
        }
    }

    public static class UserAlreadyExistsException extends BaseUserManagementException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class RestTemplateException extends BaseUserManagementException {
        public RestTemplateException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedAccessException extends BaseUserManagementException {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
    }

    public static class LoginException extends BaseUserManagementException {
        public LoginException(String message) {
            super(message);
        }
    }

    public static class VerificationFailureException extends BaseUserManagementException {
        public VerificationFailureException(String message) {
            super(message);
        }
    }


}