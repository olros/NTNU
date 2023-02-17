import codecs
import string
from nltk.stem.porter import PorterStemmer
import gensim

# 1.0. Fix random numbers generator
import random; random.seed(123)

class Assignment3:

  def __init__(self):
      self.stemmer = PorterStemmer()

      self.processed_paragraphs, self.original_paragraphs = self.__get_processed_paragraphs()

      self.dictionary, self.bagOfWords = self.__dictionary_building()

      self.tfidf_model, self.tfidf_index, self.lsi_model, self.lsi_index = self.__retrieval_models()

  def __preprocess(self, text):
      # 1.4. Tokenize paragraphs (split them into words)
      words = text.split()

      # 1.5. Remember to remove from the text punctuation (see string.punctuation) and whitecharacters ("\n\r\t")
      # 1.6. Using PorterStemmer stem words
      processed_words = [self.stemmer.stem(word.translate(str.maketrans('', '', string.punctuation+"\n\r\t"))) for word in words]
      return processed_words
  
  def __get_processed_paragraphs(self):
      """Part 1: Data loading and preprocessing"""
      # 1.1 Open and load the file (it’s UTF-8 encoded) using codecs
      f = codecs.open("pg3300.txt", "r", "utf-8")

      # 1.5 Convert everything to lower-case
      file = f.read().lower()

      # 1.2. Partition file into separate paragraphs
      raw_paragraphs = file.split('\n\n')

      # 1.3. Remove (filter out) paragraphs containing the word “Gutenberg”
      original_paragraphs = list(filter(lambda x: 'gutenberg' not in x, raw_paragraphs))

      processed_paragraphs = [self.__preprocess(paragraph) for paragraph in original_paragraphs]

      return processed_paragraphs, original_paragraphs

  def __get_stopwords(self):
      f = codecs.open("common-english-words.txt", "r", "utf-8")
      return f.read().split(",")

  def __dictionary_building(self):
      """Part 2: Dictionary building"""
      # 2.1. Build a dictionary
      dictionary = gensim.corpora.Dictionary(self.processed_paragraphs)

      # 2.1. Filter out stopwords
      stopwords = self.__get_stopwords()
      stopword_ids = [dictionary.token2id[stopword] for stopword in stopwords if stopword in dictionary.token2id]
      dictionary.filter_tokens(stopword_ids)

      # 2.2. Map paragraphs into Bags-of-Words
      bagOfWords = [dictionary.doc2bow(paragraph) for paragraph in self.processed_paragraphs]
      return dictionary, bagOfWords

  def __retrieval_models(self):
      """Part 3: Retrieval Models"""
      # 3.1. Build TF-IDF model using corpus
      tfidf_model = gensim.models.TfidfModel(self.bagOfWords)

      # 3.2. Map Bags-of-Words into TF-IDF weights
      tfidf_corpus = tfidf_model[self.bagOfWords]

      # 3.3. Construct MatrixSimilarity object that let us calculate similarities between paragraphs and queries
      tfidf_index = gensim.similarities.MatrixSimilarity(tfidf_corpus)
    
      # 3.4. Repeat the above procedure for LSI model using as an input the corpus with TF-IDF weights
      lsi_model = gensim.models.LsiModel(tfidf_corpus, id2word=self.dictionary, num_topics=100)
      lsi_corpus = lsi_model[self.bagOfWords]
      lsi_index = gensim.similarities.MatrixSimilarity(lsi_corpus)

      # 3.5. Report and try to interpret first 3 LSI topics
      result = lsi_model.print_topics()
      
      return tfidf_model, tfidf_index, lsi_model, lsi_index

  def query(self, query):
      """Part 4: Querying"""
      # 4.1. For the query apply all necessary transformations
      query = self.__preprocess(query)
      query = self.dictionary.doc2bow(query)

      # 4.2. Convert BOW to TF-IDF representation
      tfidf_query = self.tfidf_model[query]
      tfidf_query_formatted = list(map(lambda result: (self.dictionary[result[0]], str(round(result[1], 2))), tfidf_query))  

      print(tfidf_query_formatted)

      # 4.3. Report top 3 the most relevant paragraphs for the query
      query_index = enumerate(self.tfidf_index[tfidf_query])
      query_index_sorted = sorted(query_index, key=lambda result: result[1], reverse=True)[:3]

      for paragraph_index, _ in query_index_sorted:
          print(f'[paragraph {paragraph_index}]')
          print('\n'.join(self.original_paragraphs[paragraph_index].split('\n')[:5]))
          print()

      # 4.4. Convert query TF-IDF representation into LSI-topics representation (weights)
      lsi_query = self.lsi_model[tfidf_query]
      sorted_lsi_query = sorted(lsi_query, key=lambda key_value: abs(key_value[1]), reverse=True)[:3]

      for topic, _ in sorted_lsi_query:
        print(f'[topic {topic}]')
        print(f'{self.lsi_model.show_topics()[topic][1]}')

# query = "What is the function of money?"
query = "How taxes influence Economics?"

model = Assignment3()
model.query(query)
