#!/usr/bin/python

import dateparser
import sys
import os, ntpath
import shutil
import datetime, pytz

import codecs
import json

DISCRETE = "discrete"
NUMERIC = "numeric"

sensors = {}
epoch = datetime.datetime.utcfromtimestamp(0)
epoch = epoch.replace(tzinfo=pytz.UTC)


interweaved_activity_map = {
  1: "fill_dispenser",
  2: "watch_dvd",
  3: "water_plants",
  4: "answer_phone",
  5: "prepare_card",
  6: "prepare_soup",
  7: "clean",
  8: "choose_outfit"
}

def build_sensors():
    # add motion sensors
    for sensor_id in range(1, 52):
        id_key = 'M' + str(sensor_id).zfill(2)
        sensors[id_key] = {"type": "motion",
                           "id": id_key,
                           "value_type": DISCRETE,
                           "accepted_values": ["ON", "OFF"]}

    # add item sensors
    sensors['I01'] = {"type": "item",
                      "id": "soup",
                      "value_type": DISCRETE,
                      "accepted_values": ["ABSENT", "PRESENT"]}

    sensors['I02'] = {"type": "item",
                      "id": "glass",
                      "value_type": DISCRETE,
                      "accepted_values": ["ABSENT", "PRESENT"]}

    sensors['I03'] = {"type": "item",
                      "id": "DVD",
                      "value_type": DISCRETE,
                      "accepted_values": ["ABSENT", "PRESENT"]}

    sensors['I04'] = {"type": "item",
                      "id": "pill_dispenser",
                      "value_type": DISCRETE,
                      "accepted_values": ["ABSENT", "PRESENT"]}

    sensors['I05'] = {"type": "item",
                      "id": "DVD_Player",
                      "value_type": DISCRETE,
                      "accepted_values": ["ABSENT", "PRESENT"]}

    sensors['I06'] = {"type": "item",
                      "id": "medicine",
                      "value_type": DISCRETE,
                      "accepted_values": ["ABSENT", "PRESENT"]}

    sensors['I07'] = {"type": "item",
                      "id": "pot",
                      "value_type": DISCRETE,
                      "accepted_values": ["ABSENT", "PRESENT"]}

    sensors['I08'] = {"type": "item",
                      "id": "address_book",
                      "value_type": DISCRETE,
                      "accepted_values": ["ABSENT", "PRESENT"]}

    sensors['I09'] = {"type": "item", 
					  "id": "envelope", 
					  "value_type": DISCRETE, 
					  "accepted_values": ["ABSENT", "PRESENT"]}

    sensors['asterisk'] = {"type": "phone", "id": "phone", "value_type": DISCRETE, "accepted_values": ["START", "END"]}

    # add cabinet sensors
    sensors['D01'] = {"type": "cabinet",
                           "id": "door1",
                           "value_type": DISCRETE,
                           "accepted_values": ["OPEN", "CLOSE"]}

    sensors['D02'] = {"type": "cabinet",
                           "id": "door2",
                           "value_type": DISCRETE,
                           "accepted_values": ["OPEN", "CLOSE"]}

    sensors['D03'] = {"type": "cabinet",
                           "id": "door3",
                           "value_type": DISCRETE,
                           "accepted_values": ["OPEN", "CLOSE"]}
    sensors['D04'] = {"type": "cabinet",
                           "id": "door4",
                           "value_type": DISCRETE,
                           "accepted_values": ["OPEN", "CLOSE"]}
    sensors['D05'] = {"type": "cabinet",
                           "id": "door5",
                           "value_type": DISCRETE,
                           "accepted_values": ["OPEN", "CLOSE"]}
    sensors['D06'] = {"type": "cabinet",
                           "id": "door6",
                           "value_type": DISCRETE,
                           "accepted_values": ["OPEN", "CLOSE"]}
    sensors['D07'] = {"type": "cabinet",
                           "id": "cupboard",
                           "value_type": DISCRETE,
                           "accepted_values": ["OPEN", "CLOSE"]}
    
    sensors['D08'] = {"type": "cabinet",
                           "id": "freezer",
                           "value_type": DISCRETE,
                           "accepted_values": ["OPEN", "CLOSE"]}

    sensors['D09'] = {"type": "cabinet",
                           "id": "fridge",
                           "value_type": DISCRETE,
                           "accepted_values": ["OPEN", "CLOSE"]}
    
    sensors['D10'] = {"type": "cabinet",
                           "id": "microwave",
                           "value_type": DISCRETE,
                           "accepted_values": ["OPEN", "CLOSE"]}
    
    sensors['D11'] = {"type": "cabinet",
                           "id": "supplies",
                           "value_type": DISCRETE,
                           "accepted_values": ["OPEN", "CLOSE"]}
    
    sensors['D12'] = {"type": "cabinet",
                           "id": "wardrobe",
                           "value_type": DISCRETE,
                           "accepted_values": ["OPEN", "CLOSE"]}
					   
    # add special sensors
    sensors["AD1-A"] = {"type": "burner",
                        "id": "burner",
                        "value_type": NUMERIC,
                        "accepted_values": [0, 4]}

    sensors["AD1-B"] = {"type": "water",
                        "id": "hot",
                        "value_type": NUMERIC,
                        "accepted_values": [0, 1]}

    sensors["AD1-C"] = {"type": "water",
                        "id": "cold",
                        "value_type": NUMERIC,
                        "accepted_values": [0, 1]}

    # add phone sensor
    sensors['P01'] = {"type": "phone",
                      "id": "phone",
                      "value_type": DISCRETE,
                      "accepted_values": ["START", "END"]}

    # add temperature sensors
    for sensor_id in range(1, 4):
        id_key = 'T' + str(sensor_id).zfill(2)
        sensors[id_key] = {"type": "temperature",
                           "id": id_key,
                           "value_type": NUMERIC,
                           "accepted_values": [-10, 50]}

        # pp = pprint.PrettyPrinter(indent=1)
        # pp.pprint(sensors)


