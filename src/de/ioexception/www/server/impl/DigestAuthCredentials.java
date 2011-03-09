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
    
    public DigestAuthCredentials(String credentials) {
        username = fetchParam("username", credentials, "\"[^\"\r\n]*", "=\"");
        realm = fetchParam("realm", credentials, "\"[^\"\r\n]*", "=\"");
        nonce = fetchParam("nonce", credentials, "\"[^\"\r\n]*", "=\"");
        uri = fetchParam("uri", credentials, "\"[^\"\r\n]*", "=\"");
        qop = fetchParam("qop", credentials, "[^\"\r\n,]*", "=");
        nc = fetchParam("nc", credentials, "[^\"\r\n,]*", "=");
        cnonce = fetchParam("cnonce", credentials, "\"[^\"\r\n]*", "=\"");
        response = fetchParam("response", credentials, "\"[^\"\r\n]*", "=\"");
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

    String getHA1(String password) {
        return digest(getUsername() + ":" + getRealm() + ":" + password);
    }

    String getHA2(HttpMethod method) {
        return digest(method.toString()+":"+getURI());
    }

    public String calcExpectedResponse(String password, HttpMethod method) {
        return digest(getHA1(password) + ":" + getNonce() + ":" + getNC() + ":" + getCNonce() + ":" + getQOP() + ":" + getHA2(method));
    }

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

    private String fetchParam(String param, String credentials, String paramRegex, String splitter) {
        String value = null;
        if (!credentials.isEmpty())
        {
            Scanner s = new Scanner(credentials);
            value = s.findInLine(param+"=" + paramRegex);
            if (value != null)
            {
                String[] tokList = value.split(splitter, 2);
                value = tokList[1];
            }
        }
        return value;
    }
}
