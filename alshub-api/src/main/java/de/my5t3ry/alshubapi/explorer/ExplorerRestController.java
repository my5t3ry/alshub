package de.my5t3ry.alshubapi.explorer;

import de.my5t3ry.alshubapi.explorer.fs_item.AbstractFsItem;
import de.my5t3ry.alshubapi.explorer.fs_item.Directory;
import de.my5t3ry.alshubapi.explorer.fs_item.FsFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/explorer")
@Scope("singleton")
public class ExplorerRestController {

    private String curPath;

    public ExplorerRestController() {
        this.curPath = System.getProperty("user.home");
    }

    @GetMapping
    public ResponseEntity<List<AbstractFsItem>> get() {
        return new ResponseEntity<>(getCurrentItems(), HttpStatus.OK);
    }

    private List<AbstractFsItem> getCurrentItems() {
        IOFileFilter filter = new AbstractFileFilter() {
            public boolean accept(File file) {
                if (((file.isFile() && file.getName().contains("als")) || file.isDirectory()) && !file.isHidden() && file.getParent().equals(curPath) && !file.getAbsolutePath().equals(curPath)) {
                    return true;
                }
                return false;
            }
        };
        final List<AbstractFsItem> result = new ArrayList<AbstractFsItem>();
        Collection<File> files = FileUtils.listFilesAndDirs(new File(curPath), filter, filter);
        files.stream().filter(f -> Files.isReadable(f.toPath())).forEach(path -> {
            if (Files.isRegularFile(path.toPath())) {
                result.add(new FsFile(path.toPath()));
            } else {
                result.add(new Directory(path.toPath()));
            }
        });
        return result;
    }

    @PostMapping(path = "/set-path")
    public ResponseEntity<List<AbstractFsItem>> save(@RequestBody SetPathRequest setPathRequest) {
        this.curPath = setPathRequest.getPath();
        final List<AbstractFsItem> result = getCurrentItems();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(path = "/set-parent")
    public ResponseEntity<List<AbstractFsItem>> setParent() {
        this.curPath = Path.of(this.curPath).getParent().toAbsolutePath().toString();
        final List<AbstractFsItem> result = getCurrentItems();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
