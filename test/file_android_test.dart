import 'package:flutter_test/flutter_test.dart';
import 'package:file_android/file_android.dart';
import 'package:file_android/file_android_platform_interface.dart';
import 'package:file_android/file_android_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFileAndroidPlatform
    with MockPlatformInterfaceMixin
    implements FileAndroidPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<String?> salvarArquivoBase64({
    required String nomeArquivo,
    required String nomeDiretorioApp,
    required String mimetypeArquivo,
    required String base64,
  }) {
    throw UnimplementedError();
  }
}

void main() {
  final FileAndroidPlatform initialPlatform = FileAndroidPlatform.instance;

  test('$MethodChannelFileAndroid is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFileAndroid>());
  });

  test('getPlatformVersion', () async {
    FileAndroid fileAndroidPlugin = FileAndroid();
    MockFileAndroidPlatform fakePlatform = MockFileAndroidPlatform();
    FileAndroidPlatform.instance = fakePlatform;

    expect(await fileAndroidPlugin.getPlatformVersion(), '42');
  });
}
