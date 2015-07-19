package lukazitnik.jshint.options;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

class JSFileVerifier extends InputVerifier {
    private Scriptable scope;

    JSFileVerifier() {
        Context cx = Context.enter();
        scope = cx.initStandardObjects();
        Context.exit();
    }

    @Override
    public boolean verify(JComponent jc) {
        File file = new File(((JTextField) jc).getText());
        Context cx = Context.enter();
        if (scope.has("JSHINT", scope)) {
            scope = cx.initStandardObjects();
        }
        try {
            Reader in = new BufferedReader(new FileReader(file));
            cx.evaluateReader(scope, in, "jshint.js", 1, null);
        } catch (Exception ex) {
            return false;
        } finally {
            Context.exit();
        }
        return scope.has("JSHINT", scope) != false;
    }

}
