package de.my5t3ry.alshubapi.explorer.fs_item;

import java.nio.file.Path;

public class FsFile extends AbstractFsItem {
    public FsFile(final Path path) {
        super(path,FsItemType.FILE);

    }
}
