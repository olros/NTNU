#include <iostream>
#include <string>

using namespace std;

int main()
{
  // a)
  cout << "a)" << endl;
  string word1;
  string word2;
  string word3;
  cout << "Skriv inn ord 1:" << endl;
  cin >> word1;
  cout << "Skriv inn ord 2:" << endl;
  cin >> word2;
  cout << "Skriv inn ord 3:" << endl;
  cin >> word3;

  // b)
  cout << "\nb)" << endl;
  string sentence = word1 + " " + word2 + " " + word3;
  cout << "Setning: " << sentence << endl;

  // c)
  cout << "\nc)" << endl;
  cout << "Lengde ord 1: " << word1.length() << endl;
  cout << "Lengde ord 2: " << word2.length() << endl;
  cout << "Lengde ord 3: " << word3.length() << endl;
  cout << "Lengde setning: : " << sentence.length() << endl;

  // d)
  cout << "\nd)" << endl;
  string sentence2 = sentence;

  // e)
  cout << "\ne)" << endl;
  for (size_t i = 10; i <= 12; i++)
  {
    if (sentence2[i])
    {

      sentence2[i] = 'x';
    }
  }
  cout << "Setning: " << sentence << endl;
  cout << "Setning 2: " << sentence2 << endl;

  // f)
  cout << "\nf)" << endl;
  string sentence_start = "";
  for (size_t i = 0; i < 5; i++)
  {
    if (sentence[i])
    {

      sentence_start += sentence[i];
    }
  }
  cout << "Setning: " << sentence << endl;
  cout << "Setning_start: " << sentence_start << endl;

  // g)
  cout << "\ng)" << endl;
  if (sentence.find("hallo") != string::npos)
  {
    cout << "Setningen inneholder \"hallo\"" << endl;
  }
  else
  {
    cout << "Setningen inneholder ikke \"hallo\"" << endl;
  }

  // h)
  cout << "\nh)" << endl;
  int er_count = 0;
  size_t index = sentence.find("er", 0);
  while (index != string::npos)
  {
    er_count++;
    index = sentence.find("er", index + 1);
  }
  cout << "\"er\" appears " << er_count << " times" << endl;
}