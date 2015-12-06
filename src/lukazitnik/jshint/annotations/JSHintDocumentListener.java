package lukazitnik.jshint.annotations;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.editor.NbEditorDocument;

class JSHintDocumentListener implements DocumentListener {

    private LintingThread lintingThread;

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

        if (lintingThread != null && lintingThread.isAlive()) {
            lintingThread.stopAndFinishProgressHandle();
        }

        try {
            lintingThread = new LintingThread(d);
            lintingThread.start();
        } catch (NullPointerException ex) {
            // Can happen, according to http://statistics.netbeans.org/analytics/detail.do?id=221301
        }
    }
}
