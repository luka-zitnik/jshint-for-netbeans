package lukazitnik.jshint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

    public LinkedList<JSHintError> lint(FileObject fo) {
        Context cx = Context.enter();
        Scriptable scope = cx.initStandardObjects();
        LinkedList<JSHintError> result = new LinkedList<JSHintError>();

        try {
            Function jshint = getJSHint(cx, scope);
            Scriptable config = jsonToScriptable(cx, scope, getConfig(fo));
            Object args[] = {fo.asText(), config};

            jshint.call(cx, scope, scope, args);

            NativeArray errors = (NativeArray) jshint.get("errors", null);

            for (Object error : errors) {
                if (error == null) {
                    // I don't know what null means
                    // I believe it follows js files with "Too many errors."
                   continue;
                }

                result.push(new JSHintError((NativeObject) error));
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ParseException ex) {
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

    private Function getJSHint(Context cx, Scriptable scope) throws IOException {
        Reader in = getReader();
        cx.evaluateReader(scope, in, "jshint.js", 1, null);
        return (Function) scope.get("JSHINT", scope);
    }

    private Reader getReader() throws IOException {

        // jshint.js is the web bundle of the JSHint, downloaded from
        // https://raw.githubusercontent.com/jshint/jshint/master/dist/jshint.js
        InputStream stream = getClass().getResourceAsStream("jshint.js");

        return new BufferedReader(new InputStreamReader(stream));
    }
}
