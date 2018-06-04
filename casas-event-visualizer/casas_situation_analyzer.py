"""
This script generates a (slightly modified) Gantt chart of CONSERT's knowledge base facts.
It reads the JSON exports from CONSERT and produces an html Gantt chart using Plotly (https://plot.ly/python/). 
"""

import os, errno
import argparse
import json

import pandas as pd



__author__ = "Alex Sorici"
__maintainer__ = "Alex Sorici"
__date__ = "29/01/2018"
__version__ = "1.0.0"

situations = ["Phone_call", "Wash_hands", "Cook", "Eat", "Clean"]


# Parse arguments (input folder and output file)
parser = argparse.ArgumentParser(description='Analysis script for resutls of CONSERT event processing of CASAS activities')
parser.add_argument('--f', metavar='INPUT_FOLDER', type=str, nargs=1, required=True,
                    help='folder containing the CONSERT knowledgebase exported in JSON files (usually one per each'
                         'entrypoint')

args = parser.parse_args()
INPUT_FOLDER = args.f[0]

INPUT_FOLDER = "experiment/data-single"

# Select all JSON files (ending with .json) in INPUT_FOLDER
files = [os.path.join(INPUT_FOLDER, f) for f in os.listdir(INPUT_FOLDER) if f.endswith('.json')]

analysis_data = []


for f in files:
        with open(f, 'r') as data_file:
                data = json.load(data_file)
        person = data['person']

        for activity_data in data['activities']:
                d = {
                'person' : person,
                'activity': activity_data['name'],
                'detected': activity_data['detected'],
                }

                if activity_data['detected']:
                        d['start_diff'] = activity_data['start_diff']
                        d['end_diff'] = activity_data['end_diff']
                else:
                        d['start_diff'] = None
                        d['end_diff'] = None

                analysis_data.append(d)
        
        

df_analysis_base = pd.DataFrame(analysis_data)

print(df_analysis_base.describe())

df_detected = df_analysis_base[df_analysis_base['detected'] == True].groupby('activity')

writer = pd.ExcelWriter('casas_situation_detection_analysis.xlsx')
df_detected.describe().to_excel(writer, "Summary")
df_analysis_base.sort(['activity', 'start_diff', 'end_diff'], ascending=[1, 1, 1]).to_excel(writer, "Detailed")

writer.save()
