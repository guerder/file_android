import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'file_android_platform_interface.dart';

/// An implementation of [FileAndroidPlatform] that uses method channels.
class MethodChannelFileAndroid extends FileAndroidPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('file_android');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<String?> salvarArquivoBase64({
    required String nomeArquivo,
    required String nomeDiretorioApp,
    required String mimetypeArquivo,
    required String base64,
  }) async {
    if (nomeArquivo.toLowerCase().endsWith(".jfif")) {
      nomeArquivo.replaceAll(".jfif", ".jpeg");
      mimetypeArquivo.replaceAll("jfif", "jpeg");
    }
    final b64 =
        await methodChannel.invokeMethod<String>('salvarArquivoBase64', [
      nomeArquivo,
      nomeDiretorioApp,
      mimetypeArquivo,
      base64,
    ]);
    return b64;
  }
}
