 import UIKit
import CallKit
 import Flutter


 @available(iOS 10.0, *)
 class FlutterHandler {
     private let phoneStateChannel: FlutterEventChannel
     private let phoneStateMethodChannel: FlutterMethodChannel
     
     init(binding: FlutterPluginRegistrar) {
         phoneStateChannel = FlutterEventChannel(
            name: Constants.EVENT_CHANNEL,
            binaryMessenger: binding.messenger());
         phoneStateMethodChannel = FlutterMethodChannel(name: Constants.METHOD_CHANNEL, binaryMessenger: binding.messenger())
         
         phoneStateChannel.setStreamHandler(PhoneStateHandler())
         
         phoneStateMethodChannel.setMethodCallHandler { /*[weak self]*/ (call: FlutterMethodCall, result: @escaping FlutterResult) in
             switch call.method {
             case "openAppSettings": self.openAppSettings(result: result);
             case "getStatus": self.getCurrentStatus(result: result);
             default:  result(
                FlutterError(
                    code: "UNAVAILABLE",
                    message: "Method not available.",
                    details: nil
                    )
                )
             }
         }
     }
     
     func getCurrentStatus(result: FlutterResult) {
         let isCalled = self.checkForActiveCall()
         if (isCalled) {
             result("\(PhoneStateStatus.CALL_STARTED)")
         } else {
             result("\(PhoneStateStatus.NOTHING)")
         }
         print("isCalled \(isCalled)")
     }
     
     func checkForActiveCall() -> Bool {
         for call in CXCallObserver().calls {
             if call.hasEnded == false {
                 return true
             }
         }
         return false
     }
     
     public func dispose(){
         phoneStateChannel.setStreamHandler(nil)
     }
     
     
     
     private func openAppSettings(result: @escaping FlutterResult) {
       if let url = URL(string: UIApplication.openSettingsURLString) {
         if UIApplication.shared.canOpenURL(url) {
           UIApplication.shared.open(url, options: [:], completionHandler: nil)
           result(true)
         } else {
           result(false)
         }
       } else {
         result(false)
       }
     }
     
 }
