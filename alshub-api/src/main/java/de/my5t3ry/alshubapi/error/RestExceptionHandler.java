package de.my5t3ry.alshubapi.error;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * created by: sascha.bast
 * since: 10.02.18
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private ErrorReportRepository errorReportRepository;

    @ExceptionHandler(ProcessingException.class)
    protected ResponseEntity<Exception> handleApplicationException(Exception ex) {
        final String stack = Arrays.stream(ex.getStackTrace())
                .map(s -> s.toString())
                .collect(Collectors.joining("\n"));
        String message = ex.getMessage();
        if (ex.getCause() != null) {
            message = message.concat(" -> ").concat(ex.getCause().getMessage());
        }
        final ErrorReport report = errorReportRepository.save(new ErrorReport(stack, message));
        ex.printStackTrace();
        return new ResponseEntity<>(new ProcessingException(message, stack, report.getId(), ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
