package com.egopulse.web.annotation;

public enum ContentType {
    APP_JSON("application/json"),
    APP_XML("application/xml"),
    APP_FORM_URLENCODED("application/x-www-form-urlencoded"),
    APP_JAVASCRIPT("application/javascript"),
    MULTIPART_FORM_DATA("multipart/form-data"),
    TEXT_PLAN("text/plan"),
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    IMG_GIF("image/gif"),
    IMG_PNG("image/png"),
    IMG_JPEG("image/jpeg"),
    IMG_TIFF("image/tiff");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}