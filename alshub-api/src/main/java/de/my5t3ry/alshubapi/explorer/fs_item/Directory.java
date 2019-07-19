package de.my5t3ry.alshubapi.explorer.fs_item;

import lombok.Data;
import lombok.Setter;

import java.nio.file.Path;

@Data
@Setter
public class Directory extends AbstractFsItem {
    private Boolean abletonProject;

    public Directory(final Path path) {
        super(path, FsItemType.DIRECTORY);
    }

}
