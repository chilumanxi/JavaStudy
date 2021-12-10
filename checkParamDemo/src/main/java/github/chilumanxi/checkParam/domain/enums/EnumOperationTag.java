package github.chilumanxi.checkParam.domain.enums;


public enum EnumOperationTag {
    A_TYPE(1, "类型A");

    private Integer code;

    private String msg;

    EnumOperationTag(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
