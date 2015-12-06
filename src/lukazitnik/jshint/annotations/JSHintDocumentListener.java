package lukazitnik.jshint.annotations;

import java.io.IOException;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import lukazitnik.jshint.JSHint;
import lukazitnik.jshint.JSHintError;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.util.RequestProcessor;

class JSHintDocumentListener implements DocumentListener {

    private RequestProcessor.Task lastUpdateTask;
    private final RequestProcessor requestProcessor = new RequestProcessor("JSHintDocumentListener", 1, true);

    @Override
    public void insertUpdate(DocumentEvent de) {
        updateAnnotations((NbEditorDocument) de.getDocument());
    }

    @Override
    public void removeUpdate(DocumentEvent de) {
        updateAnnotations((NbEditorDocument) de.getDocument());
    }

    @Override
    public void changedUpdate(DocumentEvent de) {
        updateAnnotations((NbEditorDocument) de.getDocument());
    }

    protected void updateAnnotations(final NbEditorDocument d) {

        if (lastUpdateTask != null && !lastUpdateTask.isFinished()) {
            lastUpdateTask.cancel();
        }

        lastUpdateTask = requestProcessor.post(new Runnable() {

            @Override
            public void run() {
                String fileName;

                try {
                    fileName = NbEditorUtilities.getFileObject(d).getNameExt();
                } catch (NullPointerException ex) {
                    // Can happen, according to http://statistics.netbeans.org/analytics/detail.do?id=221301
                    return;
                }

                ProgressHandle progressHandle = ProgressHandleFactory.createHandle("Linting " + fileName);
                progressHandle.start();

                try {
                    JSHint jshint = JSHint.getInstance();
                    final List<JSHintError> errors = jshint.lint(d);
                    SwingUtilities.invokeLater(new Annotator(d, errors));
                } catch (IOException ex) {
                    // Installer should never let this happen
                } finally {
                    progressHandle.finish();
                }
            }
        });
    }
}
