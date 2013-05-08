import fileinput

output = ""
for line in fileinput.input(['../../freq.words.2.txt']):
        output += ", review_user::review::" + line.rstrip() + " as " + line.rstrip()
	print line
print output
