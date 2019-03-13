package net.vpnsdk.wanve.bean;

/**
 * Created by zhou
 * on 2019/3/11.
 */

public class OpenfileBean {

    /**
     * 2019年3月11日
     * 询问聪哥是否对其他的有影响，回答没有影响
     * type : openfile
     * path : http://19.106.174.9/DMS_Phone//QWMan/Download/EEB537EC4CA2462EB02C9ED0C6657CD320190311110153983/关于征求《市委常委会2019年工作要点任务安排》意见的函.doc
     */

    private String type;
    private String path;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "OpenfileBean{" +
                "type='" + type + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
