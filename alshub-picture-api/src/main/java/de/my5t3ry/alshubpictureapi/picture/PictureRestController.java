package de.my5t3ry.alshubpictureapi.picture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * User: sascha.bast
 * Date: 2/15/19
 * Time: 10:46 AM
 */
@RestController
@RequestMapping("/picture")
public class PictureRestController {

    @Autowired
    private PictureRepository pictureRepository;

    @GetMapping
    public ResponseEntity<List<Picture>> get() {
        return new ResponseEntity<>(pictureRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/delete/{pictureId}")
    public ResponseEntity delete(@PathVariable("pictureId") Integer id) {
        pictureRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Picture> save(@RequestBody @NotNull PictureSaveRequest pictureSaveRequest) {
        final String base64String = pictureSaveRequest.getData().substring(pictureSaveRequest.getData().indexOf("64,") + 3);
        final Picture result = new Picture(base64String);
        pictureRepository.save(result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
