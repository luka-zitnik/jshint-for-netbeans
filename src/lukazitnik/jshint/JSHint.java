package lukazitnik.jshint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import sun.org.mozilla.javascript.Context;
import sun.org.mozilla.javascript.Function;
import sun.org.mozilla.javascript.NativeArray;
import sun.org.mozilla.javascript.NativeObject;
import sun.org.mozilla.javascript.Scriptable;

/**
 *
 * @author luka
 */
public class JSHint {

    public JSHint() {
    }

    public LinkedList<JSHintError> lint(String script) {
        Context cx = Context.enter();
        Scriptable scope = cx.initStandardObjects();
        LinkedList<JSHintError> result = new LinkedList<JSHintError>();

        try {
            Function jshint = getJSHint(cx, scope);
            Object args[] = {script};

            jshint.call(cx, scope, scope, args);

            NativeArray errors = (NativeArray) jshint.get("errors", null);

            for (Object error : errors) {
                result.push(new JSHintError((NativeObject) error));
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            Context.exit();
        }

        return result;
    }

    private Function getJSHint(Context cx, Scriptable scope) throws IOException {
        Reader in = getReader();
        cx.evaluateReader(scope, in, "jshint.js", 1, null);
        return (Function) scope.get("JSHINT", scope);
    }

    private Reader getReader() throws IOException {
        InputStream stream = getClass().getResourceAsStream("jshint.js");
        return new BufferedReader(new InputStreamReader(stream));
    }
}
