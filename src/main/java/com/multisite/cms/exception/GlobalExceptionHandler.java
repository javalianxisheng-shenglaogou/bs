package com.multisite.cms.exception;

import com.multisite.cms.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        log.warn("Validation error on {}: {}", request.getRequestURI(), ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiResponse<Map<String, String>> response = ApiResponse.badRequest("参数验证失败");
        response.setData(errors);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleBindException(
            BindException ex, HttpServletRequest request) {
        
        log.warn("Bind error on {}: {}", request.getRequestURI(), ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiResponse<Map<String, String>> response = ApiResponse.badRequest("参数绑定失败");
        response.setData(errors);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        log.warn("Constraint violation on {}: {}", request.getRequestURI(), ex.getMessage());
        
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));
        
        ApiResponse<Map<String, String>> response = ApiResponse.badRequest("约束验证失败");
        response.setData(errors);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理方法参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        log.warn("Type mismatch on {}: parameter '{}' should be of type '{}'", 
                request.getRequestURI(), ex.getName(), ex.getRequiredType().getSimpleName());
        
        String message = String.format("参数 '%s' 类型错误，应为 %s 类型", 
                ex.getName(), ex.getRequiredType().getSimpleName());
        
        ApiResponse<Void> response = ApiResponse.badRequest(message);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        
        log.warn("Authentication error on {}: {}", request.getRequestURI(), ex.getMessage());
        
        String message = "认证失败";
        if (ex instanceof BadCredentialsException) {
            message = "用户名或密码错误";
        }
        
        ApiResponse<Void> response = ApiResponse.unauthorized(message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 处理访问拒绝异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        
        log.warn("Access denied on {}: {}", request.getRequestURI(), ex.getMessage());
        
        ApiResponse<Void> response = ApiResponse.forbidden("权限不足，拒绝访问");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        log.warn("Illegal argument on {}: {}", request.getRequestURI(), ex.getMessage());
        
        ApiResponse<Void> response = ApiResponse.badRequest(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理非法状态异常
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(
            IllegalStateException ex, HttpServletRequest request) {
        
        log.warn("Illegal state on {}: {}", request.getRequestURI(), ex.getMessage());
        
        ApiResponse<Void> response = ApiResponse.badRequest(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Void>> handleNullPointerException(
            NullPointerException ex, HttpServletRequest request) {
        
        log.error("Null pointer exception on {}", request.getRequestURI(), ex);
        
        ApiResponse<Void> response = ApiResponse.internalError("系统内部错误");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        
        log.error("Runtime exception on {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        
        // 根据异常消息判断错误类型
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("不存在") || message.contains("未找到")) {
                ApiResponse<Void> response = ApiResponse.notFound(message);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else if (message.contains("已存在") || message.contains("重复") || 
                      message.contains("冲突") || message.contains("无效")) {
                ApiResponse<Void> response = ApiResponse.badRequest(message);
                return ResponseEntity.badRequest().body(response);
            } else if (message.contains("权限") || message.contains("禁止")) {
                ApiResponse<Void> response = ApiResponse.forbidden(message);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }
        
        ApiResponse<Void> response = ApiResponse.internalError(message != null ? message : "系统内部错误");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        log.error("Unexpected exception on {}", request.getRequestURI(), ex);
        
        ApiResponse<Void> response = ApiResponse.internalError("系统内部错误，请联系管理员");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        
        log.warn("Business exception on {}: {} (code: {})", 
                request.getRequestURI(), ex.getMessage(), ex.getCode());
        
        ApiResponse<Void> response = ApiResponse.error(ex.getCode(), ex.getMessage());
        
        // 根据错误码确定HTTP状态码
        HttpStatus httpStatus;
        int code = ex.getCode();
        if (code == 400 || code == 4001 || code == 4002 || code == 4003) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (code == 401 || code == 4011 || code == 4012) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (code == 403 || code == 4031 || code == 4032) {
            httpStatus = HttpStatus.FORBIDDEN;
        } else if (code == 404 || code == 4041 || code == 4042) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (code == 409 || code == 4091 || code == 4092) {
            httpStatus = HttpStatus.CONFLICT;
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        return ResponseEntity.status(httpStatus).body(response);
    }

    /**
     * 自定义业务异常类
     */
    public static class BusinessException extends RuntimeException {
        private final Integer code;

        public BusinessException(Integer code, String message) {
            super(message);
            this.code = code;
        }

        public BusinessException(String message) {
            this(500, message);
        }

        public Integer getCode() {
            return code;
        }
    }
}
