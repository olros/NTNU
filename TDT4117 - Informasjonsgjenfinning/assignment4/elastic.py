from datetime import datetime
from elasticsearch import Elasticsearch
import uuid
import codecs
from pathlib import Path
import shutil
import os
ELASTIC_PASSWORD = "p2iFCHUbC7ze1QoIMVw"

es = Elasticsearch("http://localhost:9200",
                    basic_auth=("elastic", ELASTIC_PASSWORD))

es.info()

indexName = "tdt4117_assign_4"
files = [
    'Text1.txt',
    'Text2.txt',
    'Text3.txt',
    'Text4.txt',
    'Text5.txt',
    'Text6.txt',
]
for file in files:
    text = codecs.open(f"./DataAssignment4/{file}", "r", "utf-8").read()
    response = es.index(
    
        index = indexName,
        id = file,
        body = {'text': text}
    )
    print(response)


queries = ["claim", "claim*", "claims of duty", "claims of duty in an alternative way", "duty"]
for query in queries:
    resp = es.search(index=indexName, query={"match": { "text" : query}})
    results = []
    for hit in resp['hits']['hits']:
        results.append(hit['_id'])
    
    print(f'Query: "{query}", hits: {results}')

count = 1


docs = []
files = Path("enron/maildir").glob("**/*")
#limit number of email to 100000
count = 0
for i, f in enumerate(files):
    if count >= 100000:
        break
    if f.is_file():
        with open(f, "r") as text:
            try:
                
                t= text.read()
                docs.append({ "index": {"_index": indexName, "_id": text.name}})
                docs.append({"id": text.name, "text":t})
                count += 1
            except Exception as e:
                print("skipped")
                print(e)

print("indexing")

def chunks(lst, n):
    for i in range(0, len(lst), n):
        yield lst[i:i+n]

batches = chunks(docs, 1000)

for batch in batches:
    es.bulk(index = indexName, operations=batch)
    



queries = ["'Norwegian and University and Science and Technology", "Norwegian University Science Technology"]
for query in queries:
    resp = es.search(index=indexName, query={"match": { "text" : query}})
    results = []
    for hit in resp['hits']['hits']:
        results.append(hit['_id'])
    
    print(f'Query: "{query}", hits: {results}')
