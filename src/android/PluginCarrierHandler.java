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
  import org.elastos.carrier.filetransfer.FileTransferInfo;
  import org.elastos.carrier.session.ManagerHandler;
  import org.json.JSONException;
  import org.json.JSONObject;

  import java.nio.charset.StandardCharsets;
  import java.util.List;
  import java.util.ArrayList;
  import java.io.File;

  import org.elastos.carrier.*;
  import org.elastos.carrier.exceptions.CarrierException;
  import org.elastos.carrier.session.Manager;

  public class PluginCarrierHandler extends AbstractCarrierHandler implements ManagerHandler , org.elastos.carrier.filetransfer.ManagerHandler {
	  private static String TAG = "PluginCarrierHandler";

	  public Carrier mCarrier;
	  public int mCode;
	  public Manager mSessionManager;
	  public CallbackContext mCallbackContext = null;

	  private org.elastos.carrier.filetransfer.Manager mFileTransferManager;

	  public static int AGENT_READY = 0;

	  private PluginCarrierHandler(CallbackContext callbackContext) {
		  mCallbackContext = callbackContext;
	  }

	  private Carrier createCarrier(String dir, String configString, CarrierPlugin plugin) throws JSONException, CarrierException {
		  File carrierDir = new File(dir);
		  if (!carrierDir.exists()) {
			  carrierDir.mkdirs();
		  }

		  boolean udpEnabled = false;
		  List<Carrier.Options.BootstrapNode> bootstraps = new ArrayList<>();
		  ArrayList<BootstrapsGetter.BootstrapNode> list = BootstrapsGetter.getBootstrapNodes(plugin);
		  for (BootstrapsGetter.BootstrapNode node: list) {
			  Carrier.Options.BootstrapNode bootstrapNode = new Carrier.Options.BootstrapNode();
			  bootstrapNode.setIpv4(node.ipv4);
			  bootstrapNode.setPort(String.valueOf(node.port));
			  bootstrapNode.setPublicKey(node.publicKey);
			  bootstraps.add(bootstrapNode);
		  }

		  List<Carrier.Options.HiveBootstrapNode> ipfsNodes = new ArrayList<>();
		  ArrayList<BootstrapsGetter.IpfsNode> ipfsList = BootstrapsGetter.getIpfsNodes(plugin);
		  for (BootstrapsGetter.IpfsNode node: ipfsList) {
			  Carrier.Options.HiveBootstrapNode ipfsNode = new Carrier.Options.HiveBootstrapNode();
			  String part[] = node.addr.split(":");
			  ipfsNode.setIpv4(part[0]);
			  ipfsNode.setPort(part[1]);
			  ipfsNodes.add(ipfsNode);
		  }

		  JSONObject jsonObject = new JSONObject(configString);
		  udpEnabled = jsonObject.getBoolean("udpEnabled");

		  Carrier.Options options = new Carrier.Options();
		  options.setPersistentLocation(dir + '/' + jsonObject.getString("persistentLocation"))
				  .setUdpEnabled(udpEnabled)
				  .setBootstrapNodes(bootstraps)
				  .setHiveBootstrapNodes(ipfsNodes);

		  mCarrier = Carrier.createInstance(options, this);
		  Log.i(TAG, "Agent elastos carrier instance created successfully");
		  if (mCarrier == null) {
			  return null;
		  }

		  mSessionManager = Manager.createInstance(mCarrier,  this);
		  Log.i(TAG, "Agent session manager created successfully");

		  mFileTransferManager = org.elastos.carrier.filetransfer.Manager.createInstance(mCarrier,this);
		  Log.i(TAG, "Agent file transfer manager created successfully");

		  mCode = System.identityHashCode(mCarrier);

		  return mCarrier;
	  }

	  public static PluginCarrierHandler createInstance(String dir, String configString,
														CallbackContext callbackContext,
														CarrierPlugin plugin) throws JSONException, CarrierException {
		  PluginCarrierHandler handler = new PluginCarrierHandler(callbackContext);
		  if (handler != null) {
			  Carrier carrier = handler.createCarrier(dir, configString, plugin);
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
			  mFileTransferManager.cleanup();
			  mCarrier.kill();
		  }
	  }

	  public Manager getSessionManager() {
		  return mSessionManager;
	  }

	  public org.elastos.carrier.filetransfer.Manager getFileTransferManager(){
	  	    return mFileTransferManager ;
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
	  public void onFriendMessage(Carrier carrier, String from, byte[] data, boolean isOffline) {
		  JSONObject r = new JSONObject();
		  String message = new String(data, StandardCharsets.UTF_8);
		  try {
			  r.put("name", "onFriendMessage");
			  r.put("from", from);
			  r.put("message", message);
			  r.put("isOffline", isOffline);
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
			  r.put("message", data);
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

	  @Override
	  public void onGroupInvite(Carrier carrier, String from, byte[] cookie) {
		  String cookieData = Base58.encode(cookie);
		  JSONObject r = new JSONObject();
		  try {
			  r.put("name", "onGroupInvite");
			  r.put("from", from);
			  r.put("cookieCode", cookieData);
			  sendEvent(r);
		  } catch (JSONException e) {
			  e.printStackTrace();
		  }
	  }

	  @Override
	  public void onConnectRequest(Carrier carrier, String from, FileTransferInfo info) {
		  JSONObject r = new JSONObject();
		  try {
			  r.put("name", "onConnectRequest");
			  r.put("from", from);
			  r.put("info", createFileTransferJSON(info));
			  sendEvent(r);
		  } catch (JSONException e) {
			  e.printStackTrace();
		  }
	  }

	  private JSONObject createFileTransferJSON(FileTransferInfo info){
	  	  JSONObject jsonObject = new JSONObject();
		  try {
			  jsonObject.put("fileId",info.getFileId());
			  jsonObject.put("filename",info.getFileName());
			  jsonObject.put("size",info.getSize());
		  } catch (JSONException e) {
			  e.printStackTrace();
		  }

		  return jsonObject ;
	  }
  }

