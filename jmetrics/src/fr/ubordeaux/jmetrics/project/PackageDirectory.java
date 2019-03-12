package fr.ubordeaux.jmetrics.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a package directory (set of files and sub-directories).
 */
public class PackageDirectory extends ProjectComponent {

    private List<ProjectComponent> content;

    private int depth;

    public PackageDirectory(File file, String name, int depth) {
        super(file, name);
        content = new ArrayList<>();
        this.depth = depth;
    }

    public List<ProjectComponent> getContent() {
        return new ArrayList<>(content);
    }

    public void addContent(ProjectComponent content) {
        this.content.add(content);
    }

    public void removeContent(ProjectComponent content) {
        this.content.remove(content);
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PackageDirectory that = (PackageDirectory) o;
        return content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), content);
    }

}
