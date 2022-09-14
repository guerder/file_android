import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:file_android/file_android_method_channel.dart';

void main() {
  MethodChannelFileAndroid platform = MethodChannelFileAndroid();
  const MethodChannel channel = MethodChannel('file_android');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
