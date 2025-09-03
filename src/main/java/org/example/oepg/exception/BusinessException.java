package org.example.oepg.exception;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况，可以返回给前端显示
 */
public class BusinessException extends RuntimeException {
    
    private String code;
    private String message;
    
    public BusinessException(String message) {
        super(message);
        this.message = message;
        this.code = "BUSINESS_ERROR";
    }
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
}
