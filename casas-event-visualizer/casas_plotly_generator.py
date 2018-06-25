"""
This script generates a (slightly modified) Gantt chart of CONSERT's knowledge base facts.
It reads the JSON exports from CONSERT and produces an html Gantt chart using Plotly (https://plot.ly/python/). 
"""

import os, errno
import argparse
import json
from copy import copy

import pandas as pd
from pandas.io.json import json_normalize

import plotly
from plotly import tools
import plotly.figure_factory as ff
import plotly.graph_objs as go

import plotly.plotly as py
import plotly.graph_objs as go

__author__ = "Mihai Trascau"
__maintainer__ = "Mihai Trascau"
__date__ = "23/05/2017"
__version__ = "1.0.0"

raw_event_types = ["MOTION", "ITEM", "CABINET", "PHONE", "WATER", "BURNER", "TEMPERATURE", "SuppliesRetrieved"]
status_streams = ["MotionStream", "ItemStream", "CabinetStream", "PhoneStream", "SuppliesRetrievedStream"]
numeric_streams = ["WaterStream", "BurnerStream", "TemperatureStream"]

status_mapping = {
    "MotionStream" : {
        "start": "ON",
        "stop" : "OFF"
    },
    "ItemStream" : {
        "start": "ABSENT",
        "stop" : "PRESENT"
    },
    "CabinetStream" : {
        "start": "OPEN",
        "stop" : "CLOSED"
    },
    "PhoneStream" : {
        "start": "START",
        "stop" : "END"
    },
    "SuppliesRetrievedStream" : {
        "start": "YES",
        "stop" : "NO"
    },
}

def process_casas_raw_output(raw_data):
    interval_maps = {}
    processed_status_events = []
    processed_numeric_events = []

    ## sort raw_data by startTime
    sorted_data = sorted(raw_data, key = lambda k : k['annotations']['startTime'])

    earliest_startTime = sorted_data[0]['annotations']['startTime']
    latest_endTime = sorted_data[-1]['annotations']['endTime']

    for event_dump in sorted_data:
        stream_name = event_dump['streamName']
        sensor_id = event_dump['sensorId']

        if stream_name in status_streams:
            # if we are handling a status sensor
            if event_dump['status'] == status_mapping[stream_name]['start']:
                if not sensor_id in interval_maps:
                    # if there is no entry for this sensor in the interval_map, add one
                    event_copy = copy(event_dump)
                    interval_maps[sensor_id] = event_copy
                else:
                    """ There is already an entry. Since our current event is still a START one, it means
                        that for the one in the map there is no matching end. Thus we consider the event in
                        the map a short firing and replace it with the current event
                    """
                    prev_event = interval_maps[sensor_id]
                    prev_event['annotations']['endTime'] = prev_event['annotations']['startTime'] + 500    # add one second to the endTime to make it visible on the chart
                    processed_status_events.append(prev_event)

                    event_copy = copy(event_dump)
                    interval_maps[sensor_id] = event_copy
            else:
                if sensor_id in interval_maps:
                    """ If there is an entry, it must be one for event START for which we have found the matching END.
                        We therefore remove the existing entry and insert a corresponding interval event
                        in the processed_status_events list.
                    """
                    prev_event = interval_maps[sensor_id]
                    prev_event['annotations']['endTime'] = event_dump['annotations']['startTime']

                    processed_status_events.append(prev_event)
                    del interval_maps[sensor_id]
                else:
                    # if there is an END event with no matching START, just ignore the END event
                    event_copy = copy(event_dump)
                    event_copy['annotations']['startTime'] = earliest_startTime
                    processed_status_events.append(event_copy)

        else:
            # if we are handling a numeric sensor, just append it to the processed_numeric_events list
            processed_numeric_events.append(event_dump)

    for sensor_id, ev in interval_maps.items():
        ev['annotations']['endTime'] = latest_endTime
        processed_status_events.append(ev)

    return processed_status_events, processed_numeric_events


# Parse arguments (input folder and output file)
parser = argparse.ArgumentParser(description='Visualization script built with Plotly for CONSERT event processing')
parser.add_argument('--f', metavar='INPUT_FOLDER', type=str, nargs=1, required=True,
                    help='folder containing the CONSERT knowledgebase exported in JSON files (usually one per each'
                         'entrypoint')
