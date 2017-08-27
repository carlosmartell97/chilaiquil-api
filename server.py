from image_recognizer import ImageRecognizer
from pymongo import MongoClient
from flask import *

import configparser
import json
import os

# Configuration file
with open('config.json') as json_data_file:
    data = json.load(json_data_file)

# MongoDB
app = Flask(__name__)
client = MongoClient(data['mongo_db']['address'], data['mongo_db']['port'])
collection = client[data['mongo_db']['database']][data['mongo_db']['collection']]

# Tensor Flow
ir = ImageRecognizer(
    data['abs_path'] + data['tf_path'],
    data['tensor_flow']['graph'],
    data['tensor_flow']['labels'],
    data['tensor_flow']['input_layer_name'],
    data['tensor_flow']['output_layer_name'],
    data['tensor_flow']['num_top_predictions']
)

# Main and only route
@app.route('/upload', methods=['POST'])
def upload():
    if request.method == 'POST':
        file = request.files.get('fileupload')
        filename = file.filename
        file.save(data['abs_path'] + data['img_path'] + filename)

        results = ir.recognize(data['img_path'] + filename)
        results['image'] = filename
        print(filename)
        collection.insert(results)
        del results['_id']

        print(results)
        return jsonify(results)
