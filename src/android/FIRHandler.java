package org.elastos.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import org.elastos.carrier.*;

public class FIRHandler implements FriendInviteResponseHandler {
	private static String TAG = "FIRHandler";

	private int mHandlerId;
	public CallbackContext mCallbackContext = null;

	public FIRHandler(int id, CallbackContext callbackContext) {
		this.mHandlerId = id;
		this.mCallbackContext = callbackContext;
	}

	private void sendEvent(JSONObject info) throws JSONException {
		info.put("id", mHandlerId);
		if (mCallbackContext != null) {
			PluginResult result = new PluginResult(PluginResult.Status.OK, info);
			result.setKeepCallback(true);
			mCallbackContext.sendPluginResult(result);
		}
	}

	@Override
	public void onReceived(String from, int status, String reason, String data) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onReceived");
			r.put("from", from);
			r.put("status", status);
			r.put("reason", reason);
			r.put("data", data);
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}

