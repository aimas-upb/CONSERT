"""
This script generates a (slightly modified) Gantt chart of CONSERT's knowledge base facts.
It reads the JSON exports from CONSERT and produces an html Gantt chart using Plotly (https://plot.ly/python/). 
"""

import os, errno
import argparse
import json

import pandas as pd



__author__ = "David Iancu"
__maintainer__ = "David Iancu"
__date__ = "11/06/2018"
__version__ = "1.0.0"

situations =  {"1":"FillDispenser", "2":"WatchDVD", "3":"WaterPlants", "4":"PhoneCall", "5":"WriteBirthdatCard","6":"PreparingSoup", "7":"Cleaning","8":"ChoosingOutfit"};


# Parse arguments (input folder and output file)
#parser = argparse.ArgumentParser(description='Analysis script for resutls of CONSERT event processing of CASAS activities')
#parser.add_argument('--f', metavar='INPUT_FOLDER', type=str, nargs=1, required=True,
                 #   help='folder containing the CONSERT knowledgebase exported in JSON files (usually one per each'
                  #       'entrypoint')

#args = parser.parse_args()
#INPUT_FOLDER = args.f[0]

INPUT_FOLDER = "experiment/interweaved"

# Select all JSON files (ending with .json) in INPUT_FOLDER
files = [os.path.join(INPUT_FOLDER, f) for f in os.listdir(INPUT_FOLDER) if f.endswith('.json')]

analysis_data = []


for f in files:
        with open(f, 'r') as data_file:
                data = json.load(data_file)

        for i in data.keys():

            nameaux = i
            print (nameaux)
           # if nameaux == "3":
                #continue
            activity = situations[i]

            for person in data[i].keys():
                    activity_data = data[i][person]
                    d = {
                    'person' : person,
                    'activity': activity ,
                    'detected intervals': activity_data['detected intervals'],
                    'real intervals': activity_data['real number of intervals'],
                    'hit intervals': activity_data['hit intervals'],
                    'precision': activity_data['precision'],
                    'recall': activity_data['recall'],
                    'accuracy': activity_data['accuracy'],
                    'tp': activity_data['true positive time'],
                    'tn': activity_data['true negative time'],
                    'fp': activity_data['false positive time'],
                    'fn': activity_data['false negative time']
                    }
                    no = 0
                    for interv in activity_data['intervals']:
                        no = no +1
                        string1 = "delta start ";
                        string2 = "delta end " ;
                        string3 = "delta duration ";
                        if interv['delta start']!='N/A':
                            
                            d[string1] = interv['delta start']
                            d[string2] = interv['delta end']
                            d[string3] = interv['delta duration']
                        else:
                            d[string1] = None
                            d[string2] = None
                            d[string3] = None

                    analysis_data.append(d)
            
        

df_analysis_base = pd.DataFrame(analysis_data)

print(df_analysis_base.describe())

df_detected = df_analysis_base[df_analysis_base['hit intervals'] > 0 ].groupby('activity')

writer = pd.ExcelWriter('casas_interweaved_situation_detection_analysis.xlsx')
df_detected.describe().to_excel(writer, "Summary")
df_analysis_base.sort_values (['activity','detected intervals',  'hit intervals'], ascending=[1, 1, 1]).to_excel(writer, "Detailed")

writer.save()


