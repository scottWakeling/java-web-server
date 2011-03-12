package de.ioexception.www.server.impl;

import de.ioexception.www.http.HttpRequest;
import de.ioexception.www.http.HttpResponse;
import de.ioexception.www.http.HttpStatusCode;
import de.ioexception.www.http.impl.BasicHttpResponse;
import de.ioexception.www.server.cache.Cache;
import de.ioexception.www.server.cache.impl.LRUCache;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import util.Base64;

/**
 *
 * @author Scott Wakeling
 */
public class DigestAuthHttpWorker extends BasicHttpWorker {

    private static final Map<String, String> authentications;
    private static final String realm = "Protected Area";

    static {
        authentications = new HashMap<String, String>();
        authentications.put("test", "secret");
        authentications.put("user", "1234");
    }

    ;
    private static final Cache<String, String> nonceCache = new LRUCache<String, String>(100);

    enum NonceStatus {

        VALID,
        INVALID,
        STALE
    }

    static final int NONCE_LIFESPAN = 60000;

    public DigestAuthHttpWorker(Socket socket, BasicHttpServer server) {
        super(socket, server);
    }

    @Override
    protected HttpResponse handleRequest(HttpRequest request) {
        String stale = "\"false\"";

        if (request.getHeaders().containsKey("Authorization")) {
            String authValue = request.getHeaders().get("Authorization");
            String[] authValues = authValue.split(" ", 2);
            String type = authValues[0];
            String values = authValues[1];
            if (type.equalsIgnoreCase("Digest")) {
                //  TODO: Check the opaque field is correct for the realm being accessed
                DigestAuthCredentials credentials = new DigestAuthCredentials(values);
                if (credentials.getUsername() != null && authentications.containsKey(credentials.getUsername())) {
                    //  Using the provided user's password, calculate the expected MD5 response digest
                    //  and compare to what has arrived in the header, did they use the right password
                    //  to construct their response hash?
                    String expResponse = credentials.calcExpectedResponse(authentications.get(credentials.getUsername()), request.getHttpMethod());
                    while (expResponse.length() < 32) {
                        expResponse = "0" + expResponse;
                    }
                    if (credentials.getResponse().equals(expResponse)) {
                        //  Check the nonce value is valid, set the stale directive if it has expired
                        NonceStatus status = getNonceStatus(credentials.getNonce(), request.getRequestUri());
                        switch (status) {
                            case VALID:
                                return super.handleRequest(request);
                            case STALE:
                                stale = "\"true\"";
                                break;
                            case INVALID:
                            default:
                                stale = "\"false\"";
                                break;
                        }
                    }
                }
            }
        }

        //  TODO: Authentication-Info header line?
        //  TODO: qop, nc work

        //  The opaque field must be returned by the client whenever they access a URI in the same protection space, i.e. realm
        //  The opaque field is a Base64 encoding of the realm being accessed
        //  TODO: Use the realm being accessed, not the hardcoded "Protected Area" realm
        String opaque = ", opaque=\"" + Base64.encodeToString(realm.getBytes(), false) + "\"";
        BasicHttpResponse response = new BasicHttpResponse();
        response.setStatusCode(HttpStatusCode.UNAUTHORIZED);
        response.getHeaders().put("WWW-Authenticate", "Digest realm=\"" + realm + "\", qop=\"auth\", nonce=\"" + generateNonce(request.getRequestUri(), new Date()) + "\"" + opaque + ", stale=" + stale);
        return response;
    }

    /**
     * Generates a nonce value and adds it to the nonceCache as a nonce=URI pair
     *
     * @return A Base64 encoded nonce value
     */
    static String generateNonce(String uri, Date now) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        String nonce = sdf.format(now);
        nonce = Base64.encodeToString(nonce.getBytes(), false);
        nonceCache.put(nonce, uri);
        return nonce;
    }

    /**
     * Checks a Base64 encoded nonce value for validity. A valid nonce can be
     * found in nonceCache with a URI equal to the one being requested and not
     * have exceeded its 60 second lifespan. Browsers that make use of the 'stale'
     * directive will not bother a user for credentials on nonce expiration.
     *
     * @param nonce - A nonce value, as generated by generateNonce(),
     * and returned by a client browser
     * @return
     */
    static NonceStatus getNonceStatus(String nonce, String uri) {
        byte[] bytes = Base64.decode(nonce);
        if (bytes != null && bytes.length > 0) {
            String nonceString = new String(bytes);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
                Date then = sdf.parse(nonceString);
                Date now = new Date();
                if (nonceCache.get(nonce).equals(uri)) {
                    if (now.after(then) && (now.getTime() - then.getTime() < NONCE_LIFESPAN)) {
                        //  The nonce is in the cache and has not expired
                        return NonceStatus.VALID;
                    } else {
                        //  The nonce is in the cache but has expired
                        System.out.println("now = " + now.getTime() + " then = " + then.getTime());
                        System.out.println(now.getTime() - then.getTime() + " is >= " + NONCE_LIFESPAN);
                        return NonceStatus.STALE;
                    }
                }
            }
            catch (ParseException ex) {
            //  Definitely not a valid nonce
            }
        }
        return NonceStatus.INVALID;
    }
}
