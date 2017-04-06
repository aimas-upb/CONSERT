import datetime, random, pytz
import numpy as np

from scipy.stats import rv_discrete
from utils import GaussianPosTransition

DEFAULT_DURATION         = 10
DEFAULT_NON_OVERLAP_DURATION = 2

DEFAULT_UPDATE_STEP = 1
DEFAULT_DELTA_STEP  = 1

DEFAULT_TP_MU       = 0.85
DEFAULT_TP_SIGMA    = 0.2

DEFAULT_FP_MU       = 0.35
DEFAULT_FP_SIGMA    = 0.15


DEFAULT_PERSON = "doe"

class Delay(object):
    '''
    Set sleep value (in seconds) for ETALIS event stream
    '''
    def __init__(self, delay):
        self.delay = delay

    def to_etalis(self):
        etalis_form = "sleep"
        etalis_form += "("
        etalis_form += str(self.delay)
        etalis_form += ")"
        etalis_form += "."

        return etalis_form



class AtomicEvent(object):
    counter = 0

    def __init__(self, timestamp = None, certainty = 1.0, person=DEFAULT_PERSON):
        self.certainty = certainty
        # if timestamp is None:
        #     self.timestamp = datetime.datetime.today()
        # else:
        #     self.timestamp = timestamp
        self.timestamp = timestamp

        self.person = person


    @staticmethod
    def get_tp_certainty_value(mu, sigma):
        '''
        Generate AtomicEvent certainty value from a normal distribution given mean and standard deviation. Function used to generate
        true instances => certainties are capped to the interval [0.65, 1.00]
        :param mu: mean of normal distribution
        :param sigma: standard deviation of normal distribution
        :return: The certainty value for the case of a correct event detection.
        '''
        if mu < 0.65 or mu > 1:
            return 0.85

        val = np.random.normal(mu, sigma)
        if val < 0.65:
            val = 0.65
        elif val > 1.0:
            val = 1.0

        return val

    @staticmethod
    def get_fp_certainty_value(mu, sigma):
        '''
        Generate AtomicEvent certainty value from a normal distribution given mean and standard deviation. Function used to generate
        false instances => certainties are capped to the interval [0.2, 0.5]
        :param mu: mean of normal distribution
        :param sigma: standard deviation of normal distribution
        :return: The certainty value for the case of an incorrect event detection.
        '''
        if mu < 0.2 or mu > 0.5:
            return 0.35

        val = np.random.normal(mu, sigma)
        if val < 0.2:
            val = 0.2
        elif val > 0.5:
            val = 0.5

        return val

    @staticmethod
    def to_datime(timestamp):
        '''
        Convert UNIX timestamp to ETALIS datime form
        :param timestamp: UNIX timestamp
        :return:
        '''
        return "datime" + "(" \
                + str(timestamp.year) + ", " \
                + str(timestamp.month) + ", " \
                + str(timestamp.day) + ", " \
                + str(timestamp.hour) + ", " \
                + str(timestamp.minute) + ", " \
                + str(timestamp.second) + ", " \
                + "1" \
                + ")"


    def meta_to_etalis(self):
        '''
        Generate meta-properties structure in ETALIS form for AtomicEvent
        :return:
        '''
        etalis_form = "meta"
        etalis_form += "("

        if self.timestamp:
            etalis_form += str((self.timestamp - datetime.datetime(1970,1,1)).total_seconds())
        else:

            etalis_form += str(AtomicEvent.counter)
            AtomicEvent.counter += 1

        etalis_form += ", "

        etalis_form += str(self.certainty)
        etalis_form += ")"

        return etalis_form


    def predicate_to_etalis(self):
        return None


    def to_etalis(self):
        '''
        Generate event structure in ETALIS form for AtomicEvent
        :return:
        '''
        etalis_form = "event"
        etalis_form += "("

        etalis_form += self.predicate_to_etalis()

        if self.timestamp:
            etalis_form += ", "
            etalis_form += "["
            etalis_form += AtomicEvent.to_datime(self.timestamp)
            etalis_form += ", "
            etalis_form += AtomicEvent.to_datime(self.timestamp)
            etalis_form += "]"

        etalis_form += ")"
        etalis_form += "."

        return etalis_form

    def to_dict(self):
        event_dict = {
            "event": {
                "event_type" : self.get_event_type(),
                "event_info" : {}
            }
        }

        event_dict['event']['event_info'].update(self.predicate_to_dict())
        event_dict['event']['event_info'].update(self.meta_to_dict())

        return event_dict


    def get_event_type(self):
        return None

    def predicate_to_dict(self):
        return None

    def meta_to_dict(self):
        ts = self.timestamp.replace(microsecond=0)
        
        epoch = datetime.datetime.utcfromtimestamp(0)
        epoch = epoch.replace(tzinfo=pytz.UTC)

        return {
            "annotations" : {
                "lastUpdated" : (ts - epoch).total_seconds() * 1000,
                "confidence":   self.certainty,
                "startTime":    ts.isoformat(),
                "endTime":      ts.isoformat()
            }
        }




