package de.my5t3ry.alshubapi.explorer.fs_item;

import lombok.Data;

import java.nio.file.Path;

@Data
public class Directory extends AbstractFsItem {
    public Directory(final Path path) {
        super(path, FsItemType.DIRECTORY);
    }
}
