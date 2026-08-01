package com.boardingpass.be.global.exception;

import com.boardingpass.be.global.apiPayload.ApiResponse;
import com.boardingpass.be.global.apiPayload.code.ErrorReasonDto;
import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {
    String message = e.getConstraintViolations().stream()
        .map(violation -> violation.getMessage())
        .findFirst()
        .orElse("잘못된 요청입니다.");

    return handleExceptionInternalFalse(
        e,
        ErrorStatus._BAD_REQUEST,
        HttpHeaders.EMPTY,
        ErrorStatus._BAD_REQUEST.getHttpStatus(),
        request,
        message
    );
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    Map<String, String> errors = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> {
      String field = error.getField();
      String message = Optional.ofNullable(error.getDefaultMessage()).orElse("");
      errors.merge(field, message, (existing, current) -> existing + ", " + current);
    });

    return handleExceptionInternalArgs(ex, headers, ErrorStatus._BAD_REQUEST, request, errors);
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    String message = String.format(
        "지원하지 않는 HTTP 메서드입니다. '%s'는 사용할 수 없으며, 다음 메서드만 허용됩니다: %s",
        ex.getMethod(), Arrays.toString(ex.getSupportedMethods()));

    return handleExceptionInternalFalse(
        ex, ErrorStatus._BAD_REQUEST, headers, HttpStatus.METHOD_NOT_ALLOWED, request, message);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    String message = String.format(
        "지원하지 않는 미디어 타입입니다. '%s'는 사용할 수 없으며, 다음 타입만 허용됩니다: %s",
        ex.getContentType(), ex.getSupportedMediaTypes());

    return handleExceptionInternalFalse(
        ex, ErrorStatus._BAD_REQUEST, headers, HttpStatus.UNSUPPORTED_MEDIA_TYPE, request, message);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    String message = String.format("필수 파라미터가 누락되었습니다. '%s' 파라미터는 필수입니다.", ex.getParameterName());
    return handleExceptionInternalFalse(
        ex, ErrorStatus._BAD_REQUEST, headers, HttpStatus.BAD_REQUEST, request, message);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestPart(
      MissingServletRequestPartException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    String message = String.format("필수 request part가 누락되었습니다. '%s' 부분은 필수입니다.", ex.getRequestPartName());
    return handleExceptionInternalFalse(
        ex, ErrorStatus._BAD_REQUEST, headers, HttpStatus.BAD_REQUEST, request, message);
  }

  @Override
  protected ResponseEntity<Object> handleMissingPathVariable(
      MissingPathVariableException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    String message = String.format("필수 경로 변수가 누락되었습니다. '%s' 변수는 필수입니다.", ex.getVariableName());
    return handleExceptionInternalFalse(
        ex, ErrorStatus._BAD_REQUEST, headers, HttpStatus.BAD_REQUEST, request, message);
  }

  @ExceptionHandler
  public ResponseEntity<Object> exception(Exception e, WebRequest request) {
    log.error("Unhandled exception", e);
    return handleExceptionInternalFalse(
        e,
        ErrorStatus._INTERNAL_SERVER_ERROR,
        HttpHeaders.EMPTY,
        ErrorStatus._INTERNAL_SERVER_ERROR.getHttpStatus(),
        request,
        e.getMessage());
  }

  @ExceptionHandler(value = GeneralException.class)
  public ResponseEntity<Object> onThrowException(
      GeneralException generalException, HttpServletRequest request) {
    ErrorReasonDto errorReasonHttpStatus = generalException.getErrorReasonHttpStatus();
    return handleExceptionInternal(generalException, errorReasonHttpStatus, null, request);
  }

  private ResponseEntity<Object> handleExceptionInternal(
      Exception e, ErrorReasonDto reason, HttpHeaders headers, HttpServletRequest request) {
    ApiResponse<Object> body = ApiResponse.onFailure(reason.getCode(), reason.getMessage(), null);
    WebRequest webRequest = new ServletWebRequest(request);
    return super.handleExceptionInternal(e, body, headers, reason.getHttpStatus(), webRequest);
  }

  private ResponseEntity<Object> handleExceptionInternalFalse(
      Exception e,
      ErrorStatus errorCommonStatus,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request,
      String errorPoint) {
    ApiResponse<Object> body =
        ApiResponse.onFailure(errorCommonStatus.getCode(), errorCommonStatus.getMessage(), errorPoint);
    return super.handleExceptionInternal(e, body, headers, status, request);
  }

  private ResponseEntity<Object> handleExceptionInternalArgs(
      Exception e,
      HttpHeaders headers,
      ErrorStatus errorCommonStatus,
      WebRequest request,
      Map<String, String> errorArgs) {
    ApiResponse<Object> body =
        ApiResponse.onFailure(
            errorCommonStatus.getCode(), errorCommonStatus.getMessage(), errorArgs);
    return super.handleExceptionInternal(
        e, body, headers, errorCommonStatus.getHttpStatus(), request);
  }
}