class LLA(AtomicEvent):
    WALKING     = "WALKING"
    SITTING     = "SITTING"
    STANDING    = "STANDING"

    LLA_ADJACENCY = {
        WALKING:    [STANDING],
        STANDING:   [WALKING],
        SITTING:    [STANDING]
    }

    def __init__(self, type = None, person = DEFAULT_PERSON, timestamp = None, certainty = 1.0):
        super(LLA, self).__init__(timestamp=timestamp, certainty=certainty, person=person)
        self.type = type


    def predicate_to_etalis(self):
        etalis_form = "lla"
        etalis_form += "("
        etalis_form += self.person
        etalis_form += ", "

        etalis_form += str(self.type)
        etalis_form += ", "

        etalis_form += self.meta_to_etalis()
        etalis_form += ")"

        return etalis_form

    def get_event_type(self):
        return "lla"

    def predicate_to_dict(self):
        return {
            "person" : {"name" : self.person},
            "type": self.type
        } 



class Position(AtomicEvent):
    WORK_AREA           = "WORK_AREA"
    CONFERENCE_AREA     = "CONFERENCE_AREA"
    ENTERTAINMENT_AREA  = "ENTERTAINMENT_AREA"
    DINING_AREA         = "DINING_AREA"
    SNACK_AREA          = "SNACK_AREA"
    EXERCISE_AREA       = "EXERCISE_AREA"
    HYGENE_AREA         = "HYGENE_AREA"

    AREA_ADJACENCY = {
        WORK_AREA:          [DINING_AREA],
        CONFERENCE_AREA:    [DINING_AREA, ENTERTAINMENT_AREA],
        ENTERTAINMENT_AREA: [CONFERENCE_AREA, EXERCISE_AREA],
        DINING_AREA:        [WORK_AREA, CONFERENCE_AREA],
        SNACK_AREA:         [EXERCISE_AREA, HYGENE_AREA],
        EXERCISE_AREA:      [ENTERTAINMENT_AREA, SNACK_AREA],
        HYGENE_AREA:        [SNACK_AREA, WORK_AREA]
    }

    def __init__(self, type = None, person = DEFAULT_PERSON, timestamp = None, certainty = 1.0):
        super(Position, self).__init__(timestamp=timestamp, certainty=certainty, person=person)
        self.type = type


    def predicate_to_etalis(self):
        etalis_form = "pos"
        etalis_form += "("
        etalis_form += self.person
        etalis_form += ", "

        etalis_form += str(self.type)
        etalis_form += ", "

        etalis_form += self.meta_to_etalis()
        etalis_form += ")"

        return etalis_form

    def get_event_type(self):
        return "pos"

    def predicate_to_dict(self):
        return {
            "person" : {"name" : self.person},
            "type": self.type
        }



