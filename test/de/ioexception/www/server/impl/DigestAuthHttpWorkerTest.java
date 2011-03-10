/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ioexception.www.server.impl;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author scottw
 */
public class DigestAuthHttpWorkerTest {

    public DigestAuthHttpWorkerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of generateNonce method, of class DigestAuthHttpWorker.
     */
    @Test
    public void testGenerateNonce() {
        System.out.println("generateNonce");
        String nonce = DigestAuthHttpWorker.generateNonce();
        assertTrue(DigestAuthHttpWorker.nonceValid(nonce));
        assertFalse(DigestAuthHttpWorker.nonceValid("not-a-nonce"));
    }

}