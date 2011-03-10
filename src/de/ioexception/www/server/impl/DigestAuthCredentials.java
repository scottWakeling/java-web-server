package de.ioexception.www.server.impl;

import de.ioexception.www.http.HttpMethod;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * http://tools.ietf.org/html/rfc2617#section-3.5
 * http://en.wikipedia.org/wiki/Digest_access_authentication
 *
 * @author Scott Wakeling
 */
public class DigestAuthCredentials {

    private String username;
    private String realm;
    private String nonce;
    private String uri;
    private String qop;
    private String nc;
    private String cnonce;
    private String response;

    /**
     * Extracts named parameters from a credentials string as returned by a
     * client browser in response to a WWW-Authenticate 401
     *
     * @param credentials
     */
    public DigestAuthCredentials(String credentials) {
        
        username = fetchParam("username", credentials);
        realm = fetchParam("realm", credentials);
        nonce = fetchParam("nonce", credentials);
        uri = fetchParam("uri", credentials);
        qop = fetchParam("qop", credentials);
        nc = fetchParam("nc", credentials);
        cnonce = fetchParam("cnonce", credentials);
        response = fetchParam("response", credentials);
    }

    public String getResponse() {
        return response;
    }

    public String getUsername() {
        return username;
    }

    public String getNonce() {
        return nonce;
    }

    public String getURI() {
        return uri;
    }

    public String getRealm() {
        return realm;
    }

    public String getQOP() {
        return qop;
    }

    public String getNC() {
        return nc;
    }

    public String getCNonce() {
        return cnonce;
    }

    /**
     * Returns the MD5 hash of username:realm:password
     *
     * @param password
     * @return
     */
    String getHA1(String password) {
        return digest(getUsername() + ":" + getRealm() + ":" + password);
    }

    /**
     * Returns the MD5 hash of method:uri
     *
     * @param method
     * @return
     */
    String getHA2(HttpMethod method) {
        return digest(method.toString()+":"+getURI());
    }

    /**
     * Calculate the expected MD5 response for a given password and HTTP method;
     * used to compare against the result of getResponse(), to see if the same
     * password was used for the original digest
     *
     * @param password
     * @param method
     * @return
     */
    public String calcExpectedResponse(String password, HttpMethod method) {
        return digest(getHA1(password) + ":" + getNonce() + ":" + getNC() + ":" + getCNonce() + ":" + getQOP() + ":" + getHA2(method));
    }

    /**
     * Returns an MD5 digest of the input string
     *
     * @param input
     * @return
     */
    private String digest(String input) {
        String digest = null;
        byte b[] = null;
        try {
            b = java.security.MessageDigest.getInstance("MD5").digest(input.getBytes());
            BigInteger i = new java.math.BigInteger(1, b);
            digest = i.toString(16);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(DigestAuthHttpWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return digest;
    }

    /**
     * Extracts a named parameter from the given credentials string, as
     * sent by the client's browser in response to a WWW-Authenticate 401
     *
     * Assumes parameter values never contain commas, double quotes, \r, or \n
     *
     * Some params are double-quoted by some browsers and not by others,
     * e.g. 'qop' is double-quoted by Safari but not by Firefox, hence
     * some retry code is necessary in places below
     *
     * @param param - e.g. "username"
     * @param credentials - e.g. "username=/"user/", reponse=/" ... 
     * @return - the value of the requested parameter
     */
    private String fetchParam(String param, String credentials) {
        String value = null;
        if (!credentials.isEmpty())
        {
            //  Look for the param in quoted form first, the most common
            Scanner s = new Scanner(credentials);
            value = s.findInLine(param+"=" + "\"[^,\"\r\n]*");
            if (value != null)
            {
                String[] tokList = value.split("=\"", 2);
                value = tokList[1];
            }
            else
            {
                //  The param does not exist in quoted form, try unquoted,
                //  nc is often delivered in this form, and I've seen
                //  different browsers deliver qop as both quoted and unquoted
                value = s.findInLine(param+"=" + "[^,\"\r\n]*");
                if (value != null)
                {
                    String[] tokList = value.split("=", 2);
                    value = tokList[1];
                }
            }
        }
        return value;
    }
}
