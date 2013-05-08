import fileinput
from datetime import datetime

format = '%Y-%m-%d'
reference = datetime.strptime('2013-01-19', format)

for line in fileinput.input(['part-r-00000']):
#	print line
        splits=line.split(',')
	currDate = datetime.strptime(splits[6], format)
	age = (reference - currDate).days * 24 * 60
	print splits[4] + " " + splits[3] + " " + splits[7] + " " + str(age) + " " + splits[1] + " " + splits[9] + " " + splits[14]
