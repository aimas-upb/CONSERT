class Event:
    def __init__(self, date, time, sensor):
        self.date = date
        self.time = time
        self.sensor = sensor

    def to_string(self):
        return "EVENT [date = " + self.date + ", time = " + self.time + ", " + self.sensor.to_string() + "]"