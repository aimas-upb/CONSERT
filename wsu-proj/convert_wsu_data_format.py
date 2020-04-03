#!/usr/bin/python

import codecs
import sys
import os, ntpath
import shutil
import datetime, pytz
from argparse import ArgumentParser
from typing import Dict, List

import json

epoch = datetime.datetime.utcfromtimestamp(0)
epoch = epoch.replace(tzinfo=pytz.UTC)


def find_sensor_area(sensor_name: str, sensor_mapping: Dict[str, List[str]]):
    if sensor_mapping:
        for area, sensor_list in sensor_mapping.items():
            if sensor_name in sensor_list:
                return area

    return None


def extract_sensor_type(sensor_name: str):
    sensor_type = ""
    for c in sensor_name:
        if c.isalpha():
            sensor_type += c
        else:
            break
    return sensor_type


def is_data_source_file(filename: str) -> bool:
    if "interweave" in filename or "interwoven" in filename or filename == "ann.txt":
        return True

    return False


def safe_open(input_file_path, rw="r"):
    try:
        input_file = codecs.open(input_file_path, rw, 'utf-8')
        #input_file.readline()
    except UnicodeDecodeError:
        input_file = codecs.open(input_file_path, rw, 'utf-16')
    return input_file


def convert_file(input_file_path: str, output_file_path: str,
                 sensor_mapping: Dict[str, List[str]]):

    tokenized_lines = []
    with safe_open(input_file_path) as input_file:
        for line in input_file:
            tokens = line.strip().replace('\t', ' ').split(' ')
            tokenized_lines.append(tokens)

    with safe_open(output_file_path, rw="w") as output_file:
        current_activity_id = None

        for tokens in tokenized_lines:
            timestamp = " ".join([tokens[0], tokens[1]])
            sensor_id = tokens[2]
            sensor_val = tokens[3]

            sensor_semantics = find_sensor_area(sensor_id, sensor_mapping)
            if not sensor_semantics:
                sensor_semantics = extract_sensor_type(sensor_id)

            activity_id = "Other"
            if len(tokens) == 4 and current_activity_id:
                activity_id = current_activity_id
            elif len(tokens) == 5:
                if "=" in tokens[4]:
                    activity_data = tokens[4].split("=")
                    activity_id = activity_data[0]
                    if activity_data[1].lower() == "begin":
                        current_activity_id = activity_data[0]
                    else:
                        current_activity_id = None
                else:
                    current_activity_id = tokens[4]
                    activity_id = tokens[4]

            out_str = " ".join([timestamp, sensor_semantics, sensor_id, sensor_val, activity_id, "\n"])
            output_file.write(out_str)



def convert(src: str, out_folder: str, sensor_mapping: Dict[str, List[str]] = None):
    if os.path.isdir(src):
        # we are converting all the datasets in a folder

        # extract basename (last folder in path)
        base_folder = os.path.basename(os.path.normpath(src))

        # create output dir, removing previous if exists
        output_dir = out_folder + os.path.sep + base_folder
        if os.path.isdir(output_dir):
            shutil.rmtree(output_dir)

        # create output dirs
        os.makedirs(output_dir)

        for dirpath, dirnames, filenames in os.walk(src):
            print("[INFO] Parsing folder: " + dirpath)

            for filename in filenames:
                if is_data_source_file(filename):
                    print("[INFO] Parsing file: " + filename + " for person: " + filename.split(".")[0])

                    input_file_path = os.path.join(dirpath, filename)
                    output_file_path = os.path.join(output_dir, filename)

                    convert_file(input_file_path, output_file_path, sensor_mapping=sensor_mapping)

    elif os.path.isfile(src):
        filename = os.path.basename(os.path.normpath(src))
        output_file_path = os.path.join(out_folder, filename)
        convert_file(src, output_file_path, sensor_mapping=sensor_mapping)
    else:
        print("[ERROR] Argument provided not file or folder")
        sys.exit(-1)


if __name__ == "__main__":
    arg_parser = ArgumentParser(description='.')
    arg_parser.add_argument('--src', type=str, help='Path to source dataset folder.',
                            default="./wsu_datasets/adlinterweave", required=True)
    arg_parser.add_argument('--out-folder', type=str, help='Path to transformed dataset output folder',
                            default="./wsu_datasets_updated_format", required=True)
    arg_parser.add_argument('--sensor-mapping', type=str, help='Path to logical mapping of sensors to house areas',
                            required=False)

    arg = arg_parser.parse_args()

    src = arg.src
    out_folder = arg.out_folder
    sensor_mapping_file = arg.sensor_mapping
    sensor_mapping = None

    if sensor_mapping_file:
        with open(sensor_mapping_file) as f:
            sensor_mapping = json.load(f)

    convert(src, out_folder, sensor_mapping=sensor_mapping)
