#!/usr/bin/python

import dateparser
import sys
import os

import codecs

import pprint

DISCRETE = "discrete"
NUMERIC = "numeric"

sensors = {}

def build_sensors():

	# add motion sensors
	for id in range(1,52):
		id_key = 'M' + str(id).zfill(2)
		sensors[id_key] = {	"type" : "motion", \
							"id" : str(id).zfill(2), \
							"value_type" : DISCRETE, \
							"accepted_values" : [ "ON", "OFF" ] }
	
	# add item sensors
	sensors['I01'] = {	"type" : "item", \
						"id" : "oatmeal", \
						"value_type" : DISCRETE, \
						"accepted_values" : [ "ABSENT", "PRESENT" ] }

	sensors['I02'] = {	"type" : "item", \
						"id" : "raisins", \
						"value_type" : DISCRETE, \
						"accepted_values" : [ "ABSENT", "PRESENT" ] }

	sensors['I03'] = {	"type" : "item", \
						"id" : "brown_sugar", \
						"value_type" : DISCRETE, \
						"accepted_values" : [ "ABSENT", "PRESENT" ] }

	sensors['I04'] = {	"type" : "item", \
						"id" : "bowl", \
						"value_type" : DISCRETE, \
						"accepted_values" : [ "ABSENT", "PRESENT" ] }

	sensors['I05'] = {	"type" : "item", \
						"id" : "measuring_spoon", \
						"value_type" : DISCRETE, \
						"accepted_values" : [ "ABSENT", "PRESENT" ] }

	sensors['I06'] = {	"type" : "item", \
						"id" : "medicine", \
						"value_type" : DISCRETE, \
						"accepted_values" : [ "ABSENT", "PRESENT" ] }

	sensors['I07'] = {	"type" : "item", \
						"id" : "pot", \
						"value_type" : DISCRETE, \
						"accepted_values" : [ "ABSENT", "PRESENT" ] }

	sensors['I08'] = {	"type" : "item", \
						"id" : "phone_book", \
						"value_type" : DISCRETE, \
						"accepted_values" : [ "ABSENT", "PRESENT" ] }

	sensors['I09'] = {	"type" : "item", \
						"id" : "unknown", \
						"value_type" : DISCRETE, \
						"accepted_values" : [ "ABSENT", "PRESENT" ] }	

	# add cabinet sensors
	for id in range(1,13):
		id_key = 'D' + str(id).zfill(2)
		sensors[id_key] = {	"type" : "cabinet", \
							"id" : str(id).zfill(2), \
							"value_type" : DISCRETE, \
							"accepted_values" : [ "OPEN", "CLOSE" ] }

	# add special sensors
	sensors["AD1-A"] = {	"type" : "burner", \
							"id" : "burner", \
							"value_type" : NUMERIC, \
							"accepted_values" : [ 0, 1 ] }

	sensors["AD1-B"] = {	"type" : "water", \
							"id" : "hot", \
							"value_type" : NUMERIC, \
							"accepted_values" : [ 0, 1 ] }

	sensors["AD1-C"] = {	"type" : "water", \
							"id" : "cold", \
							"value_type" : NUMERIC, \
							"accepted_values" : [ 0, 1 ] }

	# add phone sensor
	sensors['P01'] = {	"type" : "phone", \
						"id" : "phone", \
						"value_type" : DISCRETE, \
						"accepted_values" : [ "START", "END" ] }

	# add temperature sensors
	for id in range(1,4):
		id_key = 'T' + str(id).zfill(2)
		sensors[id_key] = {	"type" : "temperature", \
							"id" : str(id).zfill(2), \
							"value_type" : NUMERIC, \
							"accepted_values" : [ -10, 50 ] }

	# pp = pprint.PrettyPrinter(indent=1)
	# pp.pprint(sensors)


