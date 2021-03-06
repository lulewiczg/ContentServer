package com.github.lulewiczg.contentserver.utils;

/**
 * Class for constants.
 */
public final class Constants {
    public static final String SEP = "/";
    public static final String ADMIN = "admin";
    public static final String GUEST = "guest";
    public static final String EMPTY = "";

    /**
     * Web constants.
     */
    public static class Web {
        public static final String USER = "user";
        public static final String LOGIN = "login";
        public static final String PASSWORD = "password";
        public static final String PATH = "path";
        public static final String DOWNLOAD = "download";

        public static class Headers {
            public static final String CONTENT_LENGTH = "Content-Length";
            public static final String CONTENT_RANGE = "Content-Range";
            public static final String ACCEPT_RANGES = "Accept-Ranges";
            public static final String CONTENT_DISPOSITION = "Content-Disposition";
            public static final String RANGE = "Range";
            public static final String EXPIRES = "Expires";
            public static final String APPLICATION_FORCE_DOWNLOAD = "application/force-download";
            public static final String AUTHORIZATION = "Authorization";

            private Headers() {
            }
        }

        public static class Errors {
            public static final String ACCESS_DENIED_TO = "Access denied to \"%s\"";
            public static final String NOT_FOUND = "Resource \"%s\" not found";
            public static final String USER_ALREADY_LOGGED = "User already logged in";
            public static final String INVALID_CREDENTIALS = "Invalid user or password";
            public static final String UPLOAD_DIR_NOT_FOUND = "Upload directory not specified";
            public static final String FILE_ALREADY_EXIST = "File %s already exists";

            private Errors() {
            }
        }

        private Web() {
        }
    }

    /**
     * Settings constants.
     */
    public static class Setting {
        public static final String USER = "user";
        public static final String PASSWORD = "password";
        public static final String LOGGER_LEVEL = "logger.level";
        public static final String BUFFER_SIZE = "buffer.size";
        public static final String MIME = "mime.";
        public static final String PERMISSION = "permission";
        public static final String EXTENDS = "extends";
        public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
        public static final String APPLICATION_JSON = "application/json";
        public static final String PLAIN_TEXT = "text/plain";
        public static final String UTF8 = "UTF-8";

        private Setting() {
        }
    }

    private Constants() {
    }
}
