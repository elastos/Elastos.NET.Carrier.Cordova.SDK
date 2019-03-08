 /*
  * Copyright (c) 2018 Elastos Foundation
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy
  * of this software and associated documentation files (the "Software"), to deal
  * in the Software without restriction, including without limitation the rights
  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  * copies of the Software, and to permit persons to whom the Software is
  * furnished to do so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in all
  * copies or substantial portions of the Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  * SOFTWARE.
  */

 package org.elastos.trinity.plugins.carrier;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.net.InetSocketAddress;
import java.net.ServerSocket;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.PresenceStatus;
import org.elastos.carrier.UserInfo;
import org.elastos.carrier.session.*;
import org.elastos.carrier.exceptions.CarrierException;
import org.json.JSONException;
import org.json.JSONObject;

public class PluginStreamHandler extends AbstractStreamHandler {
	private static String TAG = "PluginStreamHandler";

	public Stream mStream;
	public int mCode;
	public CallbackContext mCallbackContext = null;

	public PluginStreamHandler(CallbackContext callbackContext) {
		this.mCallbackContext = callbackContext;
	}

	public static PluginStreamHandler createInstance(Session session, int type, int options, CallbackContext callbackContext) throws CarrierException {
		PluginStreamHandler handler = new PluginStreamHandler(callbackContext);
		if (handler != null) {
			handler.mStream = session.addStream(StreamType.valueOf(type), options, handler);
			if (handler.mStream != null) {
				handler.mCode = System.identityHashCode(handler.mStream);
			}
		}
		return handler;
	}

	public JSONObject getAddressInfoJson(AddressInfo info) throws JSONException {
		JSONObject r = new JSONObject();
		r.put("type", info.getCandidateType().value());
		r.put("address", info.getAddress().getAddress().toString());
		r.put("port", info.getAddress().getPort());
		InetSocketAddress relateAddress = info.getRelatedAddress();
		if (relateAddress != null) {
			r.put("relatedAddress", relateAddress.getAddress().toString());
			r.put("relatedPort", relateAddress.getPort());
		}
		return r;
	}

	public JSONObject getTransportInfoJson() throws JSONException, CarrierException{
		TransportInfo info = mStream.getTransportInfo();
		JSONObject r = new JSONObject();
		r.put("topology", info.getTopology().value());
		r.put("localAddr", getAddressInfoJson(info.getLocalAddressInfo()));
		r.put("remoteAddr", getAddressInfoJson(info.getRemoteAddressInfo()));
		return r;
	}

	private void sendEvent(JSONObject info) throws JSONException {
		info.put("objId", mCode);
		if (mCallbackContext != null) {
			PluginResult result = new PluginResult(PluginResult.Status.OK, info);
			result.setKeepCallback(true);
			mCallbackContext.sendPluginResult(result);
		}
	}

	@Override
	public void onStateChanged(Stream stream, StreamState state) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onStateChanged");
			r.put("state", state.value());
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStreamData(Stream stream, byte[] data) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onStreamData");
			r.put("data", Base64.encodeToString(data, Base64.DEFAULT));
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onChannelOpen(Stream stream, int channel, String cookie) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onChannelOpen");
			r.put("channel", channel);
			r.put("cookie", cookie);
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void onChannelOpened(Stream stream, int channel) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onChannelOpened");
			r.put("channel", channel);
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onChannelClose(Stream stream, int channel,  CloseReason reason) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onChannelClose");
			r.put("channel", channel);
			r.put("reason", reason.value());
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onChannelData(Stream stream, int channel, byte[] data) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onChannelData");
			r.put("channel", channel);
			r.put("data", Base64.encodeToString(data, Base64.DEFAULT));
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void onChannelPending(Stream stream, int channel) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onChannelPending");
			r.put("channel", channel);
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onChannelResume(Stream stream, int channel) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onChannelResume");
			r.put("channel", channel);
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
