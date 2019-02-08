package fr.ubordeaux.jmetrics.project;


import java.util.List;

/**
 * Represent a package directory (set of files and sub-directories).
 */
public class PackageDirectory extends ProjectComponent {

    List<ProjectComponent> content;

    public PackageDirectory(String name) {
        super(name);
    }

    public List<String> getCategoriesNames() {
        return null;
    }

}