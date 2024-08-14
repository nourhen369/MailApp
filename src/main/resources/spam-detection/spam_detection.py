from flask import Flask, request, jsonify
import string
import numpy as np
import pandas as pd
import nltk
from nltk.corpus import stopwords
from nltk.stem.porter import PorterStemmer
from sklearn.ensemble import RandomForestClassifier
from sklearn.feature_extraction.text import CountVectorizer

# Initialize Flask app
app = Flask(__name__)

# Load data and model
df = pd.read_csv('src/main/resources/spam-detection/spam_ham_dataset.csv')
df['text'] = df['text'].apply(lambda x: x.replace('\r\n', ' '))

stemmer = PorterStemmer()
corpus = []
stopwords_set = set(stopwords.words('english'))

for i in range(0, len(df)):
    review = df['text'].iloc[i].lower()
    review = review.translate(str.maketrans('', '', string.punctuation)).split()
    review = [stemmer.stem(word) for word in review if not word in stopwords_set]
    review = ' '.join(review)
    corpus.append(review)

vectorize = CountVectorizer()
X = vectorize.fit_transform(corpus).toarray()
y = df['label_num']
classifier = RandomForestClassifier(n_jobs=-1)
classifier.fit(X, y)

@app.route('/detect-spam', methods=['POST'])
def detect_spam():
    email_text = request.json['email']
    email_text = email_text.lower().translate(str.maketrans('', '', string.punctuation)).split()
    email_text = [stemmer.stem(word) for word in email_text if not word in stopwords_set]
    email_text = ' '.join(email_text)
    email_corpus = [email_text]
    X_email = vectorize.transform(email_corpus)

    prediction = classifier.predict(X_email)
    result = 'spam' if prediction[0] == 1 else 'not spam'

    return jsonify({'result': result})

if __name__ == '__main__':
    app.run(debug=True)
