/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author scottw
 */
public class Base64Test {

    private static String FUBAR_IN = "fubar";
    private static String FUBAR_OUT = "ZnViYXI=";

    /**
     * Test of encodeToString method, of class Base64.
     */
    @Test
    public void testEncodeToString() {
        String input = FUBAR_IN;
        String output = Base64.encodeToString(input.getBytes(), true);
        assertEquals(FUBAR_OUT, output);
    }

    /**
     * Test of decode method, of class Base64.
     */
    @Test
    public void testDecode_String() {
        String result = new String(Base64.decode(FUBAR_OUT));
        assertEquals(FUBAR_IN, result);
    }


}