import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);
  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  // Must be unique in an app. Prefix with a 'domain' type
  static const platform = MethodChannel('samples.flutter.dev/battery');
  static const speechChannel = MethodChannel('samples.flutter.dev/speech');
  String _batteryLevel = 'Unknown battery level';
  String _sessionStatus = "nuthing";

  static const stream = const EventChannel('events.flutter.dev/speech');
  late StreamSubscription speechEvents;

  String _talkText="";

  @override
  void initState() {
    super.initState();
    speechEvents = stream.receiveBroadcastStream().listen((SpeechEvents));
  }

  void SpeechEvents(dynamic str){
    var result = str.toString();
    if(result.startsWith("Recognizing:")) {
      setState(() {
        _talkText =  "Temp : ${result.substring("Recognizing:".length)}";
      });
    }
    else if(result.startsWith("Recognised:")) {
      setState((){
        _talkText =  "Final : ${result.substring("Recognised:".length)}";
      });
    }else {
      if(str == "SessionStarted Event") {_sessionStatus = "";}
      setState(() {
        _sessionStatus = _sessionStatus + "\n" + str.toString();
      });
    }
  }


  Future<void> microPhonePerms() async {
    await Permission.microphone.request();
    await Permission.storage.request();
  }

  Future<void> startSession() async {
    String sessionStatus;
    await speechChannel.invokeMethod('startSession');
    _talkText = "";
  }

  Future<void> endSession() async {
    await speechChannel.invokeMethod('endSession');
  }

  Future<void> startStreamSession() async {
    String sessionStatus;
    await speechChannel.invokeMethod('startStreamSession');
    _talkText = "";
  }

  Future<void> endStreamSession() async {
    await speechChannel.invokeMethod('endStreamSession');
  }


  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called, for instance as done
    // by the _incrementCounter method above.
    //
    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.
    return Material(
        child: Center(
            child: Column(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: [
        Text(_talkText),
        TextButton(onPressed: startSession, child: const Text("Start Session")),
        TextButton(onPressed: endSession, child: const Text("End Session")),
        TextButton(onPressed: startStreamSession, child: const Text("Start Stream Session")),
        TextButton(onPressed: endStreamSession, child: const Text("End Stream Session")),
        ElevatedButton(
          child: const Text('microphone perms'),
          onPressed: microPhonePerms,
        ),
        Text(_sessionStatus),
      ],
    )));
  }
}
