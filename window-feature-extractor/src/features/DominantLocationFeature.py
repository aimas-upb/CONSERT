from src.features.base.Feature import Feature


class DominantLocationFeature(Feature):
    def __init__(self):
        self.name = 'Dominant location'

    # returns the location with the most sensor activations
    def get_result(self, window):
        sensor_locations = window.get_sensor_locations()
        return max(set(sensor_locations), key=sensor_locations.count)
