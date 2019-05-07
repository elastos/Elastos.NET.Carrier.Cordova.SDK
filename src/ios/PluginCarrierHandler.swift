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

import Foundation
import ElastosCarrierSDK

typealias UserInfo = CarrierUserInfo
typealias FriendInfo = CarrierFriendInfo

//@objc(PluginCarrierHandler)
class PluginCarrierHandler: CarrierDelegate {

    @objc(carrier)
    var mCarrier: Carrier!;
    var mCode:Int = 0
    var mSessionManager: CarrierSessionManager!;
    var callbackId:String?
    var commandDelegate:CDVCommandDelegate?

    init(_ callbackId: String, _ commandDelegate:CDVCommandDelegate) {
        self.callbackId = callbackId;
        self.commandDelegate = commandDelegate;
    }

    func createCarrier(_ dir: String, _ configString: String) throws -> Carrier {
        let carrierDirectory: String = NSSearchPathForDirectoriesInDomains(.libraryDirectory, .userDomainMask, true)[0] + "/" + dir
        if !FileManager.default.fileExists(atPath: carrierDirectory) {
            var url = URL(fileURLWithPath: carrierDirectory)
            try FileManager.default.createDirectory(at: url, withIntermediateDirectories: true, attributes: nil)

            var resourceValues = URLResourceValues()
            resourceValues.isExcludedFromBackup = true
            try url.setResourceValues(resourceValues)
        }


        let options = CarrierOptions()
        options.bootstrapNodes = [BootstrapNode]()

        let jsonData = configString.data(using: .utf8)
        let decodedJsonDict:[String:Any] = (try JSONSerialization.jsonObject(with: jsonData!, options: []) as? [String: Any])!
        print("decodedJsonDict=\(decodedJsonDict)")

        options.udpEnabled = decodedJsonDict["udpEnabled"] as! Bool
        if decodedJsonDict["bootstraps"] is Array<AnyObject> {
            for dict in decodedJsonDict["bootstraps"] as! Array<AnyObject>{
                let node = dict as! [String:Any]
                let bootstrapNode = BootstrapNode()
                bootstrapNode.ipv4 = (node["ipv4"] as AnyObject? as? String) ?? ""
                bootstrapNode.port = (node["port"] as AnyObject? as? String) ?? ""
                bootstrapNode.publicKey = (node["publicKey"] as AnyObject? as? String) ?? ""

                options.bootstrapNodes?.append(bootstrapNode)
            }

        }

        options.persistentLocation = carrierDirectory

        try Carrier.initializeSharedInstance(options: options, delegate: self)
        print("carrier instance created")

        mCarrier = Carrier.sharedInstance()

        try CarrierSessionManager.initializeSharedInstance(carrier: mCarrier, sessionRequestHandler: didReceiveSessionRequest);
        mSessionManager = CarrierSessionManager.sharedInstance();

        return mCarrier;
    }

    static func createInstance(_ dir: String, _ configString: String, _ callbackId:String, _ commandDelegate:CDVCommandDelegate) throws -> PluginCarrierHandler {
        let handler: PluginCarrierHandler = PluginCarrierHandler(callbackId, commandDelegate);
        let _:Carrier = try handler.createCarrier(dir, configString);
        return handler;
    }

    func getUserInfoDict(_ info: UserInfo) -> NSMutableDictionary {
        let ret: NSMutableDictionary = [
            "description": info.briefDescription ?? "",
            "email" : info.email ?? "",
            "gender" : info.gender ?? "",
            "name" : info.name ?? "",
            "phone": info.phone ?? "",
            "region" : info.region ?? "",
            "userId" : info.userId ?? "",
            "hasAvatar" : info.hasAvatar,
            ]
        return ret;
    }

    func getFriendInfoDict(_ info: FriendInfo)-> NSMutableDictionary {
        let ret: NSMutableDictionary = [
            "status" : info.status.rawValue,
            "label" : info.label ?? "",
            "presence" : info.presence.rawValue,
            "userInfo" : getUserInfoDict(info),
            ]
        return ret;
    }

    func getFriendsInfoDict(_ friends: [CarrierFriendInfo]) -> NSMutableDictionary {
        let friendDicts: NSMutableDictionary = [:]
        // var friendDicts = [NSMutableDictionary]();
        for friend in friends {
            friendDicts[friend.userId as Any] = getFriendInfoDict(friend);
            // friendDicts.append(getFriendInfoDict(friend));
        }
        return friendDicts;
    }

