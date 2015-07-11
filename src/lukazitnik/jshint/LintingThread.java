package lukazitnik.jshint;

import java.util.List;
import javax.swing.SwingUtilities;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.util.Cancellable;

public class LintingThread extends Thread {

    private final NbEditorDocument d;
    private final ProgressHandle progressHandle;

    public LintingThread(NbEditorDocument d) {
        this.d = d;
        this.setName("Linting Thread");
        String fileName = NbEditorUtilities.getFileObject(d).getNameExt();
        progressHandle = ProgressHandleFactory.createHandle("Linting " + fileName, new Cancellable() {

            @Override
            public boolean cancel() {
                LintingThread.this.stopAndFinishProgressHandle();
                return true;
            }
        });
    }

    @Override
    public void run() {
        progressHandle.start();

        JSHint jshint = JSHint.instance;
        final List<JSHintError> errors = jshint.lint(d);

        progressHandle.finish();

        SwingUtilities.invokeLater(new Annotator(d, errors));
    }

    public void stopAndFinishProgressHandle() {

        // Shouldn't be a problem, only errors property of the JS function changes.
        // If this turns out to be wrong, RequestProcessor.Task.cancel() can be used
        // instead, though that would incur poorer performance -- cancel() would
        // remove not yet running tasks from the queue, but the running task would
        // have to finish if it's interruption cannot be handled.
        stop();

        progressHandle.finish();
    }
}
