package lukazitnik.jshint;

import java.util.LinkedList;
import java.util.List;
import org.netbeans.spi.tasklist.FileTaskScanner;
import org.netbeans.spi.tasklist.Task;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author luka
 */
public class JSHintFileTaskScanner extends FileTaskScanner {

    public JSHintFileTaskScanner(String displayName, String description) {
        super(displayName, description, null);
    }

    public static JSHintFileTaskScanner create() {
        return new JSHintFileTaskScanner(
                NbBundle.getMessage(JSHintFileTaskScanner.class, "LBL_task"),
                NbBundle.getMessage(JSHintFileTaskScanner.class, "DESC_task")
        );
    }

    @Override
    public List<? extends Task> scan(FileObject fo) {

        LinkedList<Task> tasks = new LinkedList<Task>();

        if (!fo.isFolder()) {
            tasks.add(Task.create(fo, "nb-tasklist-jshint", fo.getNameExt(), 1));
        }

        return tasks;
    }

    @Override
    public void attach(Callback clbck) {
    }

}
