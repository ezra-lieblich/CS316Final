import sqlite3
import ftplib
#connect to our local database
#based off of http://kaikaichen.com/?p=59
conn = sqlite3.connect('edgar_idx.db')
cur = conn.cursor()

ftp = ftplib.FTP('ftp.sec.gov')
ftp.login()

def query(name=None, startDate='1993-01-01', endDate='2017-01-01', type='8-K'):
    print(name, startDate, endDate, type)
    cur.execute("SELECT * FROM idx WHERE date>? and conm LIKE ? and type=?", (startDate, '%'+name+'%', type))
    paths = cur.fetchall()
    for path in paths:
        print(path[1] + ' ' + path[2] + ' ' + path[3])
        saveName='-'.join([path[1], path[2], path[3]])
        saveNameNoSlashes = ''
        for char in saveName:
                if char != '/':
                    saveNameNoSlashes += char
        location = path[4].strip()
        with open(saveNameNoSlashes, 'wb') as f:
            ftp.retrbinary('RETR %s' %location, f.write)
#query to
query('apple', '1993-01-01', '1995-01-01', '10-K')
query('Microsoft', '1993-01-01', '1995-01-01', '10-K')
query('apple', '2012-01-01')
query('apple', '1993-01-01', '1995-01-01', '10-K')
query('tesla', '2012-01-05')
