package application.lib.classes;

public class JarFilePath {
    public String getFilePath(String fileDirectory){
        return (this.getClass().getResource("").getPath()
                .replaceAll("publicator.jar!", "") + fileDirectory)
                .replaceAll("application/lib/classes", "")
                .replaceAll("file:/", "")
                .replaceAll("%20", " ");
    }
}
