package system.resource;

import javax.swing.ImageIcon;

public class Resource {
    private static Resource resource = null;

    private Resource() {}

    public static ImageIcon get(String path_relative) {
        if (resource == null) resource = new Resource();
        return new ImageIcon(resource.getClass().getResource(path_relative));
    }
}