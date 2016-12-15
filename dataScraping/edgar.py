from __future__ import print_function

import datetime
import ftplib
import sqlite3
import tempfile
import zipfile

#used code from http://kaikaichen.com/?p=59 in order 

# Generate the list of quarterly zip files archived in EDGAR since
# start_year (earliest: 1993) until the most recent quarter
current_year = datetime.date.today().year
#how you get whatever quarter we are in Yo
current_quarter = (datetime.date.today().month - 1) // 3 + 1
start_year = 1993
years = list(range(start_year, current_year))
quarters = ['QTR1', 'QTR2', 'QTR3', 'QTR4']
history = [(y, q) for y in years for q in quarters]
for i in range(1, current_quarter):
    history.append((current_year, 'QTR%d' % i))
quarterly_files = ['edgar/full-index/%d/%s/master.zip' % (x[0], x[1]) for x
                   in history]
quarterly_files.sort()

# Generate the list of daily index files archived in EDGAR for the most
# recent quarter
ftp = ftplib.FTP('ftp.sec.gov')
ftp.login()
daily_files = [f for f in ftp.nlst('edgar/daily-index') if
               f.startswith('edgar/daily-index/master')]
daily_files.sort()

# Download index files and write content into SQLite
con = sqlite3.connect('edgar_idx.db')
cur = con.cursor()
cur.execute('DROP TABLE IF EXISTS idx')
cur.execute('CREATE TABLE idx (cik TEXT, conm TEXT, type TEXT, date TEXT, '
            'path TEXT)')

for file in quarterly_files:
    with tempfile.TemporaryFile() as temp:
        ftp.retrbinary('RETR %s' % file, temp.write)
        with zipfile.ZipFile(temp).open('master.idx') as z:
            for i in range(10):
                z.readline()
            records = [tuple(line.decode('latin-1').rstrip().split('|')) for
                       line in z]
    cur.executemany('INSERT INTO idx VALUES (?, ?, ?, ?, ?)', records)
    print(file, 'downloaded and wrote to SQLite')

for file in daily_files:
    with tempfile.TemporaryFile() as temp:
        ftp.retrbinary('RETR %s' % file, temp.write)
        temp.seek(0)
        for i in range(7):
            temp.readline()
        records = [tuple(line.decode('latin-1').rstrip().split('|')) for
                   line in temp]
    cur.executemany('INSERT INTO idx VALUES (?, ?, ?, ?, ?)', records)
    print(file, 'downloaded and wrote to SQLite')

con.commit()
con.close()

ftp.close()

# Write SQLite database to Stata
import pandas
from sqlalchemy import create_engine

engine = create_engine('sqlite:///edgar_idx.db')
with engine.connect() as conn, conn.begin():
    data = pandas.read_sql_table('idx', conn)
    data.to_stata('edgar_idx.dta')
