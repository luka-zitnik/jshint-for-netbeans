package lukazitnik.jshint;

import java.util.LinkedList;
import java.util.List;
import org.netbeans.spi.tasklist.FileTaskScanner;
import org.netbeans.spi.tasklist.Task;
import org.openide.filesystems.FileObject;

/**
 *
 * @author luka
 */
public class JSHintFileTaskScanner extends FileTaskScanner {

    private LinkedList<Task> tasks = new LinkedList<Task>();

    public JSHintFileTaskScanner(String displayName, String description, String optionsPath) {
        super(displayName, description, optionsPath);
    }

    public static JSHintFileTaskScanner create() {
        return new JSHintFileTaskScanner("JSHint", "JSHint", null);
    }

    @Override
    public List<? extends Task> scan(FileObject fo) {

        if (!fo.isFolder()) {
            tasks.add(Task.create(fo, "", fo.getNameExt(), 1));
        }

        return tasks;
    }

    @Override
    public void attach(Callback clbck) {
    }

}
