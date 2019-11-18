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
 
//@objc(PluginGroupHandler)
class PluginGroupHandler: CarrierGroupDelegate {
    var mGroup: Group!;
    var groupId: Int?;
    var callbackId: String?
    var commandDelegate: CDVCommandDelegate?

    init(_ callbackId: String, _ commandDelegate:CDVCommandDelegate) {
        self.callbackId = callbackId;
        self.commandDelegate = commandDelegate;
    }
    
    func setGroup(_ group: Group){
        self.mGroup = group;
    }
    
    func setGroupId(_ groupId: Int){
        self.groupId = groupId;
    }
    
    private func sendEvent(_ ret: NSMutableDictionary) {
        ret["groupId"] = groupId;
        let result = CDVPluginResult(status: CDVCommandStatus_OK,messageAs: ret as? [AnyHashable : Any]);
        result?.setKeepCallbackAs(true);
        self.commandDelegate?.send(result, callbackId:self.callbackId);
    }
    
    func getGroupPeersInfoDict(_ peers: [CarrierGroupPeer]) -> NSMutableDictionary {
        let peerDicts: NSMutableDictionary = [:]
        // var friendDicts = [NSMutableDictionary]();
        for peer in peers {
            peerDicts[peer.userId as Any] = getGroupPeerInfoDict(peer);
        }
        return peerDicts;
    }
    
    func getGroupPeerInfoDict(_ peer: CarrierGroupPeer)-> NSMutableDictionary {
        let ret: NSMutableDictionary = [
            "peerName" : peer.name ?? "",
            "peerUserId" : peer.userId ?? "",
            ]
        return ret;
    }
    
    func groupDidConnect(_ group: CarrierGroup) {
        let ret: NSMutableDictionary = [
            "name" : "onGroupConnected",
            ]
        sendEvent(ret);
    }
    
    func didReceiveGroupMessage(_ group: CarrierGroup, _ from: String, _ data: Data) {
        let message = String(data: data, encoding: .utf8)!;

        let ret: NSMutableDictionary = [
            "name" : "onGroupMessage",
            "from" : from,
            "message" : message,
            ]
        sendEvent(ret);
    }
    
    func groupTitleDidChange(_ group: CarrierGroup, _ from: String, _ newTitle: String) {
        let ret: NSMutableDictionary = [
            "name" : "onGroupTitle",
            "from" : from,
            "title" : newTitle,
            ]
        sendEvent(ret);
    }
    
    func groupPeerNameDidChange(_ group: CarrierGroup, _ from: String, _ newName: String) {
        let ret: NSMutableDictionary = [
            "name" : "onPeerName",
            "peerId" : from,
            "peerName" : newName,
            ]
        
        sendEvent(ret);
    }
    
    func groupPeerListDidChange(_ group: CarrierGroup) {
        let ret: NSMutableDictionary = [
            "name" : "onPeerListChanged",
            ]
        sendEvent(ret);
    }
}