parser.add_argument('--p', metavar='CASAS_PERSON', type=str, nargs=1, required = True,
                    help='The person executing the task for which the event visualization is being generated.')
parser.add_argument('--o', metavar='OUTPUT_HTML_FOLDER', type=str, nargs=1, default=['outputs'],
                    help='output html file containing the plot')



args = parser.parse_args()
INPUT_FOLDER = args.f[0]
OUTPUT_FOLDER = args.o[0]
PERSON = args.p[0]

# Filter all JSON files (ending with .json) in INPUT_FOLDER
files = [os.path.join(INPUT_FOLDER, f) for f in os.listdir(INPUT_FOLDER) if f.endswith('.json')]

OUTPUT_HTML_STATUS_SENSORS = OUTPUT_FOLDER + os.path.sep + PERSON + "-status-sensor-visualizer.html"
OUTPUT_HTML_NUMERIC_SENSORS = OUTPUT_FOLDER + os.path.sep + PERSON + "-numeric-sensor-visualizer.html"

OUTPUT_HTML_PRECISION_METRICS = OUTPUT_FOLDER + os.path.sep + PERSON + "-precision-metrics-visualizer.html"
OUTPUT_HTML_DELAY_METRICS = OUTPUT_FOLDER + os.path.sep + PERSON + "-delay-metrics-visualizer.html"

# Create folder paths if they do not exist
if not os.path.exists(os.path.dirname(OUTPUT_HTML_STATUS_SENSORS)):
    try:
        os.makedirs(os.path.dirname(OUTPUT_HTML_STATUS_SENSORS))
    except OSError as exc: # Guard against race condition
        if exc.errno != errno.EEXIST:
            raise

# Collect all data from JSON files into a single pandas dataframe
all_status_frames = []
all_numeric_frames = []

session_data = []

for f in files:
    with open(f, 'r') as data_file:
        key = ''
        if f.lower().find('burner') != -1:
            key = 'BURNER'
        elif f.lower().find('cabinet') != -1:
            key = 'CABINET'
        elif f.lower().find('cleaning') != -1:
            key = 'CLEANING'
        elif f.lower().find('cooking') != -1:
            key = 'COOKING'
        elif f.lower().find('eating') != -1:
            key = 'EATING'
        elif f.lower().find('handlingfood') != -1:
            key = 'HandlingFood'
        elif f.lower().find('heatingfood') != -1:
            key = 'HeatingFood'
        elif f.lower().find('item') != -1:
            key = 'ITEM'
        elif f.lower().find('motion') != -1:
            key = 'MOTION'
        elif f.lower().find('personlocation') != -1:
            key = 'PersonLocation'
        elif f.lower().find('phonecall') != -1:
            key = 'PHONECALL'
        elif f.lower().find('phone') != -1:
            key = 'PHONE'
        elif f.lower().find('probablycleaning') != -1:
            key = 'ProbablyCleaning'
        elif f.lower().find('temperature') != -1:
            key = 'TEMPERATURE'
        elif f.lower().find('washhands') != -1:
            key = 'WashHands'
        elif f.lower().find('waterplants') != -1:
            key = 'WaterPlants'
        elif f.lower().find('water') != -1:
            key = 'WATER'
        elif f.lower().find('watchdvd') != -1:
            key = 'WatchDVD'
        elif f.lower().find('preparingsoup') != -1:
            key = 'PreparingSoup'
        elif f.lower().find('choosingoutfit') != -1:
            key = 'ChoosingOutfit'
        elif f.lower().find('filldispenser') != -1:
            key = 'FillDispenser'
        elif f.lower().find('writebirthdaycard') != -1:
            key = 'WriteBirthdayCard'
        elif f.lower().find('suppliesretrieved') != -1:
            key = 'SuppliesRetrieved'
        elif f.lower().find('personmoving') != -1:
            key = 'PersonMoving'

        else:
            # print 'Skipping JSON file \'' + f + '\''
            continue

        raw_data = json.load(data_file)
        for d in raw_data:
            d.update({"Event Type" : key})

        session_data += raw_data

base_session_data = [elem for elem in session_data if elem["Event Type"] in raw_event_types]
derived_session_data = [elem for elem in session_data if not elem["Event Type"] in raw_event_types]

