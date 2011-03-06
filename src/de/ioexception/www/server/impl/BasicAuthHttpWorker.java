package de.ioexception.www.server.impl;

import de.ioexception.www.Http;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import util.Base64;
import de.ioexception.www.http.HttpRequest;
import de.ioexception.www.http.HttpResponse;
import de.ioexception.www.http.HttpStatusCode;
import de.ioexception.www.http.HttpVersion;
import de.ioexception.www.http.impl.BasicHttpResponse;

/**
 * @author Benjamin Erb
 *
 */
public class BasicAuthHttpWorker extends BasicHttpWorker
{
	private static final Map<String, String> authentications;
	private static final String realm = "Protected Area";

	static
	{
		authentications = new HashMap<String, String>();
		authentications.put("test", "secret");
		authentications.put("user", "1234");
	};

	public BasicAuthHttpWorker(Socket socket, BasicHttpServer server)
	{
		super(socket, server);
	}

	@Override
	protected HttpResponse handleRequest(HttpRequest request)
	{
		if (request.getHeaders().containsKey(Http.AUTHORIZATION))
		{
			String authValue = request.getHeaders().get(Http.AUTHORIZATION);
			String[] authValues = authValue.split(" ", 2);
			String type = authValues[0];
			String values = authValues[1];
			if (type.equalsIgnoreCase("Basic"))
			{
				String auth = new String(Base64.decode(values));
				String[] authentication = auth.split(":", 2);
				if (authentications.containsKey(authentication[0]) && authentications.get(authentication[0]).equals(authentication[1]))
				{
					return super.handleRequest(request);
				}
			}
		}
		BasicHttpResponse response = new BasicHttpResponse();
		response.setStatusCode(HttpStatusCode.UNAUTHORIZED);
		response.getHeaders().put(Http.WWW_AUTHENTICATE, "Basic realm=\"" + realm + "\"");
		return response;
	}
}