import codecs
import string

def __get_stopwords():
      f = codecs.open("common-english-words.txt", "r", "utf-8")
      return f.read().split(",")

def get_words_from_file(file_name: str):
    f = codecs.open(file_name, "r", "utf-8")
    words = f.read().lower().split()

    # Remove punctuation and whitecharacters
    words = [word.translate(str.maketrans('', '', string.punctuation+" \n\r\t")) for word in words]

    # Remove stopwords from the text
    stopwords = __get_stopwords()
    words = [word for word in words if word not in stopwords]

    return words


def create_index():
    index = {}

    files = [
      'Text1.txt',
      'Text2.txt',
      'Text3.txt',
      'Text4.txt',
      'Text5.txt',
      'Text6.txt',
    ]
    for file in files:
        words = get_words_from_file(f"./DataAssignment4/{file}")
        for word in words:
            if word in index:
                if not file in index[word]:
                  index[word].add(file)
            else:
                index[word] = set([file])

    return index

def run_query(query: str):
    index = create_index()
    words = query.lower().split()

    results = set()

    for word in words:
        if word in index:
            for document in index[word]:
                results.add(document)

    print(f'Query: {query}, hits: {results}')
    return results

run_query('claim')
run_query('claim*')
run_query('claims of duty')
run_query('claims of duty in an alternative way')
run_query('duty')
