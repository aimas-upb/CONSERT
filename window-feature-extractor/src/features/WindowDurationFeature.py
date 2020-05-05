import datetime

from src.features.base.Feature import Feature


class WindowDurationFeature(Feature):
    TIME_FORMAT = '%Y-%m-%d %H:%M:%S.%f'

    def __init__(self):
        self.name = 'Window duration'

    # returns the duration of the window
    def get_result(self, window):
        first_event = window.events[0]
        last_event = window.events[-1]
        first_event_time = datetime.datetime.strptime(first_event.date + ' ' + first_event.time, self.TIME_FORMAT)
        last_event_time = datetime.datetime.strptime(last_event.date + ' ' + last_event.time, self.TIME_FORMAT)
        return last_event_time - first_event_time
