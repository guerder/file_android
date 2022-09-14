import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'file_android_method_channel.dart';

abstract class FileAndroidPlatform extends PlatformInterface {
  /// Constructs a FileAndroidPlatform.
  FileAndroidPlatform() : super(token: _token);

  static final Object _token = Object();

  static FileAndroidPlatform _instance = MethodChannelFileAndroid();

  /// The default instance of [FileAndroidPlatform] to use.
  ///
  /// Defaults to [MethodChannelFileAndroid].
  static FileAndroidPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FileAndroidPlatform] when
  /// they register themselves.
  static set instance(FileAndroidPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> salvarArquivoBase64({
    required String nomeArquivo,
    required String nomeDiretorioApp,
    required String mimetypeArquivo,
    required String base64,
  }) {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