def to_event_datime(timestamp):
        '''
        Convert UNIX timestamp to ETALIS datime form
        :param timestamp: UNIX timestamp
        :return:
        '''
        if not timestamp:
        	return None

        return "datime" + "(" \
                + str(timestamp.year) + ", " \
                + str(timestamp.month) + ", " \
                + str(timestamp.day) + ", " \
                + str(timestamp.hour) + ", " \
                + str(timestamp.minute) + ", " \
                + str(timestamp.second) + ", " \
                + "1" \
                + ")"

def parse_date(tokens):
	if '.' in tokens[1]:
		hour_token = ":".join(tokens[1].split(".")[:-1])
	else:
		hour_token = tokens[1]
	print("Parsing " + tokens[0] + " " + hour_token)
	return dateparser.parse(tokens[0] + ' ' + hour_token)

def to_etalis_event(sensor, event_value, event_datime):
    '''
    Generate event structure in ETALIS form for AtomicEvent
    :return:
    '''
    etalis_form = "event"
    etalis_form += "("

    etalis_form += sensor["type"]
    etalis_form += "("
    etalis_form += sensor["id"]
    etalis_form += ","
    etalis_form += event_value
    etalis_form += ")"

    if event_datime:
        etalis_form += ", "
        etalis_form += "["
        etalis_form += event_datime
        etalis_form += ", "
        etalis_form += event_datime
        etalis_form += "]"

    etalis_form += ")"
    etalis_form += "."

    return etalis_form

def safe_open(input_file_path):
	try:
		input_file = codecs.open(input_file_path,'r','utf-8')
		input_file.readline()
	except UnicodeDecodeError:
		input_file = codecs.open(input_file_path,'r','utf-16')
	return input_file

def parse_file(input_file_path, output_file_path, has_class = False):
	output_file = open(output_file_path, 'w')
	with safe_open(input_file_path) as input_file:
		for line in input_file:
			tokens = line.strip().replace('\t',' ').split(' ')

			# parse date
			datetime = parse_date(tokens)
			event_datime = to_event_datime(datetime)
			if not event_datime:
				print("[ERROR] Date not correctly formatted: " + line)
				sys.exit(-1)

			# parse and validate event type
			event_id = tokens[2]
			if event_id not in sensors.keys():
				print("[ERROR] Sensor id not correctly formatted: " + line)
				continue
			
			# parse and validate event value
			event = sensors[event_id]
			event_value= tokens[3]
			if event["value_type"] is DISCRETE:
				if event_value not in event["accepted_values"]:
					print("[ERROR] Sensor value " + event_value + " not correctly formatted: " + line)
					sys.exit(-1)
			elif event["value_type"] is NUMERIC:
				if float(event_value) < event["accepted_values"][0] or float(event_value) > event["accepted_values"][1]:
					print("[ERROR] Sensor value not correctly formatted: " + line)
					sys.exit(-1)
			else:
				print("[ERROR] Sensor value type not correctly formatted: " + line)
				print (tokens)
				sys.exit(-1)

			# parse and validate event class
			if has_class:
				event_class = tokens[4]

			etalis_event = to_etalis_event(sensors[event_id],event_value,event_datime)
			output_file.write(etalis_event + '\n')
	output_file.close()

if not len(sys.argv) == 2:
	print("[ERROR] Input arguments not correct")
	sys.exit(-1)
build_sensors()
if os.path.isdir(sys.argv[1]):
	print("[INFO] Parsing folder: " + sys.argv[1])
	for file in os.listdir(sys.argv[1]):
		input_file_path = os.path.join(sys.argv[1],file)
		if file.endswith(".stream"):
			print("[WARN] Skipping .stream file: " + file)
			continue
		output_file_path = input_file_path + ".stream"
		parse_file(input_file_path, output_file_path)
elif os.path.isfile(sys.argv[1]):
	if sys.argv[1].endswith(".stream"):
		print("[ERROR] Will not convert .stream file")
		sys.exit(-1)
	output_file_path = sys.argv[1] + ".stream"
	parse_file(sys.argv[1], output_file_path)
else:
	print("[ERROR] Argument provided not file or folder")
	sys.exit(-1)
