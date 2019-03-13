package net.vpnsdk.wanve.bean;

/**
 * Created by zhou
 * on 2018/12/25.
 */

public class LookDocBean {


    /**
     * type : ViewDocument
     * params : {"path":"http://ys.wanve.com:8080/DMS_Phone/QWMan/Download/B69C6B7D0E1F4145A82A272ACDD96AFC20190102170143253/20181225_塘厦政府_需求修改确认单.doc","office365":"http://ys.wanve.com:8080/OfficeWeb365_BJM/?n=1&furl=http://ys.wanve.com:8080/DMS_Phone/QWMan/Download/B69C6B7D0E1F4145A82A272ACDD96AFC20190102170143253/20181225_塘厦政府_需求修改确认单.doc"}
     */

    private String type;
    private ParamsBean params;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public static class ParamsBean {
        /**
         * path : http://ys.wanve.com:8080/DMS_Phone/QWMan/Download/B69C6B7D0E1F4145A82A272ACDD96AFC20190102170143253/20181225_塘厦政府_需求修改确认单.doc
         * office365 : http://ys.wanve.com:8080/OfficeWeb365_BJM/?n=1&furl=http://ys.wanve.com:8080/DMS_Phone/QWMan/Download/B69C6B7D0E1F4145A82A272ACDD96AFC20190102170143253/20181225_塘厦政府_需求修改确认单.doc
         */

        private String path;
        private String office365;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getOffice365() {
            return office365;
        }

        public void setOffice365(String office365) {
            this.office365 = office365;
        }

        @Override
        public String toString() {
            return "ParamsBean{" +
                    "path='" + path + '\'' +
                    ", office365='" + office365 + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LookDocBean{" +
                "type='" + type + '\'' +
                ", params=" + params +
                '}';
    }
}
