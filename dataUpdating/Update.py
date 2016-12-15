import os
import psycopg2
import urlparse
import yahoo_finance


def getSymbols(file):
    symbols = []
    with open(file) as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            symbols.append(row['Symbol'])
    return symbols

#add the files
symbols = getSymbols('nasdaq.csv') + getSymbols('nyse.csv')
#connect to the DB
conn = psycopg2.connect(
    database='d5f790u0pfiql',
    user="xoondtisxkyxdf",
    password="4dc7d512c8054abc040dccc3f92f871e0f3cd49c0d5e43b4c7f89bb6eb911e98",
    host="http://ec2-107-20-195-181.compute-1.amazonaws.com",
    port="5432"
)

cur = conn.cursoer()
while 1==1:
    for symbol in symbols:
        share = share(symbol)
        temp={}
        temp['ticker'] = symbol
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
        cur.execute("""
        Update current
        set  price = %s, volume = %s, open = %s, avgDailyVolume = %s, marketCap = %s, divedendPerShare = %s, dividendYield = %s, earningsPerShare = %s, dailyHigh = %s,
         dailyLow = %s, fiftyDayMovingAverage = %s, twoHundredDayMovingAverage = %s, pricePerEarnings = %s, priceEarningsGrowthRatio = %s, priceSales = %s, exchange = %s
         where ticker = %s""" % (temp['price'], temp['volume'], temp['open'], temp['avgDailyVolume'], temp['marketCap'], temp['divedendPerShare'], temp['dividendYield'], temp['earningsPerShare'], temp['dailyHigh'], temp['dailyLow'], temp['yearHigh'], temp['yearLow'], temp['fiftyDayMovingAverage'], temp['200DayMovingAverage'], temp['pricePerEarnings'], temp['priceEarningsGrowthRatio'], temp['priceSales'], temp['exchange'], symbol))
