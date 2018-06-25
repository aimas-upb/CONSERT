"""
This script generates a (slightly modified) Gantt chart of CONSERT's knowledge base facts.
It reads the JSON exports from CONSERT and produces an html Gantt chart using Plotly (https://plot.ly/python/). 
"""

import os, errno
import argparse
import json

import pandas as pd

import plotly
import plotly.graph_objs as go
import matplotlib.pyplot as plt



__author__ = "David Iancu"
__maintainer__ = "David Iancu"
__date__ = "11/06/2018"
__version__ = "1.0.0"

situations =  {
    "1":"FillDispenser",
    "2":"WatchDVD",
    "3":"WaterPlants",
    "4":"PhoneCall",
    "5":"WriteBirthdatCard",
    "6":"PreparingSoup",
    "7":"Cleaning",
    "8":"ChoosingOutfit"
}


# Parse arguments (input folder and output file)
#parser = argparse.ArgumentParser(description='Analysis script for resutls of CONSERT event processing of CASAS activities')
#parser.add_argument('--f', metavar='INPUT_FOLDER', type=str, nargs=1, required=True,
                 #   help='folder containing the CONSERT knowledgebase exported in JSON files (usually one per each'
                  #       'entrypoint')

#args = parser.parse_args()
#INPUT_FOLDER = args.f[0]

INPUT_FOLDER = "./experiment/interweaved"

# Select all JSON files (ending with .json) in INPUT_FOLDER
files = [os.path.join(INPUT_FOLDER, f) for f in os.listdir(INPUT_FOLDER) if f.endswith('.json')]

precision_metrics = ["precision", "recall", "hitRate"]
delay_metrics = ["maxStartDelay", "maxEndDelay"]

analysis_data = []
average_precision_metrics = {}
average_delay_metrics = {}

for f in files:
    with open(f, 'r') as data_file:
        data = json.load(data_file)


        for activity_name in data['metrics']:
            d = {
                'person': data['person']
            }
            d['activity'] = activity_name

            if activity_name in average_precision_metrics:
                for metric in precision_metrics:
                    d[metric] = data['metrics'][activity_name][metric]
                    average_precision_metrics[activity_name][metric] += data['metrics'][activity_name][metric]

                for metric in delay_metrics:
                    d[metric] = data['metrics'][activity_name][metric]
                    average_delay_metrics[activity_name][metric] += data['metrics'][activity_name][metric] / 1000.0
            else:
                average_precision_metrics[activity_name] = {}
                average_delay_metrics[activity_name] = {}

                for metric in precision_metrics:
                    d[metric] = data['metrics'][activity_name][metric]
                    average_precision_metrics[activity_name][metric] = data['metrics'][activity_name][metric]

                for metric in delay_metrics:
                    d[metric] = data['metrics'][activity_name][metric]
                    average_delay_metrics[activity_name][metric] = data['metrics'][activity_name][metric] / 1000.0

            analysis_data.append(d)

person_count = len(files)
for activity_name in average_precision_metrics:
    for metric in precision_metrics:
        average_precision_metrics[activity_name][metric] /= person_count

    for metric in delay_metrics:
        average_delay_metrics[activity_name][metric] /= person_count


precision_plot_data = []
delay_plot_data = []

for metric in precision_metrics:
    x = []
    y = []

    for activity_name in average_precision_metrics:
        activity_metrics = average_precision_metrics[activity_name]

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

    for activity_name in average_delay_metrics:
        activity_metrics = average_delay_metrics[activity_name]

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

plotly.offline.plot(fig_precision, filename="casas_interweaved_avg_precision_results.html", auto_open=False)
plotly.offline.plot(fig_delay, filename="casas_interweaved_avg_delay_results.html", auto_open=False)

df_analysis_base = pd.DataFrame(analysis_data)

plt.suptitle('')
plt.xticks(rotation=70)

prec_fig, prec_ax = plt.subplots()
prec_ax.set_ylim([-0.5,1.5])

df_analysis_base.boxplot(column = "precision", by = "activity", ax = prec_ax)
prec_fig.show()

recall_fig, recall_ax = plt.subplots()
df_analysis_base.boxplot(column = "recall", by = "activity", ax = recall_ax)

hitRate_fig, hitRate_ax = plt.subplots()
df_analysis_base.boxplot(column = "hitRate", by = "activity", ax = hitRate_ax)

raw_input("Press Enter to terminate...")

# for f in files:
#         with open(f, 'r') as data_file:
#                 data = json.load(data_file)
#
#         for i in data.keys():
#
#             nameaux = i
#             print (nameaux)
#            # if nameaux == "3":
#                 #continue
#             activity = situations[i]
#
#             for person in data[i].keys():
#                     activity_data = data[i][person]
#                     d = {
#                     'person' : person,
#                     'activity': activity ,
#                     'detected intervals': activity_data['detected intervals'],
#                     'real intervals': activity_data['real number of intervals'],
#                     'hit intervals': activity_data['hit intervals'],
#                     'precision': activity_data['precision'],
#                     'recall': activity_data['recall'],
#                     'accuracy': activity_data['accuracy'],
#                     'tp': activity_data['true positive time'],
#                     'tn': activity_data['true negative time'],
#                     'fp': activity_data['false positive time'],
#                     'fn': activity_data['false negative time']
#                     }
#                     no = 0
#                     for interv in activity_data['intervals']:
#                         no = no +1
#                         string1 = "delta start ";
#                         string2 = "delta end " ;
#                         string3 = "delta duration ";
#                         if interv['delta start']!='N/A':
#
#                             d[string1] = interv['delta start']
#                             d[string2] = interv['delta end']
#                             d[string3] = interv['delta duration']
#                         else:
#                             d[string1] = None
#                             d[string2] = None
#                             d[string3] = None
#
#                     analysis_data.append(d)
            
        

# df_analysis_base = pd.DataFrame(analysis_data)
#
# print(df_analysis_base.describe())
#
# df_detected = df_analysis_base[df_analysis_base['hit intervals'] > 0 ].groupby('activity')
#
# writer = pd.ExcelWriter('casas_interweaved_situation_detection_analysis.xlsx')
# df_detected.describe().to_excel(writer, "Summary")
# df_analysis_base.sort_values (['activity','detected intervals',  'hit intervals'], ascending=[1, 1, 1]).to_excel(writer, "Detailed")
#
# writer.save()