class HLA(object):
    WORKING             = "WORKING"
    DISCUSSING          = "DISCUSSING"

    ENTERTAINMENT       = "ENTERTAINMENT"
    DINING              = "DINING"
    SNACKING            = "SNACKING"
    EXERCISING          = "EXERCISING"
    HYGENE              = "HYGENE"
    UNDEFINED           = "UNDEFINED"


    def __init__(self, type = UNDEFINED, person = DEFAULT_PERSON,
                 start_time = datetime.datetime.today(), duration = DEFAULT_DURATION,
                 lla_step = DEFAULT_UPDATE_STEP, pos_step = DEFAULT_UPDATE_STEP,
                 accepted_combinations = None):

        ## type of the HLA (from Mihai's classification) and name of person carrying out the activity
        self.type = type
        self.person = person

        ## start time of the activity (as UNIX timestamp), duration of activity (in seconds)
        ## update step for generated LLAs (in seconds), update step for generated Positions (in seconds)
        self.start_time = start_time
        self.duration = duration
        self.lla_step = lla_step
        self.pos_step = pos_step

        ## HLAs that preced and follow the current one
        self._followed_by = None
        self._preceded_by = None

        ## HLA generation flags
        '''
        This flag applies only to UNDEFINED HLAs
        Indicates whether the transition between one HLA and another is composed of several interweaving WALKING and STANDING LLAs of various certainties,
        detected at different positions of the AmI-Lab. Default value is FALSE, meaning the transition is simple: only the WALKING LLA is used, with a gradual
        _shift_ in the certainty of the detected Position from the previous HLA to the the following one.
        '''
        self.complex_transition = False

        '''
        These flags apply only to defined HLAs. They stipulate the error rate in LLA and Position detection certainty.
        NOTE: the flags determine ONLY the error probability for the LLAs and Positions defining the current HLA.
        '''
        self.lla_error_rate = 0
        self.pos_error_rate = 0

        '''
        These flags apply only to defined HLAs. They determine the probability that a "false" LLA or Position are detected instead of the correct ones
        for the current HLA.
        '''
        self.lla_false_detect_rate = 0
        self.pos_false_detect_rate = 0

        ## list of accepted (LLA, Position) compositions
        self.accepted_combinations = accepted_combinations

        ## Select the actual chosen position and LLA from the available combinations allowed for this HLA
        self._select_active_combination()


    def _select_active_combination(self):
        if self.accepted_combinations:
            comb_idx = random.randint(0, len(self.accepted_combinations) - 1)
            self.active_pos = self.accepted_combinations[comb_idx]['position']
            self.active_lla = self.accepted_combinations[comb_idx]['lla']

    '''
    Getters and setter for HLAs preceding and following the current one
    '''
    @property
    def followed_by(self):
        return self._followed_by

    @followed_by.setter
    def followed_by(self, hla):
        self._followed_by = hla
        if hla.preceded_by is None or hla.preceded_by != self:
            hla.preceded_by = self

    @property
    def preceded_by(self):
        return self._preceded_by

    @preceded_by.setter
    def preceded_by(self, hla):
        self._preceded_by = hla
        if hla.followed_by is None or hla.followed_by != self:
            hla.followed_by = self


    @staticmethod
    def generate_non_overlap_transition(pos_type, lla_type, current_ts, non_overlap_duration, pos_step, lla_step, person):
        '''
        Auxiliary function for UNDEFINED HLA generation.
        Generate a sequence of AtomicEvents of type WALKING for the Position from/to which the subject is transitioning (e.g. from WORK_AREA to CONFERENCE_AREA).
        This sequence will be detected with high certainty and is not subject to overlap with other detected Positions.
        :param pos_type:    Position from/to which the subject is transitioning.
        :param lla_type:    Type of LLA used for transitioning. Default is always WALKING.
        :param current_ts:  Start of the non-overlap interval.
        :param non_overlap_duration:    Duration of the non-overlap interval
        :param pos_step:    update step for generated Positions (in seconds)
        :param lla_step:    update step for generated LLAs (in seconds)
        :param person:      name of subject carrying out the actions
        :return:    List of generated LLA and Position AtomicEvents
        '''
        event_list = []

        ts_pos = ts_lla = current_ts
        computed_duration = 0

        while computed_duration < non_overlap_duration:
            ts_limit = current_ts + datetime.timedelta(seconds=DEFAULT_DELTA_STEP)
            while (ts_pos < ts_limit or ts_lla < ts_limit):
                if ts_pos < ts_limit:
                    cert = AtomicEvent.get_tp_certainty_value(DEFAULT_TP_MU, DEFAULT_TP_SIGMA)
                    pos = Position(type=pos_type, person=person, timestamp=ts_pos, certainty=cert)
                    event_list.append(pos)

                ts_pos = ts_pos + datetime.timedelta(seconds=pos_step)

                if ts_lla < ts_limit:
                    cert = AtomicEvent.get_tp_certainty_value(DEFAULT_TP_MU, DEFAULT_TP_SIGMA)
                    lla = LLA(type=lla_type, person=person, timestamp=ts_lla, certainty=cert)
                    event_list.append(lla)

                ts_lla = ts_lla + datetime.timedelta(seconds=lla_step)

            current_ts = ts_limit
            computed_duration += DEFAULT_DELTA_STEP

        return event_list, current_ts


    @staticmethod
    def generate_simple_rampdown_transition(pos_type, lla_type, start_time, duration, pos_step, lla_step, person):
        '''
        Auxiliary function for UNDEFINED HLA generation.
        Generate a sequence of AtomicEvents of type WALKING from the Position FROM which the subject is transitioning.
        Certainty of detected Postion is in continuous decrease along a Gaussian curve.
        The position events in this sequence will overlap with the ones in the :func:`generate_simple_rampup_transition <events.HLA.generate_simple_rampup_transition>`
        :param pos_type:
        :param lla_type:
        :param start_time:
        :param duration:
        :param pos_step:
        :param lla_step:
        :param person:
        :return:
        '''
        aux_events = []

        end_time = start_time + datetime.timedelta(seconds=duration)

        transition_gen = GaussianPosTransition(start_time=start_time, end_time=end_time,
                                               delta=pos_step, max_value=DEFAULT_TP_MU, right_only=True)
        pos_metas = transition_gen.generate()

        # generate Position events according to their step and add them to the aux list
        for meta in pos_metas:
            pos = Position(type=pos_type, person=person,
                           timestamp=meta['timestamp'], certainty=meta['certainty'])
            aux_events.append(pos)

        # generate LLA events according to their step and add them to the aux list
        computed_duration = 0
        ts_lla = current_ts = start_time

        while computed_duration < duration:
            ts_limit = current_ts + datetime.timedelta(seconds=DEFAULT_DELTA_STEP)
            while ts_lla < ts_limit:
                cert = AtomicEvent.get_tp_certainty_value(DEFAULT_TP_MU, DEFAULT_TP_SIGMA)
                lla = LLA(type=lla_type, person=person, timestamp=ts_lla, certainty=cert)
                aux_events.append(lla)
                ts_lla = ts_lla + datetime.timedelta(seconds=lla_step)

            current_ts = ts_limit
            computed_duration += DEFAULT_DELTA_STEP

        # order aux_events by timestamp and then add to overall event list
        aux_events.sort(key=lambda ev: ev.timestamp)

        return aux_events


    @staticmethod
    def generate_simple_rampup_transition(pos_type, lla_type, start_time, duration, pos_step, lla_step, person):
        '''
        Auxiliary function for UNDEFINED HLA generation.
        Generate a sequence of AtomicEvents of type WALKING from the Position TO which the subject is transitioning.
        Certainty of detected Postion is in continuous increase along a Gaussian curve.
        The position events in this sequence will overlap with the ones in the :func:`generate_simple_rampdown_transition <events.HLA.generate_simple_ramdown_transition>`
        :param pos_type:
        :param lla_type:
        :param start_time:
        :param duration:
        :param pos_step:
        :param lla_step:
        :param person:
        :return:
        '''
        aux_events = []

        end_time = start_time + datetime.timedelta(seconds=duration)

        transition_gen = GaussianPosTransition(start_time=start_time, end_time=end_time,
                                               delta=pos_step, max_value=DEFAULT_TP_MU, left_only=True)
        pos_metas = transition_gen.generate()

        # generate Position events according to their step and add them to the aux list
        for meta in pos_metas:
            pos = Position(type=pos_type, person=person,
                           timestamp=meta['timestamp'], certainty=meta['certainty'])
            aux_events.append(pos)

        # generate LLA events according to their step and add them to the aux list
        computed_duration = 0
        ts_lla = current_ts = start_time

        while computed_duration < duration:
            ts_limit = current_ts + datetime.timedelta(seconds=DEFAULT_DELTA_STEP)
            while ts_lla < ts_limit:
                cert = AtomicEvent.get_tp_certainty_value(DEFAULT_TP_MU, DEFAULT_TP_SIGMA)
                lla = LLA(type=lla_type, person=person, timestamp=ts_lla, certainty=cert)
                aux_events.append(lla)
                ts_lla = ts_lla + datetime.timedelta(seconds=lla_step)

            current_ts = ts_limit
            computed_duration += DEFAULT_DELTA_STEP

        # order aux_events by timestamp and then add to overall event list
        aux_events.sort(key=lambda ev: ev.timestamp)

        return aux_events, end_time



    def generate(self, with_sleep = False):
        '''
        Main event generation function for current HLA.
        :param with_sleep: Specifies if sleep(x) statements are inserted in final event stream output. Default FALSE.
        :return:
        '''
        event_list = []

        ## generate discrete distributions from which to sample for error_rate and false_detect_rate flags
        pos_error_distrib = rv_discrete(values=([True, False], [self.pos_error_rate, 1 - self.pos_error_rate]))
        lla_error_distrib = rv_discrete(values=([True, False], [self.lla_error_rate, 1 - self.lla_error_rate]))

        pos_fd_distrib = rv_discrete(values=([True, False], [self.pos_false_detect_rate, 1 - self.pos_false_detect_rate]))
        lla_fd_distrib = rv_discrete(values=([True, False], [self.lla_false_detect_rate, 1 - self.lla_false_detect_rate]))


        ## Handle generation for UNDEFINED HLA
        if self.type == HLA.UNDEFINED:
            if self.complex_transition:
                raise NotImplementedError("Complex HLA Transitions not implemented yet!")
            else:
                ''' In this case we only generate the WALKING LLA and alter the start and end positions according to the previous and next HLAs'''
                transition_start = self.start_time

                ## determine previous and next Positions
                prev_pos = next_pos = None
                if self._preceded_by:
                    prev_pos = self._preceded_by.active_pos

                if self._followed_by:
                    next_pos = self._followed_by.active_pos

                aux_list = []

                if prev_pos:
                    ## generate non-overlap events - basically continue detecting the previous HLA Position with high certainty, BUT with WALKING LLA
                    aux_overlap_events, transition_start = HLA.generate_non_overlap_transition(prev_pos, LLA.WALKING, transition_start, DEFAULT_NON_OVERLAP_DURATION, self.pos_step, self.lla_step, self.person)

                    ## generate rampdown GaussionPosTransition for duration of UNDEFINED event
                    aux_rampdown_events = HLA.generate_simple_rampdown_transition(prev_pos, LLA.WALKING, transition_start, self.duration, self.pos_step, self.lla_step, self.person)

                    aux_list.extend(aux_overlap_events)
                    aux_list.extend(aux_rampdown_events)

                if next_pos:
                    ## generate rampup events
                    aux_rampup_events, transition_start = HLA.generate_simple_rampup_transition(next_pos, LLA.WALKING, transition_start, self.duration, self.pos_step, self.lla_step, self.person)

                    ## generate non-overlap events - basically detect the next HLA Position with high certainty, with WALKING LLA
                    aux_overlap_events, transition_end = HLA.generate_non_overlap_transition(next_pos, LLA.WALKING, transition_start, DEFAULT_NON_OVERLAP_DURATION, self.pos_step, self.lla_step, self.person)

                    aux_list.extend(aux_rampup_events)
                    aux_list.extend(aux_overlap_events)

                ## gather all transition events in aux list and sort them by timestamp
                aux_list.sort(key=lambda ev: ev.timestamp)

                ## insert transition events in global event stream list
                event_list.extend(aux_list)

        ## Handle generation for defined HLA
        else:
            ## We can only generate smth if we have valid Position and LLA instances
            if self.active_pos and self.active_lla:
                computed_duration = 0
                ts_pos = ts_lla = current_ts = self.start_time

                while True:
                    ## loop while duration of event not exhausted
                    if computed_duration < self.duration:
                        ## we advance in time increments of DEFAULT_DELTA_STEP duration
                        ts_limit = current_ts + datetime.timedelta(seconds=DEFAULT_DELTA_STEP)

                        while ts_pos < ts_limit or ts_lla < ts_limit:
                            ## Position and LLA events have their own generation rate (governed by pos_step and lla_step).
                            ## For each DEFAULT_DELTA_STEP increment of the main loop, we check to see how many new LLA and Position events can be generated (may be 0)
                            ''' ======== Generate position event if possible ======== '''
                            if ts_pos < ts_limit:
                                ## sample probability of Position certainty error
                                pos_error = pos_error_distrib.rvs()

                                ## sample probability of false Position detection error
                                pos_fd = pos_fd_distrib.rvs()

                                if not pos_error:
                                    ## generate high certainty Position event
                                    cert = AtomicEvent.get_tp_certainty_value(DEFAULT_TP_MU, DEFAULT_TP_SIGMA)
                                    pos = Position(type=self.active_pos, person = self.person, timestamp=ts_pos, certainty=cert)
                                    event_list.append(pos)
                                else:
                                    ## generate low certainty Position event
                                    # cert = AtomicEvent.get_fp_certainty_value(DEFAULT_FP_MU, DEFAULT_FP_SIGMA)
                                    # pos = Position(type=self.active_pos, person=self.person, timestamp=ts_pos, certainty=cert)
                                    # event_list.append(pos)

                                    ## if false detection flag enabled
                                    if pos_fd:
                                        print "POS " + self.active_pos + " is fucked up to ",
                                        ## generate a falsely detected Position event according to "reasonable false positives" (see ADJACENCY dict for each Position)
                                        false_pos_types = Position.AREA_ADJACENCY.get(self.active_pos)
                                        if false_pos_types:
                                            idx = random.randint(0, len(false_pos_types) - 1)
                                            false_pos_type = false_pos_types[idx]
                                            print false_pos_type

                                            cert = AtomicEvent.get_fp_certainty_value(DEFAULT_TP_MU, DEFAULT_TP_SIGMA)
                                            pos = Position(type=false_pos_type, person=self.person, timestamp=ts_pos, certainty=cert)
                                            event_list.append(pos)

                            ts_pos = ts_pos + datetime.timedelta(seconds=self.pos_step)


                            ''' ======== Generate LLA event if possible ======== '''
                            if ts_lla < ts_limit:
                                ## sample probability of LLA certainty error
                                lla_error = lla_error_distrib.rvs()

                                ## sample probability of false LLA detection error
                                lla_fd = lla_fd_distrib.rvs()

                                if not lla_error:
                                    ## generate high certainty LLA event
                                    cert = AtomicEvent.get_tp_certainty_value(DEFAULT_TP_MU, DEFAULT_TP_SIGMA)
                                    lla = LLA(type=self.active_lla, person=self.person, timestamp=ts_lla, certainty=cert)
                                    event_list.append(lla)
                                else:
                                    ## generate low certainty LLA event
                                    # cert = AtomicEvent.get_tp_certainty_value(DEFAULT_FP_MU, DEFAULT_FP_SIGMA)
                                    # lla = LLA(type=self.active_lla, person=self.person, timestamp=ts_lla, certainty=cert)
                                    # event_list.append(lla)

                                    ## if false detection flag enabled
                                    if lla_fd:
                                        print "LLA " + self.active_lla + " is fucked up to ",
                                        ## generate a falsely detected LLA event according to "reasonable false positives" (see ADJACENCY dict for each LLA)
                                        false_llas = LLA.LLA_ADJACENCY.get(self.active_lla)
                                        if false_llas:
                                            idx = random.randint(0, len(false_llas) - 1)
                                            false_lla_type = false_llas[idx]
                                            print false_lla_type

                                            cert = AtomicEvent.get_tp_certainty_value(DEFAULT_TP_MU, DEFAULT_TP_SIGMA)
                                            lla = LLA(type=false_lla_type, person=self.person, timestamp=ts_lla, certainty=cert)
                                            event_list.append(lla)

                                ts_lla = ts_lla + datetime.timedelta(seconds=self.lla_step)

                        ## increment main loop current timestamp and increment total HLA duration
                        current_ts = ts_limit
                        computed_duration += DEFAULT_DELTA_STEP
                    else:
                        ## break once desired duration of HLA has been attained
                        break

            else:
                raise ValueError("No accepted LLA-position combinations for non-undefined HLA!!!")

        if not with_sleep:
            ## return event stream list
            return event_list
        else:
            event_with_sleep_list = []
            nr_events = len(event_list)

            for idx in range(nr_events):
                if idx < nr_events - 1:
                    delta = int((event_list[idx + 1].timestamp - event_list[idx].timestamp).total_seconds())

                    event_list[idx].timestamp = None
                    event_with_sleep_list.append(event_list[idx])

                    if delta > 0:
                        event_with_sleep_list.append(Delay(delta))
                else:
                    event_list[idx].timestamp = None
                    event_with_sleep_list.append(event_list[idx])

            return event_with_sleep_list



