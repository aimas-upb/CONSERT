from src.features.base.Feature import Feature


class MostRecentSensorFeature(Feature):
    def __init__(self):
        self.name = 'Most recent sensor'

    # returns last sensor of window
    def get_result(self, window):
        return window.events[-1].sensor.name
