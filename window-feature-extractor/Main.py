#!/usr/bin/python

from src.base.Window import Window
from src.features.DominantLocationFeature import DominantLocationFeature
from src.features.LastSensorLocation import LastSensorLocationFeature
from src.features.MostFrequentSensorFeature import MostFrequentSensorFeature
from src.features.MostRecentSensorFeature import MostRecentSensorFeature
from src.features.NumberOfSensorEventsFeature import NumberOfSensorEventsFeature
from src.features.WindowDurationFeature import WindowDurationFeature
from src.features.extractor.FeatureExtractor import FeatureExtractor
from src.utils.WindowEventsParser import WindowEventsParser
from argparse import ArgumentParser


def main(file):

    parser = WindowEventsParser()
    parser.read_data_from_file(file)

    window = Window(parser.events)
    # features
    number_of_sensor_events_feature = NumberOfSensorEventsFeature()
    window_duration_feature = WindowDurationFeature()
    most_recent_sensor_feature = MostRecentSensorFeature()
    most_frequent_sensor_feature = MostFrequentSensorFeature()
    last_sensor_location = LastSensorLocationFeature()
    dominant_location_feature = DominantLocationFeature()

    window.to_string()

    features = [number_of_sensor_events_feature,
                window_duration_feature,
                most_recent_sensor_feature,
                most_frequent_sensor_feature,
                last_sensor_location,
                dominant_location_feature]

    feature_extractor = FeatureExtractor(features)
    print(feature_extractor.extract_features_from_window(window))


if __name__ == "__main__":
    argument_parser = ArgumentParser(description='.')
    argument_parser.add_argument('--file', type=str, help='Path to file.', required=True)

    argument = argument_parser.parse_args()

    main(argument.file)
