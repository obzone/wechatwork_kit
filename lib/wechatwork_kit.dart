import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class WechatworkKit {
  static const MethodChannel _channel = const MethodChannel('wechatwork_kit');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future registerApp(
      {@required String schema,
      @required String corpId,
      @required String agentId}) {
    return _channel.invokeMethod('registerApp', <String, dynamic>{
      'schema': schema,
      'corpId': corpId,
      'agentId': agentId
    });
  }

  static Future sso() {
    return _channel.invokeMethod('SSO');
  }

  static Future isWWAppInstalled() {
    return _channel.invokeMethod('isWWAppInstalled');
  }
}
