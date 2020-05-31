package entity;

/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/10 0010 18:03
 * 定义接口返回状态码，便于统一修改
 */
public enum StatusCode {
        OK(20000, "成功"),
        ERROR(20001, "失败"),
        LOGINERROR(20002, "用户名或密码错误"),
        ACCESSERROR(20003, "权限不足"),
        REMOTEERROR(20004, "远程调用失败"),
        REPERROR(20005, "重复操作");

        private Integer code;
        private String msg;

        StatusCode(Integer code, String msg) {
                this.code = code;
                this.msg = msg;
        }

        public Integer getCode() {
                return code;
        }

        public void setCode(Integer code) {
                this.code = code;
        }

        public String getMsg() {
                return msg;
        }

        public void setMsg(String msg) {
                this.msg = msg;
        }
}
