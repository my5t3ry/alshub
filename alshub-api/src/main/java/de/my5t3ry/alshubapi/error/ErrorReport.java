package de.my5t3ry.alshubapi.error;

import lombok.Data;

import javax.persistence.*;

/**
 * User: sascha.bast
 * Date: 4/27/19
 * Time: 2:06 AM
 */
@Entity
@Data
public class ErrorReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    public ErrorReport(final String stack, final String msg) {
        this.stack = stack;
        this.msg = msg;
    }

    public ErrorReport() {
    }

    @Lob
    private String stack;
    @Lob
    private String msg;


}
