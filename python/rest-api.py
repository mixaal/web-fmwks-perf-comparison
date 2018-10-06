from flask import Flask, request
from flask_restful import Resource, Api
from sqlalchemy import create_engine
from json import dumps
#from flask.ext.jsonpify import jsonify

app = Flask(__name__)
api = Api(app)

class Employees(Resource):
    def get(self):
        return "Hello world!"




api.add_resource(Employees, '/employees') # Route_1


if __name__ == '__main__':
    app.run(port='5002')
