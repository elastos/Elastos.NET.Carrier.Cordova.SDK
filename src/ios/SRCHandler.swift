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
import ElastosCarrier

class SRCHandler {
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
                                     messageAs: ret as? [AnyHashable : Any]);
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

