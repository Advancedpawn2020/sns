package entity;

/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/10 0010 17:56
 */
public class Result {
    private Boolean flag;
    private Integer code;
    private String massage;
    private Object data;

    public Result() {
    }

    public Result(Boolean flag, Integer code, String massage) {
        this.flag = flag;
        this.code = code;
        this.massage = massage;
    }

    public Result(Boolean flag, Integer code, String massage, Object data) {
        this.flag = flag;
        this.code = code;
        this.massage = massage;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "flag=" + flag +
                ", code=" + code +
                ", massage='" + massage + '\'' +
                ", data=" + data +
                '}';
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
