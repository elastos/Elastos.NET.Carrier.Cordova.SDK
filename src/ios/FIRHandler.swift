
import Foundation
import ElastosCarrier

class FIRHandler: FriendInviteResponseDelegate {
//    private static String TAG = "FIRHandler";

    var handlerId:Int = 0
    var callbackId:String?
    var commandDelegate:CDVCommandDelegate?

    init(_ handlerId: Int, _ callbackId: String, _ commandDelegate:CDVCommandDelegate) {
        self.handlerId = handlerId
        self.callbackId = callbackId;
        self.commandDelegate = commandDelegate;
    }

    private func sendEvent(_ ret: NSMutableDictionary) {
        ret["id"] = handlerId
        let result = CDVPluginResult(status: CDVCommandStatus_OK,
                                     messageAs: ret as! [AnyHashable : Any]);
        result?.setKeepCallbackAs(true);
        self.commandDelegate?.send(result, callbackId:self.callbackId);

    }

    func onReceived(_ carrier: Carrier, _ from: String, _ status: Int, _ reason: String?, _ data: String?) {
        let ret: NSMutableDictionary = [
            "name": "onReceived",
            "from": from,
            "status": status,
            "reason": reason ?? "",
            "data": data ?? "",
            ]
        sendEvent(ret);
    }
}