def parse_date(tokens):
    if '.' in tokens[1]:
        hour_token = ":".join(tokens[1].split(".")[:-1])
    else:
        hour_token = tokens[1]
    print("Parsing " + tokens[0] + " " + hour_token)
    return dateparser.parse(tokens[0] + ' ' + hour_token)


def unix_time_millis(dt):
    return (dt - epoch).total_seconds() * 1000.0


def safe_open(input_file_path):
    try:
        input_file = codecs.open(input_file_path, 'r', 'utf-8')
        #input_file.readline()
    except UnicodeDecodeError:
        input_file = codecs.open(input_file_path, 'r', 'utf-16')
    return input_file


def to_json(input_file_path, has_class=False):
    event_list = []
    activity_intervals = {}
    first_timestamp = None

    previous_activity_class = None
    current_activity_start = None

    with safe_open(input_file_path) as input_file:
        for line in input_file:
            tokens = line.strip().replace('\t', ' ').split(' ')
            tokens = [t for t in tokens if t != ' ' and t != '']

            # parse date
            datetime = parse_date(tokens)
            if not datetime:
                print("[ERROR] Date not correctly formatted: " + line + ". Ignoring ...")
                #sys.exit(-1)
                continue
            datetime = datetime.replace(tzinfo=pytz.UTC)

            # parse and validate event type
            event_id = tokens[2]
            if event_id not in sensors.keys():
                print("[ERROR] Sensor id not correctly formatted: " + line)
                continue

            # parse and validate event value
            event = sensors[event_id]
            event_value = tokens[3]
            if event["value_type"] is DISCRETE:
                if event_value not in event["accepted_values"]:
                    print("[ERROR] Sensor value " + event_value + " of type " + event['value_type'] +
                          " not correctly formatted: " + line + ". Ignoring ...")
                    #sys.exit(-1)
                    continue
            elif event["value_type"] is NUMERIC:
                # if float(event_value) < event["accepted_values"][0] or float(event_value) > event["accepted_values"][1]:
                #     print("[ERROR] Sensor value not correctly formatted: " + line)
                #     sys.exit(-1)
                if float(event_value) < event["accepted_values"][0]:
                    print("[ERROR] Sensor value " + event_value + " of type " + event['value_type'] +
                          " not correctly formatted: " + line +
                          ". Value less than min accepted value: " + str(event["accepted_values"][0]) + ". Ignoring ...")
                    continue
                elif float(event_value) > event["accepted_values"][1]:
                    print("[ERROR] Sensor value " + event_value + " of type " + event['value_type'] +
                          " not correctly formatted: " + line +
                          ". Value greater than max accepted value: " + str(event["accepted_values"][1]) + ". Ignoring ...")
                    continue
            else:
                print("[ERROR] Sensor value_type not correctly formatted: " + line)
                print (tokens)
                #sys.exit(-1)
                continue

            # parse and validate event class
            # if has_class:
            #     if len(tokens) == 6:
            #         event_class = tokens[4]
            #         interval_tick = tokens[5]

            #         if event_class in activity_types: 
            #             if not event_class in activity_intervals and interval_tick == "begin":
            #                 activity_intervals[event_class] = {
            #                     "start" : int(unix_time_millis(datetime))
            #                 }
            #             elif event_class in activity_intervals and interval_tick == "end":
            #                 activity_intervals[event_class].update({
            #                     "end" : int(unix_time_millis(datetime))
            #                 })
            print ("event_class = " + tokens[4])
            event_class = int(tokens[4])

            if not previous_activity_class:
              current_activity_start = int(unix_time_millis(datetime))
              previous_activity_class = event_class
              first_timestamp = int(unix_time_millis(datetime))
            else:
              if event_class != previous_activity_class:
                if previous_activity_class in activity_intervals:
                  activity_intervals[previous_activity_class].append(
                    {
                      "activity_type": interweaved_activity_map[previous_activity_class],
                      "interval" : {"start" : current_activity_start, "end": int(unix_time_millis(datetime))}
                    })
                else:
                  activity_intervals[previous_activity_class] = \
                    [{
                      "activity_type": interweaved_activity_map[previous_activity_class],
                      "interval" : {"start" : current_activity_start, "end": int(unix_time_millis(datetime))}
                    }
                    ]

                previous_activity_class = event_class
                current_activity_start = int(unix_time_millis(datetime))


            event = create_json_event(event_id, event_value, datetime)
            # event = {
            #     "event": {
            #         "event_type": event_id,
            #         "event_info": {
            #             "value": event_value,
            #             "annotations": {
            #                 "startTime": str(datetime),
            #                 "endTime": str(datetime)
            #             }
            #         }
            #     }
            # }

            event_list.append(event)
    return event_list, activity_intervals, first_timestamp