"""====================================================================================================================="""
"""=================================================== SPECIFIC HLAs ==================================================="""
"""====================================================================================================================="""

class WorkingHLA(HLA):
    def __init__(self, person = DEFAULT_PERSON,
                 start_time = datetime.datetime.today(), duration = DEFAULT_DURATION,
                 lla_step = DEFAULT_UPDATE_STEP, pos_step = DEFAULT_UPDATE_STEP):

        super(WorkingHLA, self).__init__(type=HLA.WORKING, person=person,
                                         start_time=start_time, duration=duration,
                                         lla_step=lla_step, pos_step=pos_step,
                                         accepted_combinations = [{"lla": LLA.SITTING, "position": Position.WORK_AREA}])


class DiscussingHLA(HLA):
    def __init__(self, person = DEFAULT_PERSON,
                 start_time = datetime.datetime.today(), duration = DEFAULT_DURATION,
                 lla_step = DEFAULT_UPDATE_STEP, pos_step = DEFAULT_UPDATE_STEP):

        super(DiscussingHLA, self).__init__(type=HLA.DISCUSSING, person=person,
                                         start_time=start_time, duration=duration,
                                         lla_step=lla_step, pos_step=pos_step,
                                         accepted_combinations=[{"lla": LLA.SITTING, "position": Position.CONFERENCE_AREA},
                                                                {"lla": LLA.STANDING, "position": Position.CONFERENCE_AREA}
                                                                ])


