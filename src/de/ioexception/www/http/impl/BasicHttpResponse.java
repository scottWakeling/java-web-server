package de.ioexception.www.http.impl;

import de.ioexception.www.Http;
import de.ioexception.www.http.HttpResponse;
import de.ioexception.www.http.HttpStatusCode;
import de.ioexception.www.http.HttpVersion;
import java.util.HashMap;

public class BasicHttpResponse extends BasicHttpMessage implements HttpResponse
{
	HttpStatusCode statusCode;

        public BasicHttpResponse()
        {
            setHeaders(new HashMap<String, String>());
            getHeaders().put(Http.CONTENT_LENGTH, "0");
            setEntity(null);
            setVersion(HttpVersion.VERSION_1_1);
        }

	@Override
	public HttpStatusCode getStatusCode()
	{
		return statusCode;
	}

	public void setStatusCode(HttpStatusCode statusCode)
	{
		this.statusCode = statusCode;
	}
	
	
}
