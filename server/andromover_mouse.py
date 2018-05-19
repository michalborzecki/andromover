import sys
import json
from typing import Union, Optional

import pyautogui as pg


EVENTS = {}


def event(fun):
    EVENTS[fun.__name__] = fun


def mouse(msg: Union[bytes, bytearray]):
    print('Received msg: ', msg)

    try:
        decoded_msg = msg.decode()
        mouse_event = json.loads(decoded_msg)
        event_type = mouse_event['type']
        event_details = mouse_event['details']
        fun = EVENTS[event_type]
        fun(**event_details)
    except Exception as ex:
        print('Failed to handle mouse event: ', ex, file=sys.stderr)


@event
def click(button: str, x: Optional[float] = None, y: Optional[float] = None):
    pg.click(button=button, x=x, y=y)


@event
def move(x: float, y: float):
    pg.moveRel(x, y, 0.1)
