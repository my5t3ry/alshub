package de.my5t3ry.alshubapi.explorer;

import de.my5t3ry.alshubapi.explorer.fs_item.AbstractFsItem;
import de.my5t3ry.alshubapi.explorer.fs_item.Directory;
import de.my5t3ry.alshubapi.explorer.fs_item.FsFile;
import de.my5t3ry.alshubapi.project.Project;
import de.my5t3ry.alshubapi.project.ProjectService;
import de.my5t3ry.alshubapi.response_entity.ResponseEntityFactory;
import de.my5t3ry.alshubapi.response_entity.ResponseMessageType;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/explorer")
@Scope("singleton")
public class ExplorerRestController {
    @Autowired
    private ProjectService projectService;

    private String curPath;

    //    public ExplorerRestController() {
//        this.curPath = System.getProperty("user.home");
//    }
    public ExplorerRestController() {
        this.curPath = "/home/my5t3ry/sound/project_dump/20170428";
    }

    @GetMapping
    public ResponseEntity<ExplorerPathResult> get() {
        return new ResponseEntity<>(getCurrentItems(), HttpStatus.OK);
    }

    private ExplorerPathResult getCurrentItems() {
        final List<AbstractFsItem> result = new ArrayList<AbstractFsItem>();
        Arrays.asList(new File(curPath).listFiles(f -> f.isDirectory()))
                .stream()
                .filter(f -> Files.isReadable(f.toPath()))
                .forEach(path -> {
                    if (Files.isRegularFile(path.toPath())) {
                        result.add(new FsFile(path.toPath()));
                    } else {
                        result.add(createDirectoryItem(path));
                    }
                });
        return new ExplorerPathResult(result, this.curPath);
    }

    private Directory createDirectoryItem(final File path) {
        final Directory result = new Directory(path.toPath());
        result.setAbletonProject(checkIfDirIncludesAlsFile(result));
        return result;
    }

    private Boolean checkIfDirIncludesAlsFile(final Directory result) {
        return FileUtils.listFiles(new File(result.getAbsolutePath()), new String[]{"als"}, false).size() > 0;
    }

    @PostMapping(path = "/set-path")
    public ResponseEntity<ExplorerPathResult> save(@RequestBody SetPathRequest setPathRequest) {
        this.curPath = setPathRequest.getPath();
        final ExplorerPathResult result = getCurrentItems();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(path = "/add-project")
    public ResponseEntity<Project> addProject(@RequestBody SetPathRequest setPathRequest, Principal principal) {
        return ResponseEntityFactory.build("Project added",
                ResponseMessageType.INFO,
                projectService.addProject(setPathRequest, principal),
                HttpStatus.OK);
    }

    @PostMapping(path = "/set-parent")
    public ResponseEntity<ExplorerPathResult> setParent() {
        this.curPath = Path.of(this.curPath).getParent().toAbsolutePath().toString();
        final ExplorerPathResult result = getCurrentItems();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
