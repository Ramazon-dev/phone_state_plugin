import 'dart:async';

import 'package:flutter/services.dart';
import 'package:phone_state/src/utils/constants.dart';
import 'package:phone_state/src/utils/phone_state_status.dart';

class PhoneState {
  PhoneStateStatus status;

  String? number;

  PhoneState._({required this.status, this.number});

  factory PhoneState.nothing() =>
      PhoneState._(status: PhoneStateStatus.NOTHING);

  factory PhoneState.started() =>
      PhoneState._(status: PhoneStateStatus.CALL_STARTED);

  static const EventChannel _eventChannel =
      EventChannel(Constants.eventChannel);

  static const MethodChannel _methodChannel =
      MethodChannel(Constants.methodChannel);

  Stream<PhoneState>? _onPhoneStateChanged;

  Future<PhoneState> getStatus() async {
    final item = await _methodChannel.invokeMethod(Constants.getStatusMethod);
    final phone = PhoneState._(
        status: PhoneStateStatus.values
            .firstWhere((element) => element.name == item as String));
    return phone;
  }

  Stream<PhoneState> get stream {
    _onPhoneStateChanged ??=
        _eventChannel.receiveBroadcastStream().distinct().map((dynamic event) {
      return PhoneState._(
        status: PhoneStateStatus.values.firstWhere((element) {
          return element.name == event['status'] as String;
        }),
        number: event['phoneNumber'],
      );
    });
    return _onPhoneStateChanged!;
  }

  Future<void> openAppSettings() async {
    await _methodChannel.invokeMethod(Constants.openAppSettingsMethod);
  }
}
