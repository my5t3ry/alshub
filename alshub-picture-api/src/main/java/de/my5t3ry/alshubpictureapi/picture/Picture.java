package de.my5t3ry.alshubpictureapi.picture;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

/**
 * User: sascha.bast
 * Date: 2/14/19
 * Time: 2:58 PM
 */
@Getter
@NoArgsConstructor
@Slf4j
@Entity
public class Picture {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Lob
    private String base64;


    private String hash;

    public Picture(final String base64) {
        this.base64 = base64;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            this.hash = Base64.getEncoder().encodeToString(digest.digest(base64.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            log.error("Could not create hash for picture", e);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Picture picture = (Picture) o;
        return Objects.equals(hash, picture.hash);
    }

    @Override
    public int hashCode() {
        return hash != null ? hash.hashCode() : 0;
    }
}