class DiningHLA(HLA):
    def __init__(self, person = DEFAULT_PERSON,
                 start_time = datetime.datetime.today(), duration = DEFAULT_DURATION,
                 lla_step = DEFAULT_UPDATE_STEP, pos_step = DEFAULT_UPDATE_STEP):

        super(DiningHLA, self).__init__(type=HLA.DINING, person=person,
                                         start_time=start_time, duration=duration,
                                         lla_step=lla_step, pos_step=pos_step,
                                         accepted_combinations = [{"lla": LLA.SITTING, "position": Position.DINING_AREA}])


class SnackingHLA(HLA):
    def __init__(self, person = DEFAULT_PERSON,
                 start_time = datetime.datetime.today(), duration = DEFAULT_DURATION,
                 lla_step = DEFAULT_UPDATE_STEP, pos_step = DEFAULT_UPDATE_STEP):

        super(SnackingHLA, self).__init__(type=HLA.SNACKING, person=person,
                                         start_time=start_time, duration=duration,
                                         lla_step=lla_step, pos_step=pos_step,
                                         accepted_combinations = [{"lla": LLA.STANDING, "position": Position.DINING_AREA}])


class EntertainmentHLA(HLA):
    def __init__(self, person = DEFAULT_PERSON,
                 start_time = datetime.datetime.today(), duration = DEFAULT_DURATION,
                 lla_step = DEFAULT_UPDATE_STEP, pos_step = DEFAULT_UPDATE_STEP):

        super(EntertainmentHLA, self).__init__(type=HLA.ENTERTAINMENT, person=person,
                                         start_time=start_time, duration=duration,
                                         lla_step=lla_step, pos_step=pos_step,
                                         accepted_combinations = [{"lla": LLA.SITTING,  "position": Position.ENTERTAINMENT_AREA},
                                                                  {"lla": LLA.STANDING, "position": Position.ENTERTAINMENT_AREA}])