processed_status_data, processed_numeric_data = process_casas_raw_output(base_session_data)

# if processed_status_data:
#     partial_status_frame = json_normalize(processed_status_data)
#     partial_status_frame['Event Type'] = key
#     all_status_frames.append(partial_status_frame)
#
# elif processed_numeric_data:
#     partial_numeric_frame = json_normalize(processed_numeric_data)
#     partial_numeric_frame['Event Type'] = key
#     all_numeric_frames.append(partial_numeric_frame)

""" PROCESS THE BASE STATUS SENSORS """
# df_status_base = pd.concat(all_status_frames)
df_status_base = json_normalize(processed_status_data)

# Keep only the needed columns for status sensors
df_status_base = df_status_base[['sensorId', 'annotations.startTime', 'annotations.endTime', 'Event Type']]
#df_status_base = df_status_base[['entities.hasSubject.entityId', 'entities.hasEntity.entityId', 'annotations.startTime', 'annotations.endTime', 'Event Type']]

# Convert annotations to datetime from UNIX timestamps (dtype int64)
df_status_base['annotations.startTime'] = pd.to_datetime(df_status_base['annotations.startTime'], unit='ms')
df_status_base['annotations.endTime'] = pd.to_datetime(df_status_base['annotations.endTime'], unit='ms')

# Rename columns to match plotly conventions
df_status_base.rename(columns={'sensorId': 'Task', 'annotations.startTime': 'Start', 'annotations.endTime': 'Finish'}, inplace=True)

# Sort dataframe so we get HLAs in the lower part of the Gantt
df_status_base.sort_values(by="Event Type", ascending=False, inplace=True)
df_status_base.reset_index(inplace=True)

""" PROCESS THE DERIVED STATUS SENSORS """
def fun(d):
    if 'hasEntity' in d['entities']:
        return d['entities']['hasEntity']['entityId']
    else:
        return d['entities']['hasSubject']['entityId']

derived_session_data = [dict(d, entityId = fun(d)) for d in derived_session_data]
df_status_derived = json_normalize(derived_session_data)

df_status_derived = df_status_derived[['entityId', 'annotations.startTime', 'annotations.endTime', 'Event Type']]
df_status_derived['annotations.startTime'] = pd.to_datetime(df_status_derived['annotations.startTime'], unit='ms')
df_status_derived['annotations.endTime'] = pd.to_datetime(df_status_derived['annotations.endTime'], unit='ms')
df_status_derived.rename(columns={'entityId': 'Task', 'annotations.startTime': 'Start', 'annotations.endTime': 'Finish'}, inplace=True)
df_status_derived.sort_values(by="Event Type", ascending=False, inplace=True)
df_status_derived.reset_index(inplace=True)

df_status = pd.concat([df_status_base, df_status_derived], ignore_index = True)


""" PROCESS THE DETECTION RESULT METRICS """
metrics_filename = OUTPUT_FOLDER + os.path.sep + PERSON + "-detection-metrics" + ".json"
with open(metrics_filename, 'r') as metrics_file:
    metrics_data = json.load(metrics_file)

    precision_plot_data = []
    delay_plot_data = []

    precision_metrics = ["precision", "recall", "accuracy", "hitRate"]
    delay_metrics = ["maxStartDelay", "maxEndDelay"]

    for metric in precision_metrics:
        x = []
        y = []

        for activity_name in metrics_data['metrics']:
            activity_metrics = metrics_data['metrics'][activity_name]

            x.append(activity_name)
            y.append(activity_metrics[metric])

        trace = go.Bar(
            x=x,
            y=y,
            name=metric
        )

        precision_plot_data.append(trace)

    for metric in delay_metrics:
        x = []
        y = []

        for activity_name in metrics_data['metrics']:
            activity_metrics = metrics_data['metrics'][activity_name]

            x.append(activity_name)
            y.append(activity_metrics[metric])

        trace = go.Bar(
            x=x,
            y=y,
            name=metric
        )

        delay_plot_data.append(trace)


    layout = go.Layout(
        barmode = "group"
    )

    fig_precision = go.Figure(data = precision_plot_data, layout = layout)
    fig_delay = go.Figure(data = delay_plot_data, layout = layout)

    plotly.offline.plot(fig_precision, filename=OUTPUT_HTML_PRECISION_METRICS, auto_open=False)
    plotly.offline.plot(fig_delay, filename=OUTPUT_HTML_DELAY_METRICS, auto_open=False)


