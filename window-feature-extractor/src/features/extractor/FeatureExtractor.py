class FeatureExtractor:
    def __init__(self, features):
        self.features = features

    def add_feature(self, feature):
        if feature not in self.features:
            self.features.append(feature)

    def remove_feature(self, feature):
        if feature in self.features:
            self.features.remove(feature)

    def extract_features_from_window(self, window):
        result = {}
        for feature in self.features:
            result[feature.name] = feature.get_result(window)
        return result
