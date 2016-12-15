#used documentation from https://pypi.python.org/pypi/yahoo-finance/1.1.4
import datetime
import multiprocessing
import csv
from yahoo_finance import *
from pprint import *
yahoo = Share('aapl')

#creat list of ticker symbols

def getSymbols(file):
    symbols = []
    with open(file) as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            symbols.append(row['Symbol'])
    return symbols


#step three: create current stock data relation
#make this its own function as it is by far the most time intensive
#prevents me from having to access the network multiple times
def createShareObjects(symbol):
    shares = []
    length = len(symbol)
    for i in range(0,length):
        try:
            share = Share(symbol[i])
        except:
            print("idk why but it didn't work for " + symbol[i])
        shares.append(share)
        print(str(float(((float(i) + 1)/length)*100)) + ' percent done creating share objects')
    return shares


def retrieveCurrent(share, ticker):

    temp={}
    temp['ticker'] = ticker
    temp['name'] = share.get_name()
    temp['price'] = share.get_price()
    temp['volume'] = share.get_volume()
    temp['open'] = share.get_open()
    temp['avgDailyVolume'] = share.get_avg_daily_volume()
    temp['marketCap'] = share.get_market_cap()
    temp['divedendPerShare'] = share.get_dividend_share()
    temp['dividendYield'] = share.get_dividend_yield()
    temp['earningsPerShare'] =  share.get_earnings_share()
    temp['dailyHigh'] = share.get_days_high()
    temp['dailyLow'] = share.get_days_low()
    temp['yearHigh'] = share.get_year_high()
    temp['yearLow'] = share.get_year_low()
    temp['fiftyDayMovingAverage'] = share.get_50day_moving_avg()
    temp['200DayMovingAverage'] = share.get_200day_moving_avg()
    temp['pricePerEarnings'] = share.get_price_earnings_ratio()
    temp['priceEarningsGrowthRatio'] = share.get_price_earnings_growth_ratio()
    temp['priceSales'] = share.get_price_sales()
    temp['exchange'] = share.get_stock_exchange()
    return [temp]


def pastData(share, ticker, startTime = '2015-12-01', endTime = '2016-12-01'):
    try:
        return share.get_historical(startTime, endTime)
    except:
        startTime = datetime.datetime.strptime(startTime, '%Y-%m-%d')
        startTime = startTime + datetime.timedelta(days=1)
        startTime = datetime.datetime.strftime(startTime, '%Y-%m-%d')
        if startTime == endTime:
            print("didn't work")
            return 'none'
        return pastData(share, ticker, startTime, endTime)


def createCSVFile(function, shares, filename):
    # print('createCSVFile')


    things = (function(shares[0], symbols[0]))[0].keys()
    with open(filename, 'w') as csvfile:
        print("hello")


        writer = csv.DictWriter(csvfile, things)
        writer.writeheader()
        shareLength = len(shares)
        for i in range(0,shareLength):
            try:
                result = function(shares[i], symbols[i])

            except:
                print("It didn't work")
            if result != 'none':
                for item in result:
                    try:
                        writer.writerow(item)
                    except:
                        print("didn't write")
                print('finished ' + str(shares[i].get_name()) + ' now ' + str((float((float(i) + 1)/shareLength)*100)) + ' percent done ' + filename)
            else:
                print("didn't work :(")
#step four: create past stock data relation: do we need this?

#execute stuff
symbols = getSymbols('nasdaq.csv') + getSymbols('nyse.csv')
shares = createShareObjects(symbols)

p = multiprocessing.Process(target=createCSVFile, args=(retrieveCurrent, shares, 'current.csv', ))


firstQuarter = int(len(shares)/4)
secondQuarter = firstQuarter + firstQuarter + 1
thirdQuarter = secondQuarter + firstQuarter


a = multiprocessing.Process(target=createCSVFile, args=(pastData, shares[0:firstQuarter], 'past.csv'))
b = multiprocessing.Process(target=createCSVFile, args=(pastData, shares[(firstQuarter+1):secondQuarter], 'past1.csv'))
c = multiprocessing.Process(target=createCSVFile, args=(pastData, shares[(secondQuarter+1):thirdQuarter], 'past2.csv'))
d = multiprocessing.Process(target=createCSVFile, args=(pastData, shares[(thirdQuarter+1):len(shares)], 'past3.csv'))

p.start()
a.start()
b.start()
c.start()
d.start()