    private func sendEvent(_ ret: NSMutableDictionary) {
        ret["id"] = mCode
        let result = CDVPluginResult(status: CDVCommandStatus_OK,
                                     messageAs: ret as? [AnyHashable : Any]);
        result?.setKeepCallbackAs(true);
        self.commandDelegate?.send(result, callbackId:self.callbackId);

    }

    func willBecomeIdle(_ carrier:Carrier) {
        let ret: NSMutableDictionary = [
            "name": "onIdle",
            ]
        sendEvent(ret);
    }

    func connectionStatusDidChange(_ carrier:Carrier, _ newStatus: CarrierConnectionStatus) {
        let ret: NSMutableDictionary = [
            "name": "onConnection",
            "status": newStatus.rawValue,
            ]
        sendEvent(ret);
    }

    func didBecomeReady(_ carrier:Carrier) {
        let ret: NSMutableDictionary = [
            "name": "onReady",
            ]
        sendEvent(ret);
    }

    func selfUserInfoDidChange(_ carrier: Carrier,
                               _ newInfo: CarrierUserInfo) {
        let ret: NSMutableDictionary = [
            "name": "onSelfInfoChanged",
            "userInfo": getUserInfoDict(newInfo),
            ]
        sendEvent(ret);
    }

    func didReceiveFriendsList(_ carrier: Carrier,
                               _ friends: [CarrierFriendInfo]) {
        let ret: NSMutableDictionary = [
            "name": "onFriends",
            "friends": getFriendsInfoDict(friends),
            ]
        sendEvent(ret);
    }


    func friendConnectionDidChange(_ carrier: Carrier,
                                   _ friendId: String,
                                   _ newStatus: CarrierConnectionStatus) {
        let ret: NSMutableDictionary = [
            "name": "onFriendConnection",
            "friendId": friendId,
            "status": newStatus.rawValue,
            ]
        sendEvent(ret);
    }


    func friendInfoDidChange(_ carrier: Carrier,
                             _ friendId: String,
                             _ newInfo: CarrierFriendInfo) {
        let ret: NSMutableDictionary = [
            "name": "onFriendInfoChanged",
            "friendId": friendId,
            "friendInfo": getFriendInfoDict(newInfo),
            ]
        sendEvent(ret);
    }


    func friendPresenceDidChange(_ carrier: Carrier,
                                 _ friendId: String,
                                 _ newPresence: CarrierPresenceStatus) {
        let ret: NSMutableDictionary = [
            "name": "onFriendPresence",
            "friendId": friendId,
            "presence": newPresence.rawValue,
            ]
        sendEvent(ret);
    }

    func newFriendAdded(_ carrier: Carrier,
                        _ newFriend: CarrierFriendInfo) {
        let ret: NSMutableDictionary = [
            "name": "onFriendAdded",
            "friendInfo": getFriendInfoDict(newFriend),
            ]
        sendEvent(ret);
    }


    func friendRemoved(_ carrier: Carrier,
                       _ friendId: String) {

        let ret: NSMutableDictionary = [
            "name": "onFriendRemoved",
            "friendId": friendId,
            ]
        sendEvent(ret);
    }


    func didReceiveFriendRequest(_ carrier: Carrier,
                                 _ userId: String,
                                 _ userInfo: CarrierUserInfo,
                                 _ hello: String) {
        let ret: NSMutableDictionary = [
            "name": "onFriendRequest",
            "userId": userId,
            "userInfo": getUserInfoDict(userInfo),
            "hello": hello,
            ]
        sendEvent(ret);
    }


    func didReceiveFriendInviteRequest(_ carrier: Carrier,
                                       _ from: String,
                                       _ data: String) {
        let ret: NSMutableDictionary = [
            "name": "onFriendInviteRequest",
            "from": from,
            "data": data,
            ]
        sendEvent(ret);
    }

    func didReceiveFriendMessage(_ carrier: Carrier,
                                 _ from: String,
                                 _ data: Data) {
        let message = String(data: data, encoding: .utf8)!;
        let ret: NSMutableDictionary = [
            "name": "onFriendMessage",
            "from": from,
            "message": message,
            ]
        sendEvent(ret);
    }

    func didReceiveSessionRequest(carrier: Carrier, from: String, data: String) {
        let ret: NSMutableDictionary = [
            "name": "onSessionRequest",
            "from": from,
            "sdp": data,
        ]
        sendEvent(ret);
    }
}
