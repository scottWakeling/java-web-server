package de.ioexception.www.server.impl;

import de.ioexception.www.http.HttpRequest;
import de.ioexception.www.http.HttpResponse;
import de.ioexception.www.http.HttpStatusCode;
import de.ioexception.www.http.HttpVersion;
import de.ioexception.www.http.impl.BasicHttpResponse;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
    };

    public DigestAuthHttpWorker(Socket socket, BasicHttpServer server) {
        super(socket, server);
    }

    @Override
    protected HttpResponse handleRequest(HttpRequest request) {
        if (request.getHeaders().containsKey("Authorization")) {
            String authValue = request.getHeaders().get("Authorization");
            String[] authValues = authValue.split(" ", 2);
            String type = authValues[0];
            String values = authValues[1];
            if (type.equalsIgnoreCase("Digest")) {
                DigestAuthCredentials credentials = new DigestAuthCredentials(values);
                if (credentials.getUsername() != null) {
                    if (authentications.containsKey(credentials.getUsername())) {
                        //  Using the provided user's password, calculate the expected MD5 response digest
                        //  and compare to what has arrived in the header
                        //  TODO: fail auth based on nonce expiration, nc etc.
                        String expResponse = credentials.calcExpectedResponse(authentications.get(credentials.getUsername()), request.getHttpMethod());
                        while (expResponse.length() < 32)
                            expResponse = "0" + expResponse;
                        if (credentials.getResponse().equals(expResponse)) {
                            return super.handleRequest(request);
                        }
                    }
                }
            }
        }

        //  TODO: use a changing nonce value that expires after a given time
        //  TODO: Authentication-Info header line?
        //  TODO: Specify MD5 as algorithm in header?
        //  TODO: qop, nc work
        BasicHttpResponse response = new BasicHttpResponse();
        response.setStatusCode(HttpStatusCode.UNAUTHORIZED);
        response.getHeaders().put("WWW-Authenticate", "Digest realm=\"" + realm + "\", qop=\"auth\", nonce=\"dcd98b7102dd2f0e8b11d0f600bfb0c093\"");
        return response;
    }
}
