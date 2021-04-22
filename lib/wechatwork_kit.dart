import 'dart:async';

import 'package:flutter/services.dart';

class WechatworkKit {
  static const MethodChannel _channel = const MethodChannel('wechatwork_kit');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future registerApp(
      {required String schema,
      required String corpId,
      required String agentId}) {
    return _channel.invokeMethod('registerApp', <String, dynamic>{
      'schema': schema,
      'corpId': corpId,
      'agentId': agentId
    });
  }

  static Future sso() {
    return _channel.invokeMethod('SSO');
  }

/*! @brief 检查企业微信是否已被用户安装
 *
 * @return 企业微信已安装返回YES，未安装返回NO。
 * @note 由于iOS系统的限制，在iOS9及以上系统检测企业微信是否安装，需要将企业微信的scheme"wxwork"(云端版本)及"wxworklocal"(本地部署版本)添加到工程的Info.plist中的LSApplicationQueriesSchemes白名单里，否则此方法总是会返回NO。
 * 详情参考 https://developer.apple.com/documentation/uikit/uiapplication/1622952-canopenurl
 */
  static Future isWWAppInstalled() {
    return _channel.invokeMethod('isWWAppInstalled');
  }

  static Future imageShare(String filename) {
    return _channel.invokeMethod('imageShare', <String, dynamic>{
      'filename': filename,
    });
  }
}
