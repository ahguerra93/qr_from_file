import 'dart:io';

import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:qr_from_file/qr_from_file.dart';
import 'package:image_picker/image_picker.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String _result = '...';
  Future<File> _imageFile;
  VoidCallback listener;
  String _qrPath;

  void _onImageButtonPressed(ImageSource source) {
    setState(() {
      
        _imageFile = ImagePicker.pickImage(source: source);
      
    });
  }

  @override
  void initState() {
    super.initState();
    initPlatformState();
    listener = () {
      setState(() {});
    };
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await QrFromFile.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }
  Widget _previewImage() {
    return FutureBuilder<File>(
        future: _imageFile,
        builder: (BuildContext context, AsyncSnapshot<File> snapshot) {
          if (snapshot.connectionState == ConnectionState.done &&
              snapshot.data != null) {
            
            _qrPath = snapshot.data.path;
            
            return Image.file(snapshot.data);
          } else if (snapshot.error != null) {
            return const Text(
              'Error picking image.',
              textAlign: TextAlign.center,
            );
          } else {
            return const Text(
              'You have not yet picked an image.',
              textAlign: TextAlign.center,
            );
          }
        });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
            child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text('Running on: $_platformVersion\n'),
            RaisedButton(
              child: Text('Scan Image'),
              onPressed: () {
                print("pressed");
                Future<String> futureResult =
                    QrFromFile.scanImageFromGallery(_qrPath);
                futureResult.then((res) {
                  _result = res;
                  setState(() {});
                });
              },
            ),

            new Center(
              child: _previewImage(),
            ),
            Text("Result is: $_result")
          ],
        )),
        floatingActionButton :FloatingActionButton(
            onPressed: () {
              _onImageButtonPressed(ImageSource.gallery);
              _result = "...";
            },
            heroTag: 'image0',
            tooltip: 'Pick Image from gallery',
            child: const Icon(Icons.photo_library),
          ),
      ),
    );
  }
}
