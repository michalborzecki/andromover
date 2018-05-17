import json
from typing import Union, Optional

import pyautogui as pg


class Mouse:

    def __call__(self, msg: Union[bytes, bytearray]):
        event = json.loads(msg.decode())
        try:
            method = getattr(self, event['type'])
            method(**event['details'])
        except KeyError:
            pass

    def click(self, *, x: Optional[float] = None, y: Optional[float] = None,
              button: str):
        pg.click(x=x, y=y, button=button)

    def move(self, x: float, y: float):
        pg.moveRel(x, y)
