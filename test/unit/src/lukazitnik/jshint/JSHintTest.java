package lukazitnik.jshint;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Test;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;

public class JSHintTest extends NbTestCase {

    public JSHintTest(String name) {
        super(name);
    }

    public static junit.framework.Test suite() {
        return NbModuleSuite.create(JSHintTest.class, null, null);
    }

    @Test
    public void testLint() throws IOException {
        FileSystem fs = FileUtil.createMemoryFileSystem();
        FileObject fo = fs.getRoot().createData("index.js");
        PrintWriter out = (new PrintWriter(fo.getOutputStream()));

        out.write("a;");
        out.close();

        JSHint jshint = JSHint.getInstance();
        LinkedList<JSHintError> errors = jshint.lint(fo);
        JSHintError head = errors.element();

        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(1 == head.getLine());
        Assert.assertEquals("Expected an assignment or function call and instead saw an expression.", head.getReason());
    }

    @Test
    public void testFindFile() throws IOException {
        FileSystem fs = FileUtil.createMemoryFileSystem();
        FileObject hasFile = fs.getRoot().createFolder("hasFile");
        FileObject file = hasFile.createData("file");
        FileObject childFolder = hasFile.createFolder("childFolder");
        FileObject result = JSHint.findFile("file", childFolder);

        Assert.assertEquals(file, result);
    }

    @Test
    public void testLintWithConfig() throws IOException {
        FileObject root = FileUtil.createMemoryFileSystem().getRoot();
        FileObject jsFo = root.createData("index.js");
        PrintWriter jsOut = (new PrintWriter(jsFo.getOutputStream()));

        jsOut.write("while (day)\n  shuffle();");
        jsOut.close();

        FileObject configFo = root.createData(".jshintrc");
        PrintWriter configOut = (new PrintWriter(configFo.getOutputStream()));

        configOut.write("{\"curly\":true,\"undef\":true}");
        configOut.close();

        JSHint jshint = JSHint.getInstance();
        LinkedList<JSHintError> errors = jshint.lint(jsFo);

        Assert.assertEquals(3, errors.size());
        Assert.assertEquals("'shuffle' is not defined.", errors.pop().getReason());
        Assert.assertEquals("'day' is not defined.", errors.pop().getReason());
        Assert.assertEquals("Expected '{' and instead saw 'shuffle'.", errors.pop().getReason());
    }

    @Test
    public void testGlobalsOption() throws IOException {
        FileObject root = FileUtil.createMemoryFileSystem().getRoot();
        FileObject jsFo = root.createData("index.js");
        PrintWriter jsOut = (new PrintWriter(jsFo.getOutputStream()));

        jsOut.write("a(); b = 1;");
        jsOut.close();

        FileObject config = root.createData(".jshintrc");
        PrintWriter configOut = (new PrintWriter(config.getOutputStream()));

        configOut.write("{\"undef\":true,\"globals\":{\"a\":false,\"b\":true}}");
        configOut.close();

        JSHint jshint =  JSHint.getInstance();
        LinkedList<JSHintError> errors = jshint.lint(jsFo);

        Assert.assertEquals(0, errors.size());
    }

}
