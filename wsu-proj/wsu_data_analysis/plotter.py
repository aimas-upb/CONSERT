#!/usr/bin/python

import matplotlib.pyplot as plt
from matplotlib.dates import DateFormatter, MinuteLocator, SecondLocator
import numpy as np
from StringIO import StringIO
import datetime
import time
from pytz import timezone
import re
import os

import pprint

from multiprocessing import Process
import sys

HLA_TYPE = "hla"
LLA_TYPE = "lla"
POS_TYPE = "pos"

COLORS = ['b', 'g', 'r', 'c', 'm', 'y', 'k']


def parseDate(inputDate):
    date_components = [int(token.strip()) for token in re.split("\(|\)|,", inputDate.strip()) if
                       token.strip().isdigit()]
    parsed_date = datetime.datetime(*date_components[:-1])
    return parsed_date


def extract_data(inputData):
    for line in inputData:
        if line.strip() and not line.startswith("%"):
            datime_regex = re.compile(r"datime\(.*?\)")
            dates = datime_regex.findall(line)
            start_date = parseDate(dates[0])
            end_date = parseDate(dates[1])
            tokens = [rawToken.strip() for rawToken in re.split('[(),\[\]]', line.strip()) if rawToken.strip()]
            # print tokens
            if tokens:
                extracted = tokens[1:4]
                extracted.append(str((start_date - datetime.datetime(1970, 1, 1)).total_seconds()))
                extracted.append(str((end_date - datetime.datetime(1970, 1, 1)).total_seconds()))
                yield ','.join(extracted) + "\n"


def timelines(y, xstart, xstop, color='b'):
    """Plot timelines at y from xstart to xstop with given color."""
    plt.hlines(y, xstart, xstop, color, lw=4)
    plt.scatter(xstart, y, s=100, c=color, marker=".", lw=2, edgecolor=color)
    plt.scatter(xstop, y, s=100, c=color, marker=".", lw=2, edgecolor=color)
    plt.xticks(np.arange(min(xstart), max(xstop) + 1, 5.0))


def _filter_outliers(input_stream):
    motion_stream = filter(lambda x: x['input_type'] == 'motion', input_stream)
    last_on_items = {}
    items_to_filter = []
    for item in motion_stream:
        if item['input_value'] == 'ON':
            if item['input_id'] in last_on_items:
                items_to_filter.append(last_on_items[item['input_id']])
            last_on_items[item['input_id']] = item

        elif item['input_value'] == 'OFF':
            if item['input_id'] in last_on_items:
                on_item = last_on_items[item['input_id']]
                if on_item['start_time'] == item['start_time']:
                    items_to_filter.append(on_item)
                last_on_items.pop(item['input_id'], None)
    print items_to_filter
    for item_to_remove in items_to_filter:
        motion_stream.remove(item_to_remove)
    return motion_stream


