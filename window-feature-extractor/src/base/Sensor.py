class Sensor:
    def __init__(self, name, state, location):
        self.name = name
        self.state = state
        self.location = location

    def to_string(self):
        if self.location:
            return "SENSOR [name = " + self.name + ", state = " + self.state + ", location = " + self.location + "]"
        else:
            return "SENSOR [name = " + self.name + ", state = " + self.state + "]"
