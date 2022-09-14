import 'file_android_platform_interface.dart';

class FileAndroid {
  Future<String?> getPlatformVersion() {
    return FileAndroidPlatform.instance.getPlatformVersion();
  }

  Future<String?> salvarArquivoBase64({
    required String nomeArquivo,
    String nomeDiretorioApp = '',
    required String mimetypeArquivo,
    required String base64,
  }) {
    return FileAndroidPlatform.instance.salvarArquivoBase64(
      nomeArquivo: nomeArquivo,
      nomeDiretorioApp: nomeDiretorioApp,
      mimetypeArquivo: mimetypeArquivo,
      base64: base64,
    );
  }
}