def create_json_event(event_id, event_value, dt):
    dt = dt.replace(tzinfo=pytz.UTC)
    if sensors[event_id]['type'] == "phone":
        return \
            {
                "event": {
                    "event_type": sensors[event_id]['type'],
                    "event_info": {
                        "sensorId": "phone",
                        "status": event_value,
                        "annotations": {
                            "confidence": 1,
                            "startTime": dt.isoformat(),
                            "endTime": dt.isoformat(),
                            "lastUpdated": unix_time_millis(dt)
                        }
                    }
                }
            }
    elif sensors[event_id]['type'] == "burner":
        return \
            {
                "event": {
                    "event_type": sensors[event_id]['type'],
                    "event_info": {
                        "value": float(event_value),
                        "annotations": {
                            "confidence": 1,
                            "startTime": dt.isoformat(),
                            "endTime": dt.isoformat(),
                            "lastUpdated": unix_time_millis(dt)
                        }
                    }
                }
            }
    elif sensors[event_id]['type'] == "temperature" or sensors[event_id]['type'] == "water":
        return \
            {
                "event": {
                    "event_type": sensors[event_id]['type'],
                    "event_info": {
                        "sensorId": sensors[event_id]['id'],
                        "value": float(event_value),
                        "annotations": {
                            "confidence": 1,
                            "startTime": dt.isoformat(),
                            "endTime": dt.isoformat(),
                            "lastUpdated": unix_time_millis(dt)
                        }
                    }
                }
            }
    else:
        return \
            {
                "event": {
                    "event_type": sensors[event_id]['type'],
                    "event_info": {
                        "sensorId": sensors[event_id]['id'],
                        "status": event_value,
                        "annotations": {
                            "confidence": 1,
                            "startTime": dt.isoformat(),
                            "endTime": dt.isoformat(),
                            "lastUpdated": unix_time_millis(dt)
                        }
                    }
                }
            }



