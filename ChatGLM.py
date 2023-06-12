from langchain.llms.base import LLM
from langchain.llms.utils import enforce_stop_tokens
from typing import Dict, List, Optional, Tuple, Union

import requests
import json

class ChatGLM(LLM):
    max_token: int = 10000
    temperature: float = 0.1
    top_p = 0.9
    history = []

    def __init__(self):
        super().__init__()

    @property
    def _llm_type(self) -> str:
        return "ChatGLM"

    def _call(self, prompt: str, stop: Optional[List[str]] = None) -> str:
        # headers中添加上content-type这个参数，指定为json格式
        headers = {'Content-Type': 'application/json'}
        data=json.dumps({
          'prompt':prompt,
          'temperature':self.temperature,
          'history':self.history,
          'max_length':self.max_token
        })
        # print("ChatGLM prompt:",prompt)
        # 调用api
        response = requests.post("{your_host}/api",headers=headers,data=data)
		# print("ChatGLM resp:",response)
        if response.status_code!=200:
          return "查询结果错误"
        resp = response.json()
        if stop is not None:
            response = enforce_stop_tokens(response, stop)
        self.history = self.history+[[None, resp['response']]]
        return resp['response']
