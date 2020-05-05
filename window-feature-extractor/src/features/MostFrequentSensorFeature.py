from src.features.base.Feature import Feature


class MostFrequentSensorFeature(Feature):
    def __init__(self):
        self.name = 'Most frequent sensor'

    # for previous_most_frequent_sensor call this for previous window
    def get_result(self, window):
        sensor_names = window.get_sensor_names()
        return max(set(sensor_names), key=sensor_names.count)
