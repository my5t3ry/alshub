package de.my5t3ry.alshubapi.explorer;

import de.my5t3ry.alshubapi.explorer.fs_item.AbstractFsItem;
import lombok.Data;

import java.util.List;

@Data
public class ExplorerPathResult {
    public ExplorerPathResult(final List<AbstractFsItem> items, final String path) {
        this.items = items;
        this.path = path;
    }

    private final List<AbstractFsItem> items ;
    private String path;

}
