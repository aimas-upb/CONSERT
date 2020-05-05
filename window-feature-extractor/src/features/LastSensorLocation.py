from src.features.base.Feature import Feature


class LastSensorLocationFeature(Feature):
    def __init__(self):
        self.name = 'Last sensor location'

    # returns the location of the last activated sensor
    def get_result(self, window):
        return window.events[-1].sensor.location
