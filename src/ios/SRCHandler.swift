import Foundation
import ElastosCarrier

class SRCHandler: SessionRequestCompleteDelegate {
//    private static String TAG = "FIRHandler";

    var code:Int = 0
    var callbackId:String?
    var commandDelegate:CDVCommandDelegate?

    init(_ code: Int, _ callbackId: String, _ commandDelegate:CDVCommandDelegate) {
        self.code = code
        self.callbackId = callbackId;
        self.commandDelegate = commandDelegate;
    }

    private func sendEvent(_ ret: NSMutableDictionary) {
        ret["id"] = code
        let result = CDVPluginResult(status: CDVCommandStatus_OK,
                                     messageAs: ret as! [AnyHashable : Any]);
        result?.setKeepCallbackAs(true);
        self.commandDelegate?.send(result, callbackId:self.callbackId);

    }

    func onCompletion(_ session: CarrierSession,
                      _ status: Int, _ reason: String?, _ sdp: String?) {
        let ret: NSMutableDictionary = [
            "name": "onCompletion",
            "status": status,
            "reason": reason ?? "",
            "sdp": sdp ?? "",
            ]
        sendEvent(ret);
    }
}

