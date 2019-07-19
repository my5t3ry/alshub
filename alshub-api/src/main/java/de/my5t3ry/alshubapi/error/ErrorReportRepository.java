package de.my5t3ry.alshubapi.error;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * User: sascha.bast
 * Date: 4/27/19
 * Time: 2:08 AM
 */
public interface ErrorReportRepository extends JpaRepository<ErrorReport, Integer> {
}
