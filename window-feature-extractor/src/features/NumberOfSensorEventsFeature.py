from src.features.base.Feature import Feature


class NumberOfSensorEventsFeature(Feature):
    def __init__(self):
        self.name = 'Number of sensor events'

    # returns the number of the window events
    def get_result(self, window):
        return len(window.events)
