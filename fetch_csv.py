#!/usr/bin/env python

import sys

import os.path
if sys.version_info[0] <= 2:
    from urllib import urlretrieve
else:
    from urllib.request import urlretrieve

if len(sys.argv) < 3:
    sys.exit('Tablename and number of records required')
    
kind = sys.argv[1]
to = int(sys.argv[2])
by = 1000
from0 = 0

if len(sys.argv) > 3:
    by = int(sys.argv[3])
if len(sys.argv) > 4:
    from0 = int(sys.argv[4])

#print("FROM: " + str(from0))
#print("BY:   " + str(by))
#print("TO:   " + str(to))

specs = []
filenames = []

lb = from0
while ((lb+by) < to):
    filename = "%s_%05dx%04d.csv" % (kind, lb, by)
    url = "https://gae-wrapup-server.appspot.com/admin/bulk/%s?header=true&token=wrapmixerwrap&defer=true&useCache=true" % (filename)
    specs.append((filename,url))
    filenames.append(filename)

    if not os.path.isfile(filename):
        print("downloading " + filename);
        urlretrieve(url, filename)
        print("downloaded  " + filename);
    
    lb += by

# print("NOW: " + str(lb))

filename = "%s_%05dx%04d.csv" % (kind, lb, by)
url = "https://gae-wrapup-server.appspot.com/admin/bulk/%s?header=true&token=wrapmixerwrap" % (filename)
specs.append((filename,url))
filenames.append(filename)

if not os.path.isfile(filename):
    print("downloading " + filename);
    urlretrieve(url, filename)
    print("downloaded  " + filename);
    
catfile = "%s.csv" % (kind)
    
with open(catfile, 'w') as outfile:
    fileno = 0
    for fname in filenames:
        with open(fname) as infile:
            lineno = 0
            for line in infile:
                if ((fileno == 0) or (lineno > 0)):
                    outfile.write(line)
                lineno += 1
        fileno += 1
    
# urlretrieve("https://gae-wrapup-server.appspot.com/admin/bulk/AccessEvent_15000x1000.csv?header=true&token=wrapmixerwrap&defer=true&useCache=true", "AccessEvent_15000x1000.csv")
