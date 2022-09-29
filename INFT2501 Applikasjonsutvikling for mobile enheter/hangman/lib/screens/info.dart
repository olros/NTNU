import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class InfoScreen extends StatelessWidget {
  const InfoScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    TextStyle textStyle = const TextStyle(fontSize: 18.0);

    return Scaffold(
      appBar: AppBar(
        title: Text(AppLocalizations.of(context)!.title),
      ),
      body: Center(
        child: ListView(
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Text(AppLocalizations.of(context)!.info_paragraph1,
                  textAlign: TextAlign.center,
                  style: const TextStyle(fontWeight: FontWeight.bold)
                      .merge(textStyle)),
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Text(AppLocalizations.of(context)!.info_paragraph2,
                  textAlign: TextAlign.center, style: textStyle),
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Text(AppLocalizations.of(context)!.info_paragraph3,
                  textAlign: TextAlign.center, style: textStyle),
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Text(AppLocalizations.of(context)!.info_paragraph4,
                  textAlign: TextAlign.center, style: textStyle),
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Text(AppLocalizations.of(context)!.info_paragraph5,
                  textAlign: TextAlign.center, style: textStyle),
            ),
            Padding(
                padding: const EdgeInsets.all(8.0),
                child: ElevatedButton(
                    child: Text(AppLocalizations.of(context)!.info_play),
                    onPressed: () {
                      Navigator.pushNamed(
                        context,
                        '/game',
                      );
                    },
                    style: ElevatedButton.styleFrom(
                      minimumSize: const Size(double.infinity, 50),
                    ))),
          ],
        ),
      ),
    );
  }
}
