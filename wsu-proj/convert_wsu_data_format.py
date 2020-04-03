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

SENSOR_AREAS = {
    "Kitchen": ["M15", "M16", "M17", "M18",
                "I01", "I02", "I04", "I06", "I07",
                "D07", "D08", "D09", "D10"],

    "DiningTable": ["M13", "M14"],

    "LivingRoom": ["M06", "M07", "M08", "M09", "M10",
                   "T01"],

    "TV": ["M02", "M03",
           "I03", "I05",
           ]
}
