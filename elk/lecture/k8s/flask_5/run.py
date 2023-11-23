from flask import Flask
import logging
import sys
# 로깅 기본 설정
logging.basicConfig(level=logging.INFO, stream=sys.stdout)

app = Flask(__name__)

@app.route("/")
def hello_world():
    result = "Hello World"
    logging.info("This is a test log message.")
    return result

@app.route("/error")
def error():
    result = "Error Test"
    logging.error(result)
    return result

if __name__=='__main__':
    app.run(host="0.0.0.0", port=8080, debug=True)