def plot_event_stream(inputStream, title):
    # data = np.genfromtxt(extractData(inputStream),
    #     names=['input_type', 'user', 'input_value', 'last_update', 'confidence', 'start_time', 'end_time'], dtype=None, delimiter=',')
    # input_type, user, input_value, last_update, confidence, start_time, end_time = data['input_type'], data['user'], data['input_value'], data['last_update'], data['confidence'], data['start_time'], data['end_time']

    pp = pprint.PrettyPrinter(indent=1)

    data = np.genfromtxt(extract_data(inputStream),
                         names=['input_type', 'input_id', 'input_value', 'start_time', 'end_time'], dtype=None,
                         delimiter=',')
    for item in data:
        if item['input_id'] == '17':
            print item
    print
    data = np.array(_filter_outliers(data))
    for item in data:
        print item
    input_type, input_id, input_value, start_time, end_time = [data['input_type'], data['input_id'],
                                                               data['input_value'], data['start_time'],
                                                               data['end_time']]
    # data = np.genfromtxt(extractData(inputStream),
    #     names=['input_type', 'user', 'input_value', 'last_update', 'confidence'], dtype=None, delimiter=',')
    # input_type, user, input_value, last_update, confidence = data['input_type'], data['user'], data['input_value'], data['last_update'], data['confidence']


    # input_combos = data[['input_type','input_value']]
    # input_combos = data[['input_type','input_value', 'start_time', 'end_time']]

    # input_combos = data[['input_type', 'user', 'input_value', 'last_update', 'confidence', 'start_time', 'end_time']]
    input_combos = data[['input_type', 'input_id', 'input_value', 'start_time', 'end_time']]
    # input_types, unique_idx, input_type_markers = np.unique(input_combos, 1, 1)
    # y = (input_type_markers + 1) / float(len(input_types) + 1)



    # get positions of unique input_types
    user_input_types, indices = np.unique(input_combos[['input_type', 'input_id']], return_inverse=True)

    # create reverse dict from unique input type index to indices in original array where the unique input type appears
    index_groups = {}
    for i in range(len(indices)):
        if indices[i] in index_groups:
            index_groups[indices[i]].append(i)
        else:
            index_groups[indices[i]] = [i]

    unique_type_instances = {}
    min_start_time = None

    for unique_idx in index_groups:
        # for each unique input type
        type_instances = input_combos[index_groups[unique_idx]]

        # sort entries
        sorted_type_instances = sorted(type_instances, key=lambda input: float(input['start_time']), reverse=False)

        # check data type and group
        # pp.pprint(sorted_type_instances)
        sensor_type = sorted_type_instances[0][0]
        sensor_values = {
            'motion': {"left": "ON", "right": "OFF"},
            'item': {"left": "ABSENT", "right": "PRESENT"},
            'cabinet': {"left": "OPEN", "right": "CLOSE"},
            'phone': {"left": "START", "right": "END"}
        }
        if sensor_type in sensor_values.keys():
            event_idx = 0
            event_idx_to_remove = []
            while event_idx < len(sorted_type_instances) - 1:
                if sorted_type_instances[event_idx][2] == sensor_values[sensor_type]["left"]:
                    if sorted_type_instances[event_idx + 1][2] == sensor_values[sensor_type]["right"]:
                        sorted_type_instances[event_idx][4] = sorted_type_instances[event_idx + 1][3]
                        event_idx_to_remove.append(event_idx + 1)
                        event_idx += 2
                    else:
                        event_idx += 1
                else:
                    event_idx += 1
            sorted_del_idx = sorted(event_idx_to_remove, reverse=True)
            for del_idx in sorted_del_idx:
                del sorted_type_instances[del_idx]

        # create an input type key (which excludes the start times, i.e. all
        # separate instances for the same input_type, user, input_value combination
        # will be included in this list
        key = type_instances[0]['input_type'] + " [ " + str(type_instances[0]['input_id']) + "]"
        if key in unique_type_instances:
            unique_type_instances[key].append(type_instances)
        else:
            unique_type_instances[key] = type_instances

    # pp.pprint(unique_type_instances)
    # print unique_type_instances

    ## draw the plot
    ax = plt.gca()
    color_map = {'pos': 'r', 'lla': 'b', 'hla': 'g'}
    standardMarkers = ['o']  # ,'*','^']

    xticks = []
    yticks = []
    y = 1
    sorted_keys = sorted(unique_type_instances.keys(), reverse=True)
    for key in sorted_keys:
        # yticks are the same as the input type keys
        yticks.append(key)

        unique_type_instances[key] = sorted(unique_type_instances[key], key=lambda x: float(x['start_time']))

        color_idx = 0
        for instance in unique_type_instances[key]:
            # xticks are start_time and end_time timestamps
            start = float(instance['start_time'])  # - min_start_time
            end = float(instance['end_time'])  # - min_start_time
            xticks.append(start)
            xticks.append(end)

            plt.hlines(y, start, end, COLORS[(color_idx - 1) % len(COLORS)], lw=2)

            plt.scatter([start, end], [y, y], marker=standardMarkers[(color_idx - 1) % len(standardMarkers)])

            color_idx += 1

        y += 1

    xticks = sorted(xticks)

    plt.xticks(xticks, rotation=90, )
    plt.ylim(0, y)
    plt.yticks(range(1, y + 1), yticks)
    plt.xlabel('Time')

    delta = (xticks[-1] - xticks[0]) / 20
    plt.xlim(xticks[0] - delta, xticks[-1] + delta)

    plt.title(title)
    plt.show()


if os.path.isdir(sys.argv[1]):
    processes = []
    for filename in os.listdir(sys.argv[1]):
        if filename.endswith(".stream"):
            filename = os.path.join(sys.argv[1], filename)
            f = open(filename)
            p = Process(target=plot_event_stream, args=([f, filename]))
            p.start()
            processes.append(p)
    for p in processes:
        p.join()
else:
    f = open(sys.argv[1])
    p = Process(target=plot_event_stream, args=([f, sys.argv[1]]))
    p.start()
    p.join()
