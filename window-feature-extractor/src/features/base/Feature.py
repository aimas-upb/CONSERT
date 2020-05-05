from abc import abstractmethod


class Feature:
    @abstractmethod
    def get_result(self, window):
        pass
