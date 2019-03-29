import os
import sys
import datetime, pytz
import dateparser
import numpy as np
from random import shuffle
from sklearn.svm import SVC, LinearSVC
import json
from sklearn.metrics import confusion_matrix
import matplotlib.pyplot as plt
import itertools

import rospy
from std_msgs.msg import String
from consert.msg import ContextAssertion, EventWindow, EntityRole, ContextEntity

sensors = []
n_sensors = None
discrete_sensor_value_map = {
    "ON": 1,
    "OFF": -1,
    "PRESENT": 1,
    "ABSENT": -1,
    "OPEN": 1,
    "CLOSE": -1,
    "START": 1,
    "END": -1   
}
activity_types = [
  "fill_dispenser",
  "watch_dvd",
  "water_plants",
  "answer_phone",
  "prepare_card",
  "prepare_soup",
  "clean",
  "choose_outfit"
]
n_activities = len(activity_types)

activity_name_map = {
  "fill_dispenser": "FillingDispenser",
  "watch_dvd": "WatchDVD",
  "water_plants": "WaterPlants",
  "answer_phone": "PhoneCall",
  "prepare_card": "WritingBirthdayCard",
  "prepare_soup": "PreparingSoup",
  "clean": "Cleaning",
  "choose_outfit": "ChoosingOutfit"
}
prediction_counter = 0

class SensorActivation:
    start_time = None
    end_time = None
    sensor_type = None
    sensor_id = None
    value_type = None
    value = None

    def __init__(self, start_time, end_time, sensor_type, sensor_id, value_type, value):
        self.start_time = start_time
        self.end_time = end_time
        self.sensor_type = sensor_type
        self.sensor_id = sensor_id
        self.value_type = value_type
        self.value = value

class ADL:
    start_time = None
    end_time = None
    activity_type = None

    def __init__(self, start_time, end_time, activity_type):
        self.start_time = start_time
        self.end_time = end_time
        self.activity_type = activity_type

def get_filenames():
    sa_files = []
    act_files = []
    path = "./casas_adlinterweaved"
    for filename in os.listdir(path):
        token = filename.split(".")[0].split("_")[1]
        if token == "interweaved":
            sa_files.append(path + "/" + filename)
        else:
            act_files.append(path + "/" + filename)
    return sa_files, act_files

def init_sensors():
    sensors = []
    for sensor_id in range(1, 52):
        sensors.append('M' + str(sensor_id).zfill(2))
    sensors += ["soup", "glass", "DVD", "pill_dispenser", "DVD_Player", "medicine", "pot", "address_book", "envelope"]
    sensors += ["door1", "door2", "door3", "door4", "door5", "door6", "cupboard", "freezer", "fridge", "microwave", "supplies", "wardrobe"]
    sensors += ["burner", "hot", "cold", "phone"]
    for sensor_id in range(1, 4):
        sensors.append('T' + str(sensor_id).zfill(2))
    return sensors

def parse_activity(activity_dict):
    start_time = datetime.datetime.utcfromtimestamp(activity_dict['interval']['start']/1000).replace(tzinfo=pytz.UTC)
    end_time = datetime.datetime.utcfromtimestamp(activity_dict['interval']['end']/1000).replace(tzinfo=pytz.UTC)
    activity_type = activity_dict['activity_type']
    return ADL(start_time, end_time, activity_type)

def preprocess_svc(streams):
    Xs = []
    ys = []
    for s in streams:
        features = np.zeros(n_sensors)
        for sa in s[1]:
            sensor_idx = sensors.index(sa.sensor_id)
            if sa.value_type == "DISCRETE":
                features[sensor_idx] = discrete_sensor_value_map[sa.value]
            elif sa.value_type == "NUMERIC":
                print(sa.value)
                features[sensor_idx] = sa.value
        Xs.append(features)
        ys.append(s[0].activity_type)
    Xs = np.array(Xs)
    ys = np.array(ys)
    return Xs, ys

def preprocess_svc_n_activations(streams):
    Xs = []
    ys = []
    for s in streams:
        features = np.zeros(2 * n_sensors)
        for sa in s[1]:
            sensor_idx = sensors.index(sa.sensor_id)
            if sa.value_type == "DISCRETE":
                features[2 * sensor_idx] = discrete_sensor_value_map[sa.value]
                features[2 * sensor_idx + 1] += 1
            elif sa.value_type == "NUMERIC":
                print(sa.value)
                features[2 * sensor_idx] = sa.value
                features[2 * sensor_idx + 1] += 1
        Xs.append(features)
        ys.append(s[0].activity_type)
    Xs = np.array(Xs)
    ys = np.array(ys)
    return Xs, ys

