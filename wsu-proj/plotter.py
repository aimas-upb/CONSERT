#!/usr/bin/python

import matplotlib.pyplot as plt
from matplotlib.dates import DateFormatter, MinuteLocator, SecondLocator
import numpy as np
from StringIO import StringIO
import datetime
import time
from pytz import timezone
import re

import pprint

from multiprocessing import Process
import sys

HLA_TYPE = "hla"
LLA_TYPE = "lla"
POS_TYPE = "pos"

COLORS = ['b', 'g', 'r', 'c', 'm', 'y', 'k']

def parseDate(inputDate):
    dateComponents = [int(token.strip()) for token in re.split("\(|\)|,",inputDate.strip()) if token.strip().isdigit()]
    parsedDate = datetime.datetime(*dateComponents[:-1])
    return parsedDate

def extractData(inputData):
    for line in inputData:
        if line.strip() and not line.startswith("%"):
            datimeRegex = re.compile(r"datime\(.*?\)")
            dates = datimeRegex.findall(line)
            startDate = parseDate(dates[0])
            endDate = parseDate(dates[1])
            tokens = [rawToken.strip() for rawToken in re.split('[(),\[\]]',line.strip()) if rawToken.strip()]
            # print tokens
            if tokens:
                extracted = tokens[1:4]
                extracted.append(str((startDate - datetime.datetime(1970,1,1)).total_seconds()))
                extracted.append(str((endDate - datetime.datetime(1970,1,1)).total_seconds()))
                yield ','.join(extracted)+"\n"

def timelines(y, xstart, xstop, color='b'):
    """Plot timelines at y from xstart to xstop with given color."""   
    plt.hlines(y, xstart, xstop, color, lw=4)
    plt.scatter(xstart,y,s=100,c=color,marker=".",lw=2,edgecolor=color)
    plt.scatter(xstop,y,s=100,c=color,marker=".",lw=2,edgecolor=color)
    plt.xticks(np.arange(min(xstart), max(xstop)+1, 5.0))

def plotEventStream(inputStream, title):
    # data = np.genfromtxt(extractData(inputStream), 
    #     names=['input_type', 'user', 'input_value', 'last_update', 'confidence', 'start_time', 'end_time'], dtype=None, delimiter=',')
    # input_type, user, input_value, last_update, confidence, start_time, end_time = data['input_type'], data['user'], data['input_value'], data['last_update'], data['confidence'], data['start_time'], data['end_time']

    pp = pprint.PrettyPrinter(indent=1)

    data = np.genfromtxt(extractData(inputStream), 
        names=['input_type', 'input_id', 'input_value', 'start_time', 'end_time'], dtype=None, delimiter=',')
    input_type, input_id, input_value, start_time, end_time = data['input_type'], data['input_id'], data['input_value'], data['start_time'], data['end_time']

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
    user_input_types, indices = np.unique(input_combos[['input_type', 'input_id']], return_inverse = True)

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
        sorted_type_instances = sorted(type_instances, key = lambda input: float(input['start_time']), reverse=False)

        # check data type and group
        pp.pprint(sorted_type_instances)
        sensor_type = sorted_type_instances[0][0]
        sensor_values = {}
        sensor_values['motion'] = { "left" : "ON", "right" : "OFF" }
        sensor_values['item'] = { "left" : "ABSENT", "right" : "PRESENT" }
        sensor_values['cabinet'] = { "left" : "OPEN", "right" : "CLOSE" }
        sensor_values['phone'] = { "left" : "START", "right" : "END" }
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

    colorMap = {}
    colorMap['pos'] = 'r'
    colorMap['lla'] = 'b'
    colorMap['hla'] = 'g'

    standardMarkers = ['o']#,'*','^']

    xticks = []
    yticks = []
    y = 1
    sorted_keys = sorted(unique_type_instances.keys(),reverse=True)
    for key in sorted_keys:
        # yticks are the same as the input type keys
        yticks.append(key)

        unique_type_instances[key] = sorted(unique_type_instances[key], key = lambda x: float(x['start_time']))

        color_idx = 0
        for instance in unique_type_instances[key]:
            # xticks are start_time and end_time timestamps
            start = float(instance['start_time'])# - min_start_time
            end = float(instance['end_time'])# - min_start_time
            xticks.append(start)
            xticks.append(end)

            plt.hlines(y, start, end, COLORS[(color_idx - 1) % len(COLORS)], lw = 2)
            
            plt.scatter([start, end], [y, y], marker=standardMarkers[(color_idx - 1) % len(standardMarkers)])

            color_idx += 1

        y += 1

    xticks = sorted(xticks)

    plt.xticks(xticks, rotation=90,)
    plt.ylim(0, y)
    plt.yticks(range(1, y + 1), yticks)
    plt.xlabel('Time')

    delta = (xticks[-1] - xticks[0]) / 20
    plt.xlim(xticks[0] - delta, xticks[-1] + delta)

    plt.title(title)
    plt.show()

f = open(sys.argv[1])
p = Process(target=plotEventStream,args=([f,sys.argv[1]]))
p.start()
p.join()