# Set color map for different event types
colors = {
    'BURNER': 'rgb(0, 220, 0)',	
    'CABINET': 'rgb(220, 0, 0)',
	'CLEANING':  'rgb(46, 45, 3)',
	'COOKING':  'rgb(0, 169, 0)',
	'EATING':  'rgb(0, 169, 0)',
	'HandlingFood':  'rgb(0, 169, 0)',
	'HeatingFood':  'rgb(0, 169, 0)',
	'ITEM': 'rgb(169, 169, 169)',
	'MOTION': 'rgb(211, 211, 211)',
	'PersonLocation': 'rgb(0, 169, 0)',
	'PHONE': 'rgb(0, 220, 220)',
	'PHONECALL':  'rgb(0, 169, 0)',
	'ProbablyCleaning':  'rgb(0, 169, 0)',
	'TEMPERATURE': 'rgb(220, 0, 220)',
	'WashHands':  'rgb(0, 169, 0)',
	'WATER': 'rgb(0, 0, 220)',
    'WatchDVD': 'rgb(0, 220, 220)',
    'FillDispenser': 'rgb(0, 0, 220)',
    'WriteBirthdayCard': 'rgb(30, 127, 100)',
    'PreparingSoup': 'rgb(0,0,220)',
    'WaterPlants': 'rgb(100,12,32)',
    'SuppliesRetrieved' : 'rgb(100,203,32)',
    'ChoosingOutfit': 'rgb(255,0,0)',
    'PersonMoving' : 'rgb(221, 97, 10)'
}

# Create Gantt figure
gant_chart = ff.create_gantt(df_status, colors=colors, index_col='Event Type', title='CASAS Scenario',
                      showgrid_y=True, show_colorbar=True, group_tasks=True)

# Tilt y labels 45 degrees (due to them being too long to display correctly)
gant_chart['layout']['yaxis']['tickangle'] = 45

plotly.offline.plot(gant_chart, filename=OUTPUT_HTML_STATUS_SENSORS, auto_open=False)

#data = [gant_chart['data'][0]]


""" Process the numeric sensors if there are any entries """
# if all_numeric_frames:
if processed_numeric_data:

    # df_numeric = pd.concat(all_numeric_frames)
    df_numeric = json_normalize(processed_numeric_data)

    # Keep only the needed columns for numeric sensors
    df_numeric = df_numeric[['sensorId', 'value', 'annotations.startTime', 'Event Type']]

    # Convert annotations to datetime from UNIX timestamps (dtype int64)
    df_numeric['annotations.startTime'] = pd.to_datetime(df_numeric['annotations.startTime'], unit='ms')
    df_numeric.rename(columns={'annotations.startTime': 'Start'}, inplace=True)

    data = []
    for event_type in ["WATER", "BURNER", "TEMPERATURE"]:
        df_numeric_type = df_numeric[df_numeric["Event Type"] == event_type]

        if not df_numeric_type.empty:
            numeric_chart = go.Bar(
                x = df_numeric_type['Start'], y = df_numeric['value'], name = event_type, text = df_numeric_type['sensorId']
            )

            data.append(numeric_chart)

    nrows = len(data)
    titles = []
    if nrows > 1:
        titles = titles + [e.name + " Sensor" for e in data[1:]]

    fig = tools.make_subplots(rows=nrows, cols=1,
                              subplot_titles=titles, shared_xaxes=True, shared_yaxes=False)

    for i in range(len(data)):
        fig.append_trace(data[nrows - i - 1], (i + 1), 1)

    fig['layout'].update(height=600, width = 900)

    plotly.offline.plot(fig, filename=OUTPUT_HTML_NUMERIC_SENSORS, auto_open=False)

    # fig['data'].append({
    #     'x': df_numeric['Start'], 'y': df_numeric['value'], 'type' : 'bar', 'name': df_numeric['sensorId']
    # }, 2, 1)



#fig['layout'].update(gant_chart['layout'])
