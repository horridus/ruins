package cek.ruins.jws.listeners;

import org.jwebsocket.listener.WebSocketServerTokenEvent;
import org.jwebsocket.listener.WebSocketServerTokenListener;
import org.jwebsocket.token.Token;

public abstract class GenericListener implements WebSocketServerTokenListener {
	private static Integer GENERIC_ERR = 1;

	protected Token createGenericErrorResponse(WebSocketServerTokenEvent aEvent, Token aInToken, String message) {
		Token errResponse = aEvent.createResponse(aInToken);
		errResponse.setInteger("code", GenericListener.GENERIC_ERR);

		if (message != null)
			errResponse.setString("msg", message);

		return errResponse;
	}
}
