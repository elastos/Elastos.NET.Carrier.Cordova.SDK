package org.elastos.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.elastos.carrier.session.Session;
import org.elastos.carrier.session.SessionRequestCompleteHandler;
import org.json.JSONException;
import org.json.JSONObject;

public class SRCHandler implements SessionRequestCompleteHandler {
	private static String TAG = "FIRHandler";

	private int mHandlerId;
	public CallbackContext mCallbackContext = null;

	public SRCHandler(int id, CallbackContext callbackContext) {
		this.mHandlerId = id;
		this.mCallbackContext = callbackContext;
	}

	private void sendEvent(JSONObject info) throws JSONException {
		info.put("id", mHandlerId);
		if (mCallbackContext != null) {
			PluginResult result = new PluginResult(PluginResult.Status.OK, info);
			// result.setKeepCallback(true);
			mCallbackContext.sendPluginResult(result);
		}
	}

	@Override
	public void onCompletion(Session session, int status, String reason, String sdp) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onCompletion");
			r.put("status", status);
			r.put("reason", reason);
			r.put("sdp", sdp);
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}

