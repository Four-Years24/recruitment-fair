package com.recruitment.common;

/**
 * 业务异常
 * 抛出这个异常会被全局异常处理器捕获，返回给前端友好的错误信息
 *
 * 用法：throw new BusinessException("企业名称不能为空")
 */
public class BusinessException extends RuntimeException {

    private int code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() { return code; }
}
