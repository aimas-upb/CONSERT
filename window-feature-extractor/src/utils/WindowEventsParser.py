from src.base.Event import Event
from src.base.Sensor import Sensor
import codecs


class WindowEventsParser:
    events = []

    @staticmethod
    def safe_open(input_file_path, rw):
        try:
            input_file = codecs.open(input_file_path, rw, 'utf-8')
        except UnicodeDecodeError:
            input_file = codecs.open(input_file_path, rw, 'utf-16')
        return input_file

    def read_data_from_file(self, file):
        with self.safe_open(file, "r") as file:
            for line in file:
                elements = line.split()
                date = elements[0]
                time = elements[1]

                # checks if data has locations
                if len(elements) == 4:
                    sensor = Sensor(elements[2], elements[3], None)
                else:
                    sensor = Sensor(elements[3], elements[4], elements[2])

                self.events.append(Event(date, time, sensor))
            file.close()

