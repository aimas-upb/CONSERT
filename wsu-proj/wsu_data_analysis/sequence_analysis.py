import sys
import os
import collections
import numpy as np
import etalis_stream_reader
import difflib
import ordered_set

from fastdtw import fastdtw

_sensor_interval_limits = {
    'motion': {"left": "ON", "right": "OFF"},
    'item': {"left": "ABSENT", "right": "PRESENT"},
    'cabinet': {"left": "OPEN", "right": "CLOSE"},
    'phone': {"left": "START", "right": "END"}
}

_sensor_filter_out = [
    ('motion', "OFF"),
    ('item', "PRESENT"),
    ('cabinet', "CLOSE"),
    ('phone', "END")
]


def _filter_outliers(input_stream):
    motion_stream = filter(lambda x: x, input_stream)
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
    for item_to_remove in items_to_filter:
        motion_stream.remove(item_to_remove)
    return motion_stream


def _filter_events(input_stream):
    # load data
    data = np.genfromtxt(etalis_stream_reader.parse_stream(input_stream),
                         names=['input_type', 'input_id', 'input_value', 'start_time', 'end_time'], dtype=None,
                         delimiter=',')

    # filter outliers
    data = _filter_outliers(data)

    # filter end events
    filtered_data = np.array(filter(lambda x: (x['input_type'], x['input_value']) not in _sensor_filter_out, data))

    # encode
    event_id = filtered_data[['input_type', 'input_id']]
    symbol_sequence = []
    for item in event_id:
        symbol_sequence.append(item['input_type'][0] + str(item['input_id']))
    print "Symbol sequence: ", symbol_sequence
    return symbol_sequence


def _recode_to_unique_id(input_arrays):
    index_set = ordered_set.OrderedSet(_flatten(input_arrays))
    print "Unique events: ", index_set
    encoded_sequences = []
    for sequence in input_arrays:
        encoded_sequence = ""
        for item in sequence:
            encoded_sequence += str(index_set.index(item))
        encoded_sequences.append(encoded_sequence)
    return encoded_sequences


def _measure_distances(input_sequences):
    ratio_scores = []
    quick_ratio_scores = []
    for x in range(0, len(input_sequences)-1):
        for y in range(x+1, len(input_sequences)):
            s = difflib.SequenceMatcher(None, input_sequences[x], input_sequences[y])
            ratio = s.ratio()
            ratio_scores.append(ratio)
            quick_ratio = s.quick_ratio()
            quick_ratio_scores.append(quick_ratio)
            # print "SequenceMatcher ratio:\t\t\t\t", ratio
            # print "SequenceMatcher quick_ratio:\t\t", quick_ratio

    print "\nMean quick ratio:\t", np.mean(quick_ratio_scores)
    print "Mean ratio:\t\t\t", np.mean(ratio_scores)
    print "Mean std:\t\t\t", np.std(ratio_scores)
    return ratio_scores


def _flatten(x):
    if isinstance(x, collections.Iterable) and not isinstance(x, basestring):
        return [a for i in x for a in _flatten(i)]
    else:
        return [x]

if os.path.isdir(sys.argv[1]):
    processes = []
    all_sequences = []
    for filename in os.listdir(sys.argv[1]):
        if filename.endswith(".stream"):
            filename = os.path.join(sys.argv[1], filename)
            f = open(filename)
            all_sequences.append(_filter_events(f))
    recoded_sequences = _recode_to_unique_id(all_sequences)
    _measure_distances(recoded_sequences)

else:
    f = open(sys.argv[1])
    print f
    _filter_events(f)
