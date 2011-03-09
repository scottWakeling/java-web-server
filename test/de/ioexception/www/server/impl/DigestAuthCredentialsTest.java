package de.ioexception.www.server.impl;

import de.ioexception.www.http.HttpMethod;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author scott
 */
public class DigestAuthCredentialsTest {


    /**
     * Test of default constructor of class DigestAuthCredentials.
     */
    @Test
    public void testDigestAuthCredentials() {
        System.out.println("testDigestAuthCredentials");
        DigestAuthCredentials creds = new DigestAuthCredentials("username=\"user\", " 
                                                                + "realm=\"Protected Area\", "
                                                                + "nonce=\"\", "
                                                                + "uri=\"/rince wind/index.html\", "
                                                                + "qop=auth, "
                                                                + "nc=00000001, "
                                                                + "cnonce=\"0a4f113b\", "
                                                                + "response=\"8a6291c576b982d5bdec1a9cca884688\"");

        //  Test out of order access of extracted credential parameters
        assertEquals("user", creds.getUsername());
        assertEquals("auth", creds.getQOP());
        assertEquals("8a6291c576b982d5bdec1a9cca884688", creds.getResponse());
        assertEquals("00000001", creds.getNC());
        assertEquals("Protected Area", creds.getRealm());
        assertEquals("0a4f113b", creds.getCNonce());
        assertEquals("/rince wind/index.html", creds.getURI());
    }

    /**
     * Test of getHA1 method of class DigestAuthCredentials.
     * 
     */
    @Test
    public void testGetHA1() {
        System.out.println("testGetHA1");
        DigestAuthCredentials creds = new DigestAuthCredentials("username=\"Mufasa\", "
                                                                + "realm=\"testrealm@host.com\", "
                                                                + "nonce=\"dcd98b7102dd2f0e8b11d0f600bfb0c093\", "
                                                                + "uri=\"/dir/index.html\", "
                                                                + "qop=auth, "
                                                                + "nc=00000001, "
                                                                + "cnonce=\"0a4f113b\", "
                                                                + "response=\"6629fae49393a05397450978507c4ef1\", "
                                                                + "opaque=\"5ccc069c403ebaf9f0171e9517f40e41\"");

        assertEquals("939e7578ed9e3c518a452acee763bce9", creds.getHA1("Circle Of Life"));
    }

    /**
     * Test of getHA2 method of class DigestAuthCredentials.
     *
     */
    @Test
    public void testGetHA2() {
        System.out.println("testGetHA2");
        DigestAuthCredentials creds = new DigestAuthCredentials("username=\"Mufasa\", "
                                                                + "realm=\"testrealm@host.com\", "
                                                                + "nonce=\"dcd98b7102dd2f0e8b11d0f600bfb0c093\", "
                                                                + "uri=\"/dir/index.html\", "
                                                                + "qop=auth, "
                                                                + "nc=00000001, "
                                                                + "cnonce=\"0a4f113b\", "
                                                                + "response=\"6629fae49393a05397450978507c4ef1\", "
                                                                + "opaque=\"5ccc069c403ebaf9f0171e9517f40e41\"");

        assertEquals("39aff3a2bab6126f332b942af96d3366", creds.getHA2(HttpMethod.GET));
    }

    /**
     * Test of calcResponse method of class DigestAuthCredentials.
     *
     */
    @Test
    public void testCalcResponse() {
        System.out.println("testCalcResponse");
        DigestAuthCredentials creds = new DigestAuthCredentials("username=\"Mufasa\", "
                                                                + "realm=\"testrealm@host.com\", "
                                                                + "nonce=\"dcd98b7102dd2f0e8b11d0f600bfb0c093\", "
                                                                + "uri=\"/dir/index.html\", "
                                                                + "qop=auth, "
                                                                + "nc=00000001, "
                                                                + "cnonce=\"0a4f113b\", "
                                                                + "response=\"6629fae49393a05397450978507c4ef1\", "
                                                                + "opaque=\"5ccc069c403ebaf9f0171e9517f40e41\"");

        assertEquals("6629fae49393a05397450978507c4ef1", creds.calcExpectedResponse("Circle Of Life", HttpMethod.GET));
    }
}