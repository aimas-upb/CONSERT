import re
import datetime


def _parse_date(input_date):
    date_components = [int(token.strip()) for token in re.split("\(|\)|,", input_date.strip()) if
                       token.strip().isdigit()]
    parsed_date = datetime.datetime(*date_components[:-1])
    return parsed_date


def parse_stream(input_data):
    for line in input_data:
        if line.strip() and not line.startswith("%"):
            datime_regex = re.compile(r"datime\(.*?\)")
            dates = datime_regex.findall(line)
            start_date = _parse_date(dates[0])
            end_date = _parse_date(dates[1])
            tokens = [rawToken.strip() for rawToken in re.split('[(),\[\]]', line.strip()) if rawToken.strip()]
            # print tokens
            if tokens:
                extracted = tokens[1:4]
                extracted.append(str((start_date - datetime.datetime(1970, 1, 1)).total_seconds()))
                extracted.append(str((end_date - datetime.datetime(1970, 1, 1)).total_seconds()))
                yield ','.join(extracted) + "\n"
