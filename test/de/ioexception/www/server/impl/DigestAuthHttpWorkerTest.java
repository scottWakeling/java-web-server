/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.ioexception.www.server.impl;

import java.util.Calendar;
import java.util.Date;
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
        String nonce = DigestAuthHttpWorker.generateNonce("/index.html", new Date());
        assertEquals(DigestAuthHttpWorker.NonceStatus.VALID, DigestAuthHttpWorker.getNonceStatus(nonce, "/index.html"));
        assertEquals(DigestAuthHttpWorker.NonceStatus.INVALID, DigestAuthHttpWorker.getNonceStatus(nonce, "/the/world/turtle.html")); // good nonce, bad URI
        assertEquals(DigestAuthHttpWorker.NonceStatus.INVALID, DigestAuthHttpWorker.getNonceStatus("not-a-nonce", "/index.html")); // bad nonce, good URI
    }

    /**
     * Test of getNonceStatus method, of class DigestAuthHttpWorker.
     */
    @Test
    public void testGetNonceStatusStale() {
        System.out.println("getNonceStatusStale");
        Calendar expired = Calendar.getInstance();
        Date now = new Date();

        //  Test for stale
        expired.setTime(now);
        expired.setTimeInMillis(expired.getTimeInMillis() - DigestAuthHttpWorker.NONCE_LIFESPAN);
        String nonce = DigestAuthHttpWorker.generateNonce("/light-fantastic.html", expired.getTime());
        assertEquals(DigestAuthHttpWorker.NonceStatus.STALE, DigestAuthHttpWorker.getNonceStatus(nonce, "/light-fantastic.html"));

        //  Test for valid - 59 seconds ago should be valid
        expired.setTimeInMillis(now.getTime() - (DigestAuthHttpWorker.NONCE_LIFESPAN - 1000));
        nonce = DigestAuthHttpWorker.generateNonce("/light-fantastic.html", expired.getTime());
        assertEquals(DigestAuthHttpWorker.NonceStatus.VALID, DigestAuthHttpWorker.getNonceStatus(nonce, "/light-fantastic.html"));
    }
}