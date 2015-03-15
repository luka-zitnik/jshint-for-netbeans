/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lukazitnik.jshint;

import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author luka
 */
public class JSHintTest {

    @Test
    public void testLint() {
        JSHint jshint = new JSHint();
        LinkedList<JSHintError> errors = jshint.lint("a;");
        JSHintError head = errors.element();

        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(1 == head.getLine());
        Assert.assertEquals("Expected an assignment or function call and instead saw an expression.", head.getReason());
    }

}
