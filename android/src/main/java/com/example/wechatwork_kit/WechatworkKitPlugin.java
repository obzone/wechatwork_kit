package com.example.wechatwork_kit;

import android.content.Context;

import androidx.annotation.NonNull;

import com.tencent.wework.api.IWWAPI;
import com.tencent.wework.api.IWWAPIEventHandler;
import com.tencent.wework.api.WWAPIFactory;
import com.tencent.wework.api.model.BaseMessage;
import com.tencent.wework.api.model.WWAuthMessage;
import com.tencent.wework.api.model.WWMediaImage;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.util.PathUtils;

/** WechatworkKitPlugin */
public class WechatworkKitPlugin implements FlutterPlugin, MethodCallHandler {
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    final MethodChannel channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "wechatwork_kit");
    WechatworkKitPlugin plugin = new WechatworkKitPlugin(registrar, channel);
    channel.setMethodCallHandler(plugin);
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "wechatwork_kit");
    WechatworkKitPlugin plugin = new WechatworkKitPlugin(registrar, channel);
    channel.setMethodCallHandler(plugin);
    plugin.context = registrar.context();
  }

  private String APPID = "";
  private String AGENTID = "";
  private String SCHEMA = "";
  private Context context;

  private IWWAPI iwwapi = null;

  private final Registrar registrar;
  private final MethodChannel channel;

  private WechatworkKitPlugin(Registrar registrar, MethodChannel channel) {
    this.registrar = registrar;
    this.channel = channel;
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull final Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("registerApp")) {
      this.SCHEMA = call.argument("schema");
      this.APPID = call.argument("corpId");
      this.AGENTID = call.argument("agentId");
      iwwapi = WWAPIFactory.createWWAPI(this.registrar.context().getApplicationContext());
      iwwapi.registerApp(this.SCHEMA);
      result.success(null);
    } else if (call.method.equals("SSO")) {
      final WWAuthMessage.Req req = new WWAuthMessage.Req();
      req.sch = SCHEMA;
      req.appId = APPID;
      req.agentId = AGENTID;
      req.state = "dd";
      iwwapi.sendMessage(req, new IWWAPIEventHandler() {
        @Override
        public void handleResp(BaseMessage resp) {
          if (resp instanceof WWAuthMessage.Resp) {
            WWAuthMessage.Resp rsp = (WWAuthMessage.Resp) resp;
            if (rsp.errCode == WWAuthMessage.ERR_CANCEL) {
              result.error(String.valueOf(rsp.errCode), "登录取消", null);
            }else if (rsp.errCode == WWAuthMessage.ERR_FAIL) {
              result.error(String.valueOf(rsp.errCode), "登录失败", null);
            } else if (rsp.errCode == WWAuthMessage.ERR_OK) {
              result.success(rsp.code);
            }
          }
        }
      });
    }else if (call.method.equals("isWWAppInstalled")){
      result.success(iwwapi.isWWAppInstalled());
    }else if (call.method.equals("imageShare")){

      WWMediaImage img = new WWMediaImage();
      img.fileName = call.argument("fileName");;
      img.filePath = PathUtils.getDataDirectory(context) + '/' + call.argument("fileName");
//      img.appPkg = getPackageName();
//      img.appName = getString(stringId);
      img.appId = APPID;
      img.agentId = AGENTID;
      iwwapi.sendMessage(img);
      result.success(true);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
  }
}
