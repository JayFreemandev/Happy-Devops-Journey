from flask import Flask
from flask import request, jsonify
import logging
import telegram
import asyncio
import sys
# 로깅 기본 설정
logging.basicConfig(level=logging.INFO, stream=sys.stdout)

app = Flask(__name__)

# Telegram 봇의 토큰과 채팅 ID
token = "6963065955:AAH6Mq13-u9YG372CGJI_GmHxnkEDOg-zaM"
chat_id = -4024208378

# Telegram 봇 인스턴스 생성
bot = telegram.Bot(token=token)

# 보낼 메시지
message = "BITNAMU ERROR ! BITNAMU ERROR ! BITNAMU ERROR !"

async def send_telegram_message():
    await bot.send_message(chat_id=chat_id, text=message)

@app.route("/")
def hello_world():
    result = "BITNAMU TEST ! BITNAMU TEST ! BITNAMU TEST !"
    logging.info("This is a test log message.")
    return result

@app.route("/error")
def error():
    result = "BITNAMU ERROR ! BITNAMU ERROR ! BITNAMU ERROR !"
    logging.error(result)
    return result

if __name__=='__main__':
    app.run(host="0.0.0.0", port=8080, debug=True)