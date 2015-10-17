package lukazitnik.jshint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import lukazitnik.jshint.options.JSHintPanel;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.json.JsonParser;
import org.mozilla.javascript.json.JsonParser.ParseException;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbPreferences;

public class JSHint {

    private static JSHint instance;
    private final Scriptable scope;
    private Function jshint;

    private JSHint() throws IOException {
        Context cx = Context.enter();
        scope = cx.initStandardObjects();

        try {
            jshint = evaluateJSHint(cx, scope);
        } finally {
            Context.exit();
        }
    }

    public static JSHint getInstance () throws IOException {
        if (instance == null) {
            instance = new JSHint();
        }

        return instance;
    }

    public LinkedList<JSHintError> lint(Document d) {
        Context cx = Context.enter();
        LinkedList<JSHintError> result = new LinkedList<>();
        FileObject fo = NbEditorUtilities.getFileObject(d);

        try {
            Scriptable config = getConfig(cx, fo);
            Object globals = config.get("globals", config);

            if (globals == Scriptable.NOT_FOUND) {
                globals = Context.getUndefinedValue();
            }

            config.delete("globals");

            Object args[] = {d.getText(0, d.getLength()), config, globals};
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
            Scriptable config = getConfig(cx, fo);
            Object globals = config.get("globals", config);

            if (globals == Scriptable.NOT_FOUND) {
                globals = Context.getUndefinedValue();
            }

            config.delete("globals");

            Object args[] = {fo.asText(), config, globals};
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

    private Scriptable getConfig(Context cx, FileObject fo) throws ParseException, IOException {
        JsonParser parser = new JsonParser(cx, scope);
        FileObject config = findConfig(fo);
        String json = config == null ? "{}" : config.asText();
        return (Scriptable) parser.parseValue(json);
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

    private Function evaluateJSHint(Context cx, Scriptable scope) throws IOException {
        Reader in = getReader();
        cx.evaluateReader(scope, in, "jshint.js", 1, null);
        return (Function) scope.get("JSHINT", scope);
    }

    private Reader getReader() throws IOException {

        // jshint.js is the web bundle of the JSHint, downloaded from
        // https://raw.githubusercontent.com/jshint/jshint/master/dist/jshint.js
        String defaultJSFile = InstalledFileLocator.getDefault().locate("jshint.js", "lukazitnik.jshint", false).getPath();

        String jSFile = NbPreferences.forModule(JSHintPanel.class).get("jshint.js", defaultJSFile);

        return new BufferedReader(new FileReader(new File(jSFile)));
    }

    // Async executions of this method cause org.mozilla.javascript.JavaScriptException:
    // TypeError: Cannot read property "id" from null (jshint.js#12315) and similar.
    // Maybe it would be cheaper to have multiple instances of this class.
    private synchronized NativeArray callJSHint(Context cx, Object[] args) {
        jshint.call(cx, scope, scope, args);
        return (NativeArray) jshint.get("errors", null);
    }
}
