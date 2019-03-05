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

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.elastos.carrier.session.ManagerHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

import org.elastos.carrier.*;
import org.elastos.carrier.exceptions.CarrierException;
import org.elastos.carrier.session.Manager;

public class PluginCarrierHandler extends AbstractCarrierHandler implements ManagerHandler {
	private static String TAG = "PluginCarrierHandler";

	public Carrier mCarrier;
	public int mCode;
	public Manager mSessionManager;
	public CallbackContext mCallbackContext = null;


	public static int AGENT_READY = 0;

	private PluginCarrierHandler(CallbackContext callbackContext) {
		mCallbackContext = callbackContext;
	}

	private Carrier createCarrier(String dir, String configString) throws CarrierException {

		File carrierDir = new File(dir);
		if (!carrierDir.exists()) {
			carrierDir.mkdirs();
		}

		boolean udpEnabled = false;
		List<Carrier.Options.BootstrapNode> bootstraps = new ArrayList<>();

		try {

//			InputStream configStream = mContext.getResources().openRawResource(R.raw.elastos_carrier_config);
//			String configString = IOUtils.toString(configStream, "UTF-8");
			JSONObject jsonObject = new JSONObject(configString);

			udpEnabled = jsonObject.getBoolean("udpEnabled");

			JSONArray jsonBootstraps = jsonObject.getJSONArray("bootstraps");
			for (int i = 0, m = jsonBootstraps.length(); i < m; i++) {
				JSONObject jsonBootstrap = jsonBootstraps.getJSONObject(i);
				Carrier.Options.BootstrapNode bootstrap = new Carrier.Options.BootstrapNode();
				String ipv4 = jsonBootstrap.optString("ipv4");
				if (ipv4 != null) {
					bootstrap.setIpv4(ipv4);
				}
				String ipv6 = jsonBootstrap.optString("ipv6");
				if (ipv4 != null) {
					bootstrap.setIpv6(ipv6);
				}
				bootstrap.setPort(jsonBootstrap.getString("port"));
				bootstrap.setPublicKey(jsonBootstrap.getString("publicKey"));
				bootstraps.add(bootstrap);
			}
		} catch (Exception e) {
			// report exception
		}

		Carrier.Options options = new Carrier.Options();
		options.setPersistentLocation(dir).
				setUdpEnabled(udpEnabled).
				setBootstrapNodes(bootstraps);

		Carrier.initializeInstance(options, this);
		mCarrier = Carrier.getInstance();
		Log.i(TAG, "Agent elastos carrier instance created successfully");
		if (mCarrier == null) {
			return null;
		}

		Manager.initializeInstance(mCarrier,  this);
		mSessionManager = Manager.getInstance();
		Log.i(TAG, "Agent session manager created successfully");

//		mCarrier.start(50);
		mCode = System.identityHashCode(mCarrier);
//		mCarrierMap.put(dir, carrier);

		return mCarrier;
	}

	public static PluginCarrierHandler createInstance(String dir, String configString, CallbackContext callbackContext) throws CarrierException {
		PluginCarrierHandler handler = new PluginCarrierHandler(callbackContext);
		if (handler != null) {
			Carrier carrier = handler.createCarrier(dir, configString);
			if (carrier == null) {
				handler = null;
			}
		}
		return handler;
	}

	public JSONObject getUserInfoJson(UserInfo info) throws JSONException {
		JSONObject r = new JSONObject();
		r.put("description", info.getDescription());
		r.put("email", info.getEmail());
		r.put("gender", info.getGender());
		r.put("name", info.getName());
		r.put("phone", info.getPhone());
		r.put("region", info.getRegion());
		r.put("userId", info.getUserId());
		r.put("hasAvatar", info.hasAvatar());
		return r;
	}

	public JSONObject getFriendInfoJson(FriendInfo info) throws JSONException {
		JSONObject r = new JSONObject();
		r.put("status", info.getConnectionStatus().value());
		r.put("label", info.getLabel());
		r.put("presence", info.getPresence().value());
		r.put("userInfo", getUserInfoJson(info));
		return r;
	}

