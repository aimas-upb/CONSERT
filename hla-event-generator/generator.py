import datetime, pytz
import events
import sys, os
import json

class Generator(object):
    def __init__(self, hla_list, output_stream):
        self.hla_list = hla_list
        self.output_stream = output_stream

    def generate(self, with_sleep = False):
        for hla in hla_list:
            event_list = hla.generate(with_sleep=with_sleep)
            print (self.output_stream, "%% ======== HLA: " + hla.type + " ======== ")
            for event in event_list:
                print >> self.output_stream, event.to_etalis()

            print >> self.output_stream, os.linesep

    def generate_json(self):
        output = []
        for hla in hla_list:
            event_list = hla.generate(with_sleep=False)
            for event in event_list:
                output.append(event.to_dict())

        json.dump(output, self.output_stream)
        

if __name__ == "__main__":
    
    '''
    ## set start of HLAs
    work_start = datetime.datetime.today()
    work_duration = 10  # 10 seocnd duration of working HLA
    work_hla = events.WorkingHLA(person="alex", start_time=work_start, duration=work_duration, lla_step=1, pos_step=1)

    ## set start of UNDEFINED transition HLA at 10 seconds from "working" HLA
    undef_start = work_start + datetime.timedelta(seconds = work_duration)
    undef_duration = 7
    undef_hla = events.UndefinedHLA(person="alex", start_time=undef_start, duration=undef_duration, lla_step=1, pos_step=1)

    ## set preceding HLA
    undef_hla.preceded_by = work_hla


    ## set start of "discussing" HLA - take into account undefined_duration + THE 2 DEFAULT_NON_OVERLAP INTERVALS
    ##  - one for previous "working" HLA  and one for the following "discussing" HLA
    discussing_start = undef_start + datetime.timedelta(seconds = undef_duration + 2 * events.DEFAULT_NON_OVERLAP_DURATION)
    discussing_duration = 10
    discussing_hla = events.DiscussingHLA(person="alex", start_time=discussing_start, duration=discussing_duration, lla_step=1, pos_step=1)

    ## set preceding HLA
    discussing_hla.preceded_by = undef_hla

    ## create desired HLA sequence
    hla_list = [work_hla, undef_hla, discussing_hla]
    '''

    ## set start of UNDEFINED transition HLA at 10 seconds from "working" HLA
    undef_1_start = datetime.datetime.utcnow()
    undef_1_start = undef_1_start.replace(tzinfo=pytz.UTC)
    
    undef_1_duration = 10
    undef_1_hla = events.UndefinedHLA(person="mihai", start_time=undef_1_start, duration=undef_1_duration, lla_step=1, pos_step=1)
    undef_1_hla.lla_error_rate = 0.1
    undef_1_hla.pos_error_rate = 0.1
    undef_1_hla.lla_false_detect_rate = 0.1
    undef_1_hla.pos_false_detect_rate = 0.1

    ## set start of HLAs
    work_1_start = undef_1_start + datetime.timedelta(seconds = undef_1_duration + 2 * events.DEFAULT_NON_OVERLAP_DURATION)
    # work_1_start = datetime.datetime.today()
    work_1_duration = 120  # 10 seocnd duration of working HLA
    work_1_hla = events.WorkingHLA(person="mihai", start_time=work_1_start, duration=work_1_duration, lla_step=1, pos_step=1)
    work_1_hla.lla_error_rate = 0.1
    work_1_hla.pos_error_rate = 0.1
    work_1_hla.lla_false_detect_rate = 0.1
    work_1_hla.pos_false_detect_rate = 0.1

    ## set preceding HLA
    work_1_hla.preceded_by = undef_1_hla

    ## set start of UNDEFINED transition HLA at 10 seconds from "working" HLA
    undef_2_start = work_1_start + datetime.timedelta(seconds = work_1_duration)
    undef_2_duration = 20
    undef_2_hla = events.UndefinedHLA(person="mihai", start_time=undef_2_start, duration=undef_2_duration, lla_step=1, pos_step=1)
    undef_2_hla.lla_error_rate = 0.1
    undef_2_hla.pos_error_rate = 0.1
    undef_2_hla.lla_false_detect_rate = 0.1
    undef_2_hla.pos_false_detect_rate = 0.1

    ## set preceding HLA
    undef_2_hla.preceded_by = work_1_hla

    # set start of HLAs
    work_2_start = undef_2_start + datetime.timedelta(seconds = undef_2_duration + 2 * events.DEFAULT_NON_OVERLAP_DURATION)
    work_2_duration = 60  # 10 seocnd duration of working HLA
    work_2_hla = events.WorkingHLA(person="mihai", start_time=work_2_start, duration=work_2_duration, lla_step=1, pos_step=1)
    work_2_hla.lla_error_rate = 0.1
    work_2_hla.pos_error_rate = 0.1
    work_2_hla.lla_false_detect_rate = 0.1
    work_2_hla.pos_false_detect_rate = 0.1

    ## set preceding HLA
    work_2_hla.preceded_by = undef_2_hla

    ## set start of HLAs
    dining_hla_start = work_2_start + datetime.timedelta(seconds = work_2_duration + 2 * events.DEFAULT_NON_OVERLAP_DURATION)
    dining_hla_duration = 60  # 10 seocnd duration of working HLA
    dining_hla = events.DiningHLA(person="mihai", start_time=dining_hla_start, duration=dining_hla_duration, lla_step=1, pos_step=1)
    dining_hla.lla_error_rate = 0.1
    dining_hla.pos_error_rate = 0.1
    dining_hla.lla_false_detect_rate = 0.1
    dining_hla.pos_false_detect_rate = 0.1

    dining_hla.preceded_by = work_2_hla

    discussing_hla_start = dining_hla_start + datetime.timedelta(seconds = dining_hla_duration + 2 * events.DEFAULT_NON_OVERLAP_DURATION)
    discussing_hla_duration = 60  # 10 seocnd duration of working HLA
    discussing_hla = events.DiscussingHLA(person="mihai", start_time=discussing_hla_start, duration=discussing_hla_duration, lla_step=1, pos_step=1)
    discussing_hla.lla_error_rate = 0.1
    discussing_hla.pos_error_rate = 0.1
    discussing_hla.lla_false_detect_rate = 0.1
    discussing_hla.pos_false_detect_rate = 0.1

    discussing_hla.preceded_by = dining_hla
    
    exercise_hla_start = discussing_hla_start + datetime.timedelta(seconds = discussing_hla_duration + 2 * events.DEFAULT_NON_OVERLAP_DURATION)
    exercise_hla_duration = 60  # 10 seocnd duration of working HLA
    exercise_hla = events.ExerciseHLA(person="mihai", start_time=exercise_hla_start, duration=exercise_hla_duration, lla_step=1, pos_step=1)
    exercise_hla.lla_error_rate = 0.1
    exercise_hla.pos_error_rate = 0.1
    exercise_hla.lla_false_detect_rate = 0.1
    exercise_hla.pos_false_detect_rate = 0.1

    exercise_hla.preceded_by = discussing_hla
    
    hla_list = [undef_1_hla, work_1_hla, undef_2_hla, work_2_hla, dining_hla, discussing_hla, exercise_hla]

    ## generate HLA events and print them to file
   ## with open("../single_hla_120s_01er_015fd_with_sleep.stream", "w") as outfile:
    ## with open("../single_hla_120s_01er_015fd.stream", "w") as outfile:
      ##   gen = Generator(hla_list, outfile)
       ##  gen.generate(with_sleep=True)

         #print "Done. Event stream generated!"

    ## generate HLA events as JSON
    with open("ex.json", "w") as outfile:
        gen = Generator(hla_list, outfile)
        gen.generate_json()
        print ("Done. JSON dump of event stream generated!")