def to_json_file(input_file_path, output_file_path, activity_interval_file_path, has_class=False):
    event_list, activity_intervals, start_ts = to_json(input_file_path, has_class)
    if event_list:
        with open(output_file_path, 'w') as outfile:
            json.dump(event_list, outfile)

    if activity_intervals:
        # print (activity_intervals)
        

        for k in activity_intervals.keys():
            for interval in activity_intervals[k]:
              interval["interval"].update({
                "relative_start" : interval["interval"]["start"] - start_ts,
                "relative_end" : interval["interval"]["end"] - start_ts
              })

        with open(activity_interval_file_path, 'w') as outfile:
            json.dump(activity_intervals, outfile) 


if not (len(sys.argv) == 2 or len(sys.argv) == 3):
    print("[ERROR] Input arguments not correct")
    sys.exit(-1)

build_sensors()

if os.path.isdir(sys.argv[1]):
    # extract basename (last folder in path)
    base_folder = os.path.basename(os.path.normpath(sys.argv[1]))

    # create output dir, removing previous if exists
    output_dir = "." + os.path.sep + "json_parsed" + os.path.sep + base_folder
    if os.path.isdir(output_dir):
        shutil.rmtree(output_dir)

    # create output dirs
    os.makedirs(output_dir)

    for person_dir in os.listdir(sys.argv[1]):
      full_dir = os.path.join(sys.argv[1], person_dir)
      
      print("[INFO] Parsing folder: " + full_dir)

      if os.path.isdir(full_dir):
        for file in os.listdir(full_dir):
          if "interweave" in file or "interwoven" in file:
            print("[INFO] Parsing file: " + file + " for person: " + person_dir)

            input_file_path = os.path.join(full_dir, file)
            output_file_path = os.path.join(output_dir, person_dir + "_interweaved.json")
            activity_interval_file_path = os.path.join(output_dir, person_dir + "_activity_intervals" + ".json")

            to_json_file(input_file_path, output_file_path, activity_interval_file_path, has_class = True)
            break

# elif os.path.isfile(sys.argv[1]):
#     if sys.argv[1].endswith(".stream"):
#         print("[ERROR] Will not convert .stream file")
#         sys.exit(-1)

#     if not os.path.isdir(sys.argv[2]):
#         print("[ERROR] second argument (output folder) is not a folder")
#         sys.exit(-1)

#     outputfile_file = ntpath.basename(sys.argv[1])

#     output_file_path = sys.argv[2] + outputfile_file + ".json"
#     activity_interval_file_path = sys.argv[2] + outputfile_file + "-" + "activity_intervals" + ".json"
#     # to_etalis_stream_file(sys.argv[1], output_file_path)
#     to_json_file(sys.argv[1], output_file_path, activity_interval_file_path, has_class = True)
else:
    print("[ERROR] Argument provided not file or folder")
    sys.exit(-1)
