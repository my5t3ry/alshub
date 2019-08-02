package de.my5t3ry.alshubpictureapi.picture;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * User: sascha.bast
 * Date: 2/14/19
 * Time: 2:59 PM
 */
public interface PictureRepository extends JpaRepository<Picture, Integer> {
    boolean existsByHash(String foo);

}
