#import "WechatworkKitPlugin.h"
#import "WWKApi.h"

@interface WechatworkKitPlugin () <WWKApiDelegate>

@property (nonatomic, strong) NSMutableDictionary *flutterResultDictonary;

@end

@implementation WechatworkKitPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"wechatwork_kit"
            binaryMessenger:[registrar messenger]];
  WechatworkKitPlugin* instance = [[WechatworkKitPlugin alloc] init];
    [registrar addApplicationDelegate:instance];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else if ([@"registerApp" isEqualToString: call.method]) {
      NSString * schema = call.arguments[@"schema"];
      NSString * corpId = call.arguments[@"corpId"];
      NSString * agentId = call.arguments[@"agentId"];
      [WWKApi registerApp:schema corpId:corpId agentId:agentId];
      result(nil);
  } else if ([@"SSO" isEqualToString:call.method]) {
      NSString *resultKey = [NSString stringWithFormat:@"%f", [[NSDate new] timeIntervalSince1970]];
      [self.flutterResultDictonary setValue:result forKey:resultKey];
      WWKSSOReq *req = [WWKSSOReq new];
      req.state = resultKey;
      [WWKApi sendReq:req];
  } else {
    result(FlutterMethodNotImplemented);
  }
}

- (NSMutableDictionary *)flutterResultDictonary {
    if (self->_flutterResultDictonary) {
        self->_flutterResultDictonary = [NSMutableDictionary new];
    }
    return self->_flutterResultDictonary;
}

# pragma mark - AppDelegate

- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation {
    return [self handleOpenURL:url sourceApplication:sourceApplication];
}
- (BOOL)handleOpenURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication {
    return [WWKApi handleOpenURL:url delegate:self];
}

# pragma mark - WWKApiDelegate

/*! @brief 收到一个来自企业微信的请求，第三方应用程序处理完后调用sendResp向企业微信发送结果
 *
 * 收到一个来自企业微信的请求，异步处理完成后必须调用sendResp发送处理结果给企业微信。
 * 目前并未使用。
 * @param req 具体请求内容，是自动释放的
 */
- (void)onReq:(WWKBaseReq *)req {
    
}

/*! @brief 发送一个sendReq后，收到企业微信的回应
 *
 * 收到一个来自企业微信的处理结果。调用一次sendReq后会收到onResp。
 * 可能收到的处理结果有WWKSendMessageResp等。
 * @param resp 具体的回应内容，是自动释放的
 */
- (void)onResp:(WWKBaseResp *)resp {
    if ([resp isKindOfClass:[WWKSSOResp class]]) {
        FlutterResult result = [self.flutterResultDictonary objectForKey:((WWKSSOResp *)resp).state];
        if (!result) return;
        if (resp.errStr != nil) {
            result([FlutterError errorWithCode:[NSString stringWithFormat:@"%d", resp.errCode] message:resp.errStr details:resp]);
        } else {
            ///  TODO  取出code
        }
        [self.flutterResultDictonary removeObjectForKey:((WWKSSOResp *)resp).state];
    }
}

@end