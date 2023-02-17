
from datetime import datetime
from elasticsearch import Elasticsearch
from pathlib import Path

ELASTIC_PASSWORD = "p2iFCHUbC7ze1QoIMVw"

es = Elasticsearch("http://localhost:9200",
                    basic_auth=("elastic", ELASTIC_PASSWORD))

es.info()
indexName = "tdt4117_assign_5"
count = 1

def get_mail_data(text):
    message_id_index = text.find("Message-ID: ")
    date_index = text.find("Date: ")
    from_index = text.find("From: ")
    to_index = text.find("To: ")
    subject_index = text.find("Subject: ")
    content_index = text.find("\n\n")

    message_id = text[text.find(': ', message_id_index)+2:text.find('\n', message_id_index)]
    date = text[text.find(': ', date_index)+2:text.find('\n', date_index)]
    from_email = text[text.find(': ', from_index)+2:text.find('\n', from_index)]
    subject = text[text.find(': ', subject_index)+2:text.find('\n', subject_index)]
    content = text[content_index+2:]

    parsed_date = datetime.strptime(date[5:31], '%d %b %Y %H:%M:%S %z')

    to_emails = text[to_index+4:subject_index-1].split(',')
    to_emails = [x.strip() for x in to_emails]


    dict = {
        "id" : message_id,
        "date": parsed_date,
        "from": from_email,
        "to": to_emails,
        "subject": subject,
        "content": content,
    }
    
    return dict


def insert():
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
                    data = get_mail_data(t)
                    docs.append({ "index": {"_index": indexName, "_id": data['id']}})
                    docs.append(data)
                    count += 1
                    if count % 1000 == 0:
                        print(count)
                except Exception:
                    pass

    print("indexing")

    def chunks(lst, n):
        for i in range(0, len(lst), n):
            yield lst[i:i+n]

    batches = chunks(docs, 1000)

    for batch in batches:
        es.bulk(index = indexName, operations=batch)

# insert()

def query_most_sent_emails(): 
    resp = es.search(index=indexName, aggregations={
            "group_by_from": {
                "terms": {
                    "field" : "from.keyword"
                }
            }
    })
    
    print(resp['aggregations']['group_by_from']['buckets'][0])
    
# query_most_sent_emails()


def most_common_subject():
    resp = es.search(index=indexName, aggregations={
            "group_by_subject": {
                "terms": {
                    "field" : "subject.keyword"
                }
            }
    })
    
    print(resp['aggregations']['group_by_subject']['buckets'])

# most_common_subject()

def not_from_eron():
    resp = es.count(index=indexName,
            query={
                "bool": {
                    "must": [
                        {
                                "from.keyword": {
                                    "value" : "debra.perlingiere@enron.com*"
                                }
                        }
                    ]
                }
            },
    )
    
    print(resp)
    
not_from_eron()

def debra_talked_to():
    resp = es.count(index=indexName,
            query={
                    "filter": {
                    "query" : {
                        "match" : {
                            "from.keyword": "debra.perlingiere@enron.com",
                            "to": "debra.perlingiere@enron.com"
                        }
                    }
            }
            }
    )
    
    print(resp)

debra_talked_to()