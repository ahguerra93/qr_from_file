import 'dart:async';
import 'package:flutter/services.dart';

class QrFromFile {
  static const MethodChannel _channel =
      const MethodChannel('qr_from_file');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> scanImageFromAssets(String path) async {
    
    final String result = await _channel.invokeMethod('scanImageFromAssets', {"path": path});
    return result;
  }

  static Future<String> scanImageFromGallery(String path) async {
    
    final String result = await _channel.invokeMethod('scanImageFromFile', {"path": path});
    return result;
  }
}