	public JSONObject getFriendsInfoJson(List<FriendInfo> friends) throws JSONException {
		// List<JSONObject> jsons = new ArrayList<JSONObject>();
		JSONObject ret = new JSONObject();
		for (FriendInfo friend : friends) {
			// jsons.add(getFriendInfoJson(friend));
			ret.put(friend.getUserId(), getFriendInfoJson(friend));
			Log.d(TAG, friend.toString());
		}
		return ret;
	}

//	public JSONObject getCarrierInfoJson() throws JSONException, CarrierException {
//		UserInfo selfInfo = mCarrier.getSelfInfo();
//		List<FriendInfo> friends = mCarrier.getFriends();
//
//		JSONObject r = new JSONObject();
//		r.put("nodeId", mCarrier.getNodeId());
//		r.put("address", mCarrier.getAddress());
//		r.put("nospam", mCarrier.getNospam());
//		r.put("presence", mCarrier.getPresence().value());
//		r.put("selfInfo", getUserInfoJson(selfInfo));
//		r.put("friends", getFriendsInfoJson(friends));
//		return r;
//	}

//	public void logout() {
//		String elaCarrierPath = mContext.getFilesDir().getAbsolutePath() + "/elaCarrier";
//		File elaCarrierDir = new File(elaCarrierPath);
//		if (elaCarrierDir.exists()) {
//			File[] files = elaCarrierDir.listFiles();
//			for (File file : files) {
//				file.delete();
//			}
//		}
//
//		this.kill();
//	}

	public void kill() {
		if (mCarrier != null) {
			mSessionManager.cleanup();
			mCarrier.kill();
		}
	}

	public Manager getSessionManager() {
		return mSessionManager;
	}

	public UserInfo getInfo() throws CarrierException {
		return mCarrier.getSelfInfo();
	}

	private void sendEvent(JSONObject info) throws JSONException {
		info.put("id", mCode);
		if (mCallbackContext != null) {
			PluginResult result = new PluginResult(PluginResult.Status.OK, info);
			result.setKeepCallback(true);
			mCallbackContext.sendPluginResult(result);
		}
	}

	@Override
	public void onIdle(Carrier carrier) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onIdle");
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnection(Carrier carrier, ConnectionStatus status) {
		Log.i(TAG, "Agent connection status changed to " + status);
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onConnection");
			r.put("status", status.value());
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReady(Carrier carrier) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onReady");
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSelfInfoChanged(Carrier carrier, UserInfo userInfo) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onSelfInfoChanged");
			r.put("userInfo", getUserInfoJson(userInfo));
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFriends(Carrier carrier, List<FriendInfo> friends) {
		Log.i(TAG, "Client portforwarding agent received friend list: " + friends);

		JSONObject r = new JSONObject();
		try {
			r.put("name", "onFriends");
			r.put("friends", getFriendsInfoJson(friends));
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFriendConnection(Carrier carrier, String friendId, ConnectionStatus status) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onFriendConnection");
			r.put("friendId", friendId);
			r.put("status", status.value());
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFriendInfoChanged(Carrier carrier, String friendId, FriendInfo friendInfo) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onFriendInfoChanged");
			r.put("friendId", friendId);
			r.put("friendInfo", getFriendInfoJson(friendInfo));
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFriendPresence(Carrier carrier, String friendId, PresenceStatus presence) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onFriendPresence");
			r.put("friendId", friendId);
			r.put("presence", presence);
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFriendAdded(Carrier carrier, FriendInfo friendInfo) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onFriendAdded");
			r.put("friendInfo", getFriendInfoJson(friendInfo));
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFriendRemoved(Carrier carrier, String friendId) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onFriendRemoved");
			r.put("friendId", friendId);
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFriendRequest(Carrier carrier, String userId, UserInfo info, String hello) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onFriendRequest");
			r.put("userId", userId);
			r.put("userInfo", getUserInfoJson(info));
			r.put("hello", hello);
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFriendInviteRequest(Carrier carrier, String from, String data) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onFriendInviteRequest");
			r.put("from", from);
			r.put("data", data);
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFriendMessage(Carrier carrier, String from, byte[] message) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onFriendMessage");
			r.put("from", from);
			r.put("message", message);
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSessionRequest(Carrier carrier, String from, String sdp) {
		JSONObject r = new JSONObject();
		try {
			r.put("name", "onSessionRequest");
			r.put("from", from);
			r.put("sdp", sdp);
			sendEvent(r);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}

