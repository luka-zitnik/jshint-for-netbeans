package lukazitnik.jshint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.modules.InstalledFileLocator;

public class JSHint {

    public final static JSHint instance = new JSHint();
    private Scriptable scope;
    private Function jshint;

    private JSHint() {
        Context cx = Context.enter();
        scope = cx.initStandardObjects();

        try {
            jshint = evaluateJSHint(cx, scope);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            Context.exit();
        }
    }

    public LinkedList<JSHintError> lint(Document d) {
        Context cx = Context.enter();
        LinkedList<JSHintError> result = new LinkedList<>();
        FileObject fo = NbEditorUtilities.getFileObject(d);

        try {
            Scriptable config = jsonToScriptable(cx, scope, getConfig(fo));
            Object args[] = {d.getText(0, d.getLength()), config};
            NativeArray errors = callJSHint(cx, args);

            for (Object error : errors) {
                if (error == null) {
                    // Null is added to the end in case of an "Unrecoverable
                    // syntax error." or "Too many errors.", so we could break
                    // out of the loop just as well
                    continue;
                }

                result.push(new JSHintError((NativeObject) error));
            }
        } catch (IOException | ParseException | BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            Context.exit();
        }

        return result;
    }

    public LinkedList<JSHintError> lint(FileObject fo) {
        Context cx = Context.enter();
        LinkedList<JSHintError> result = new LinkedList<>();

        try {
            Scriptable config = jsonToScriptable(cx, scope, getConfig(fo));
            Object args[] = {fo.asText(), config};
            NativeArray errors = callJSHint(cx, args);

            for (Object error : errors) {
                if (error == null) {
                    // Null is added to the end in case of an "Unrecoverable
                    // syntax error." or "Too many errors.", so we could break
                    // out of the loop just as well
                    continue;
                }

                result.push(new JSHintError((NativeObject) error));
            }
        } catch (IOException | ParseException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            Context.exit();
        }

        return result;
    }

    private JSONObject getConfig(FileObject fo) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        FileObject config = findConfig(fo);

        if (config == null) {
            return (JSONObject) parser.parse("{}");
        }

        return (JSONObject) parser.parse(config.asText());
    }

    private FileObject findConfig(FileObject fo) {
        return findFile(".jshintrc", fo.getParent());
    }

    public static FileObject findFile(String name, FileObject folder) {
        FileObject fo = folder.getFileObject(name, "");

        if (fo != null && fo.isData()) {
            return fo;
        }

        if (folder.isRoot()) {
            return null;
        }

        return findFile(name, folder.getParent());
    }

    private Scriptable jsonToScriptable(Context cx, Scriptable scope, JSONObject obj) {
        Scriptable scriptable = cx.newObject(scope);

        for (Object key : obj.keySet()) {
            scriptable.put(key.toString(), scriptable, obj.get(key).toString());
        }

        return scriptable;
    }

    private Function evaluateJSHint(Context cx, Scriptable scope) throws IOException {
        Reader in = getReader();
        cx.evaluateReader(scope, in, "jshint.js", 1, null);
        return (Function) scope.get("JSHINT", scope);
    }

    private Reader getReader() throws IOException {

        // jshint.js is the web bundle of the JSHint, downloaded from
        // https://raw.githubusercontent.com/jshint/jshint/master/dist/jshint.js
        File file = InstalledFileLocator.getDefault().locate("jshint.js", "lukazitnik.jshint", false);

        return new BufferedReader(new FileReader(file));
    }

    // Async executions of this method cause org.mozilla.javascript.JavaScriptException:
    // TypeError: Cannot read property "id" from null (jshint.js#12315) and similar.
    // Maybe it would be cheaper to have multiple instances of this class.
    private synchronized NativeArray callJSHint(Context cx, Object[] args) {
        jshint.call(cx, scope, scope, args);
        return (NativeArray) jshint.get("errors", null);
    }
}
