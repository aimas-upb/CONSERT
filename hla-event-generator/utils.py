import math, datetime
import numpy as np

class GaussianPosTransition(object):
    DEFAULT_DELTA = 1

    @staticmethod
    def gaussian(mean, sigma):
        def func(x):
            return (1.0 / (math.sqrt(2 * math.pi) * sigma)) * math.exp( -((x - mean)**2) / (2 * sigma**2))

        return func


    def __init__(self, start_time = None, end_time = None, delta = DEFAULT_DELTA,
                 max_value = 1.0, mean = 0, sigma = 1.0,
                 left_only = False, right_only = False):
        ## left only means we only generate values from the left side of the mean
        ## right only means we only generate values from the right side of the mean
        self.start_time = start_time
        self.end_time = end_time
        self.delta = delta

        self.max_value = max_value
        self.mean = mean
        self.sigma = sigma

        self.left_only = left_only
        self.right_only = right_only

        self.distrib_func = GaussianPosTransition.gaussian(self.mean, self.sigma)

    def generate(self):
        event_meta_list = []

        sec_diff = (self.end_time - self.start_time).total_seconds()
        steps = int(sec_diff / self.delta)

        factor = self.max_value / self.distrib_func(0)

        xvals = None
        if self.left_only:
            xvals = np.linspace(-2 * self.sigma, 0, num=steps)
        elif self.right_only:
            xvals = np.linspace(0, 2 * self.sigma, num=steps)
        else:
            xvals = np.linspace(-2 * self.sigma , 2 * self.sigma, num=steps)

        ts = self.start_time
        for idx in range(len(xvals)):
            x = xvals[idx]
            event_meta_list.append({
                "timestamp": ts,
                "certainty": factor * self.distrib_func(x)
            })

            ts = ts + datetime.timedelta(seconds=self.delta)

        return event_meta_list