class ExerciseHLA(HLA):
    def __init__(self, person = DEFAULT_PERSON,
                 start_time = datetime.datetime.today(), duration = DEFAULT_DURATION,
                 lla_step = DEFAULT_UPDATE_STEP, pos_step = DEFAULT_UPDATE_STEP):

        super(ExerciseHLA, self).__init__(type=HLA.EXERCISING, person=person,
                                         start_time=start_time, duration=duration,
                                         lla_step=lla_step, pos_step=pos_step,
                                         accepted_combinations = [{"lla": LLA.STANDING, "position": Position.EXERCISE_AREA}])



class HygeneHLA(HLA):
    def __init__(self, person = DEFAULT_PERSON,
                 start_time = datetime.datetime.today(), duration = DEFAULT_DURATION,
                 lla_step = DEFAULT_UPDATE_STEP, pos_step = DEFAULT_UPDATE_STEP):

        super(HygeneHLA, self).__init__(type=HLA.HYGENE, person=person,
                                         start_time=start_time, duration=duration,
                                         lla_step=lla_step, pos_step=pos_step,
                                         accepted_combinations = [{"lla": LLA.STANDING, "position": Position.HYGENE_AREA},
                                                                  {"lla": LLA.WALKING,  "position": Position.HYGENE_AREA}])



class UndefinedHLA(HLA):
    def __init__(self, person = DEFAULT_PERSON,
                 start_time = datetime.datetime.today(), duration = DEFAULT_DURATION,
                 lla_step = DEFAULT_UPDATE_STEP, pos_step = DEFAULT_UPDATE_STEP,
                 direct_transition = True, complex_transition = False):

        super(UndefinedHLA, self).__init__(type=HLA.UNDEFINED, person=person,
                                         start_time=start_time, duration=duration,
                                         lla_step=lla_step, pos_step=pos_step)
        self.direct_transition = direct_transition
        self.complex_transition = complex_transition