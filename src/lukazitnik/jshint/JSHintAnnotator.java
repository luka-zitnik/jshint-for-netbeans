package lukazitnik.jshint;

import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.editor.AnnotationDesc;
import org.netbeans.editor.Annotations;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.editor.NbEditorUtilities;

class JSHintAnnotator implements DocumentListener {

    private Thread lintingThread;
    private ProgressHandle progressHandle;

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
        String fileName = NbEditorUtilities.getFileObject(d).getNameExt();

        if (lintingThread != null && lintingThread.isAlive()) {
            progressHandle.finish();
            lintingThread.stop();
        }

        progressHandle = ProgressHandleFactory.createHandle("Linting " + fileName);
        lintingThread = new Thread("Linting Thread") {

            @Override
            public void run() {
                progressHandle.start();

                JSHint jshint = JSHint.instance;
                final List<JSHintError> errors = jshint.lint(d);

                progressHandle.finish();

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        removeAnnotations();
                        addAnnotations(errors);
                    }
                });
            }

            private void removeAnnotations() {
                Annotations annotations = d.getAnnotations();

                // getNextLineWithAnnotation seems to return its argument
                // as long as there are annotations on that line.
                for (int line = annotations.getNextLineWithAnnotation(0); line != -1;) {
                    AnnotationDesc annotationDesc = annotations.getAnnotation(line, "lukazitnik-jshint-jshintannotation");

                    if (annotationDesc == null) {
                        // Increment line index. We are done with this line.
                        line = annotations.getNextLineWithAnnotation(++line);
                        continue;
                    }

                    annotations.removeAnnotation(annotationDesc);
                }
            }

            private void addAnnotations(List<JSHintError> errors) {
                for (JSHintError error : errors) {

                    // Line indexes start from 0, while line numbers start from 1
                    Integer offset = Utilities.getRowStartFromLineOffset(d, error.getLine() - 1);

                    JSHintAnnotation annotation = new JSHintAnnotation(error);

                    try {
                        d.addAnnotation(d.createPosition(offset), 0, annotation);
                    } catch (BadLocationException ex) {
                        // Document changed in the meantime. Do nothing.
                        // Another update to annotations should follow.
                    }
                }
            }
        };

        lintingThread.start();
    }
}
