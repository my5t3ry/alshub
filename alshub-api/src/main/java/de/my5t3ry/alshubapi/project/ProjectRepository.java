package de.my5t3ry.alshubapi.project;

import de.my5t3ry.alshubapi.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Integer> {
    List<Project> findByUser(User user);

}
