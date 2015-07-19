package lukazitnik.jshint.options;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.openide.filesystems.FileUtil;

class JSFilesOnlyFilter extends FileFilter {

    @Override
    public boolean accept(File file) {
        return file.isDirectory() || FileUtil.toFileObject(file).getMIMEType().equals("text/javascript");
    }

    @Override
    public String getDescription() {
        return "JavaScript Files";
    }

}
