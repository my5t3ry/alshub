package de.my5t3ry.alshubapi.explorer.fs_item;

import lombok.Data;

import java.nio.file.Path;

@Data
public abstract class AbstractFsItem {
    private final Path path;
    private final FsItemType type;
    private final String name;
    private final String absolutePath;

    public AbstractFsItem(final Path path, final FsItemType type) {
        this.path = path;
        this.type = type;
        this.name = path.getFileName().toString();
        this.absolutePath = path.toAbsolutePath().toString();
    }
}
