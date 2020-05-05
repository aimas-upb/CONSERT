class Window:
    def __init__(self, events):
        self.events = events

    def get_sensors(self):
        sensors = []
        for event in self.events:
            sensors.append(event.sensor)
        return sensors

    def get_sensor_names(self):
        sensor_names = []
        for sensor in self.get_sensors():
            sensor_names.append(sensor.name)
        return sensor_names

    def get_sensor_locations(self):
        sensor_locations = []
        for sensor in self.get_sensors():
            sensor_locations.append(sensor.location)
        return sensor_locations

    def to_string(self):
        print("WINDOW: ")
        for event in self.events:
            print(event.to_string())