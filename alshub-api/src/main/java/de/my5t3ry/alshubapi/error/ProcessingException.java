package de.my5t3ry.alshubapi.error;

import lombok.Data;

/**
 * User: sascha.bast
 * Date: 4/27/19
 * Time: 1:40 AM
 */
@Data
public class ProcessingException extends RuntimeException {
    private ProcessingExceptionType type;
    private String stack;
    private Integer reportId;

    public ProcessingException(final String msg, final String stack) {
        super(msg);
        this.stack = stack;
    }

    public ProcessingException(final String msg) {
        super(msg);
    }

    public ProcessingException(final String msg, final String stack, final Exception cause) {
        super(msg, cause);
        this.stack = stack;
    }

    public ProcessingException(final String msg, final String stack, final Integer reportId, final Exception cause) {
        super(msg, cause);
        this.stack = stack;
        this.reportId = reportId;
    }

    public ProcessingException(final String msg, final Exception cause) {
        super(msg, cause);
    }

    public ProcessingException(final String msg, final ProcessingExceptionType type) {
        super(msg);
        this.type = type;
    }
}
