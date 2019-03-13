package net.vpnsdk.wanve.utils;

/**
 * Created by zhou
 * on 2018/12/28.
 */

public class LastPoint {

    public static final String getPoint(String data) {
        if (data.equals("doc")) {
            return "application/msword";
        }
        if (data.equals("dot")) {
            return "application/msword";
        }
        if (data.equals("pdf")) {
            return "application/pdf";
        }
        if (data.equals("dotx")) {
            return "application/msword";
        }
        if (data.equals("txt")) {
            return "application/txt";
        }
        if (data.equals("docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        if (data.equals("xls")) {
            return "application/vnd.ms-excel";
        }
        if (data.equals("xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        if (data.equals("ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (data.equals("pptx")) {
            return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        }
        return data;
    }
}
