package lukazitnik.jshint;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.spi.tasklist.FileTaskScanner;
import org.netbeans.spi.tasklist.Task;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
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

        if (fo.isFolder() || !fo.getExt().equals("js")) {
            return Collections.<Task>emptyList();
        }

        LinkedList<Task> tasks = new LinkedList<Task>();
        JSHint jshint = new JSHint();

        try {
            for (JSHintError error : jshint.lint(fo.asText())) {
                tasks.add(Task.create(fo, "nb-tasklist-jshint", error.getReason(), error.getLine()));
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return tasks;
    }

    @Override
    public void attach(Callback clbck) {
    }

}