def preprocess_svc_n_activations_avg(streams):
    Xs = []
    ys = []
    for s in streams:
        features = np.zeros(2 * n_sensors)
        for sa in s[1]:
            sensor_idx = sensors.index(sa.sensor_id)
            if sa.value_type == "DISCRETE":
                features[2 * sensor_idx] += discrete_sensor_value_map[sa.value]
                features[2 * sensor_idx + 1] += 1
            elif sa.value_type == "NUMERIC":
                print(sa.value)
                features[2 * sensor_idx] += sa.value
                features[2 * sensor_idx + 1] += 1
        for idx in range(n_sensors):
            features[2 * idx] /= float(features[2 * sensor_idx + 1])
        Xs.append(features)
        ys.append(s[0].activity_type)
    Xs = np.array(Xs)
    ys = np.array(ys)
    return Xs, ys

def parse_assertions(assertions):
    features = np.zeros(2 * n_sensors)
    for assertion in assertions:
        sensor_idx = sensors.index(assertion.entities[0].entity.id)
        value = assertion.entities[1].entity.value
        if value in discrete_sensor_value_map:
            features[2 * sensor_idx] = discrete_sensor_value_map[sa.value]
            features[2 * sensor_idx + 1] += 1
        else:
            print(value)
            features[2 * sensor_idx] = value
            features[2 * sensor_idx + 1] += 1
    return features


def callback(data, args):
    publisher = args[0]
    clf = args[1]

    possible_activity = data.possibleContextAssertion.entities[0].entity.value
    assertions = data.supportingAssertions

    features = parse_assertions(assertions)
    result = clf.predict([features])[0]
    resulting_assertion = ContextAssertion()
    resulting_assertion.id = data.possibleContextAssertion.type
    resulting_assertion.type = data.possibleContextAssertion.type
    resulting_assertion.arity = 2
    resulting_assertion.acquisitionType = "DERIVED"
    resulting_assertion.entities = [EntityRole(
        role="possibleActivity",
        entity=ContextEntity(
            id=possible_activity,
            type="org.aimas.consert.model.content.StringLiteral",
            isLiteral=True,
            value=possible_activity))]
    if possible_activity == activity_name_map[result]:
        resulting_assertion.entities.append(EntityRole(
        role="status",
        entity=ContextEntity(
            id="YES",
            type="org.aimas.consert.model.content.StringLiteral",
            isLiteral=True,
            value="YES")))
    else:
        resulting_assertion.entities.append(EntityRole(
        role="status",
        entity=ContextEntity(
            id="NO",
            type="org.aimas.consert.model.content.StringLiteral",
            isLiteral=True,
            value="NO")))

    print(activity_name_map[result])
    rospy.loginfo(resulting_assertion)
    publisher.publish(resulting_assertion)

def init_ros_node(clf):
    rospy.init_node('svcWindows', anonymous=True)
    publisher = rospy.Publisher("consert/engine/insertedAssertions", ContextAssertion, queue_size=10)
    rospy.Subscriber("consert/engine/eventWindows", EventWindow, callback, (publisher, clf))
    print("ROS nodes initialised")


if __name__ == "__main__":
    sa_files, act_files = get_filenames()
    sensors = init_sensors()
    n_sensors = len(sensors)
    streams = []
    # Exclude training data from specified persons
    if len(sys.argv) > 1:
        sa_files = [fn for fn in sa_files if reduce(lambda res, p: res and (p not in fn), [True] + sys.argv[1:])]
    for sa_file in sa_files:
        # Parse sensor activations
        sensor_activations = []
        #person_streams
        with open(sa_file, "r") as f:
            events = map(lambda x: x['event'], json.loads(f.readline()))
            for event in events:
                info = event['event_info']
                anno = info['annotations']
                start_time = dateparser.parse(anno['startTime'])
                end_time = dateparser.parse(anno['endTime'])
                sensor_type = event['event_type']
                sensor_id = info['sensorId']
                if 'status' in info:
                    # discrete values
                    value_type = "DISCRETE"
                    value = info['status']
                elif 'value' in event:
                    # numeric values
                    value_type = "NUMERIC"
                    value = event['value']
                sa = SensorActivation(start_time, end_time, sensor_type, sensor_id, value_type, value)
                sensor_activations.append(sa)
    
        # Parse ADLs
        ADLs = []
        act_file = sa_file.replace("_interweaved", "_activity_intervals")
        with open(act_file, "r") as f:
            activities = json.loads(f.readline())
            for activity_id in range(1, 9):
                if str(activity_id) in activities:
                    ADLs += map(parse_activity, activities[str(activity_id)])
    
        # Split data into streams: (activity, [sensor_activations_for_activity])
        for adl in ADLs:
            afferent_sas = filter(lambda sa: sa.start_time >= adl.start_time and sa.end_time <= adl.end_time, sensor_activations)
            streams.append((adl, afferent_sas))

    # Train a SVC
    train_Xs, train_ys = preprocess_svc_n_activations(streams)
    clf = LinearSVC()
    clf.fit(train_Xs, train_ys)

    try:
        init_ros_node(clf)
        rospy.spin()
    except rospy.ROSInterruptException:
        pass

