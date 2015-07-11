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
        stop();
        progressHandle.finish();
    }
}
