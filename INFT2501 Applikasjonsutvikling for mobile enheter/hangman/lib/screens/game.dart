import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class GameScreen extends StatefulWidget {
  const GameScreen({Key? key}) : super(key: key);

  @override
  State<GameScreen> createState() => _GameScreen();
}

class _GameScreen extends State<GameScreen> {
  String _wordToGuess = '';
  List<String> _correctLettersGuessed = [];
  List<String> _wrongLettersGuessed = [];

  void _generateNewWordToGuess(BuildContext context) {
    List<String> words = AppLocalizations.of(context)!.game_words.split(",");

    String randomWord = (words.toList()..shuffle()).first;
    setState(() {
      _wordToGuess = randomWord;
    });
  }

  void _restart(BuildContext context) {
    _generateNewWordToGuess(context);
    setState(() {
      _correctLettersGuessed = [];
      _wrongLettersGuessed = [];
    });
  }

  void _guessLetter(String letter) {
    if (_wordToGuess.toLowerCase().contains(letter.toLowerCase())) {
      setState(() {
        _correctLettersGuessed = [
          ..._correctLettersGuessed,
          letter.toLowerCase()
        ];
      });
    } else {
      setState(() {
        _wrongLettersGuessed = [..._wrongLettersGuessed, letter.toLowerCase()];
      });
    }
  }

  @override
  void initState() {
    super.initState();

    WidgetsBinding.instance!.addPostFrameCallback((_) {
      _generateNewWordToGuess(context);
    });
  }

  @override
  Widget build(BuildContext context) {
    List<String> letters = AppLocalizations.of(context)!.game_letters.split('');

    TextStyle textStyle = const TextStyle(fontSize: 18.0);

    List<String> _toGuessLetters = _wordToGuess
        .split('')
        .map((letter) => _correctLettersGuessed.contains(letter.toLowerCase())
            ? letter
            : '_')
        .toList();
    bool _hasWon = _toGuessLetters.where((letter) => letter == '_').isEmpty;
    bool _hasLost = _wrongLettersGuessed.length > 5;

    return Scaffold(
      appBar: AppBar(
        title: Text(AppLocalizations.of(context)!.title),
      ),
      body: Center(
        child: ListView(
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Image(
                image: AssetImage(
                    'assets/hangman_state_${_wrongLettersGuessed.length + 1}.jpg'),
                height: 300.0,
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Text(_toGuessLetters.join(" "),
                  textAlign: TextAlign.center, style: textStyle),
            ),
            if (_hasWon)
              Column(children: <Widget>[
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Text(AppLocalizations.of(context)!.game_you_won,
                      textAlign: TextAlign.center, style: textStyle),
                ),
                Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: ElevatedButton(
                        onPressed: () {
                          _restart(context);
                        },
                        child:
                            Text(AppLocalizations.of(context)!.game_restart))),
              ])
            else if (_hasLost)
              Column(children: <Widget>[
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Text(
                      AppLocalizations.of(context)!.game_you_lost +
                          _wordToGuess,
                      textAlign: TextAlign.center,
                      style: textStyle),
                ),
                Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: ElevatedButton(
                        onPressed: () {
                          _restart(context);
                        },
                        child:
                            Text(AppLocalizations.of(context)!.game_restart))),
              ])
            else
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Column(
                  children: <Widget>[
                    Text('Pick a letter',
                        textAlign: TextAlign.center, style: textStyle),
                    Wrap(
                      spacing: 1.0,
                      runSpacing: 0.0,
                      children: [
                        for (String letter in letters)
                          ElevatedButton(
                              child: Text(letter),
                              onPressed:
                                  _correctLettersGuessed.contains(letter) ||
                                          _wrongLettersGuessed.contains(letter)
                                      ? null
                                      : () {
                                          _guessLetter(letter);
                                        },
                              style: ElevatedButton.styleFrom(
                                minimumSize: const Size(30, 30),
                              ))
                      ],
                    )
                  ],
                ),
              ),
          ],
        ),
      ),
    );
  }
}
