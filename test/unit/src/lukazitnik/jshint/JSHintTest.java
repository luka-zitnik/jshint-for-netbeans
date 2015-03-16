package lukazitnik.jshint;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Test;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author luka
 */
public class JSHintTest {

    @Test
    public void testLint() throws IOException {
        FileSystem fs = FileUtil.createMemoryFileSystem();
        FileObject fo = fs.getRoot().createData("index.js");
        PrintWriter out = (new PrintWriter(fo.getOutputStream()));
        out.write("a;");
        out.close();

        JSHint jshint = new JSHint();
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

}
