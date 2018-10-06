# Python REST performance

## Install required modules

```
pip install flask flask-jsonpify flask-sqlalchemy flask-restful
```

## Run 

```
python rest-api.py
```

## Test performance

```
ab -n 1000 -c 4 http://localhost:5002/employees
```
