# qr_from_file 

Exclusive for Android

A plugin that scans qr code from files (only), whether it's from your flutter assets or your gallery.

## Getting Started

Make sure you have images downloaded for gallery qr scan.

For qr scanning of images inside your flutter assets dont forget to [add them in your pubspec.yaml file](https://flutter.io/docs/development/ui/assets-and-images).


## API
### Android

The content of the qr barcode is given to a String:

For qr barcode images inside the flutter assets file:

```dart
String rawResult = QrFromFile.scanImageFromAssets('assets/images/qr_wikipedia.png'); //raw result
```

For qr barcode images inside the device storage (gallery):

```dart
String rawResult = QrFromFile.scanImageFromGallery('/storage/emulated/0/Download/qr_wikipedia.png'); //raw result
```

### iOS

For now the iOs functionality has been disabled. The app won't crash on iOS if you add it to your `pubspec.yaml` file. 