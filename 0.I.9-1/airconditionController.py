#!/usr/bin/python

"""airconditionController.py
Message-based WebSockets client that consumes node/capability updates from 
Uberdust server on http://uberdust.cti.gr.
"""

import sys
import getopt
from twisted.internet import reactor
from autobahn.websocket import connectWS,WebSocketClientFactory, WebSocketClientProtocol
import os
import time
import thread
import message
import urllib2
import logging
import datetime


logging.basicConfig(format='%(asctime)-15s %(levelname)s %(message)s',filename='airconditionController.log',level=logging.DEBUG)

#FORMAT = '%(asctime)-15s %(clientip)s %(user)-8s %(message)s'
#logging.basicConfig(format=FORMAT)
#logger = logging.getLogger('0.I.9-1.py')

temperaturenode="urn:wisebed:ctitestbed:0x9979"
temperaturecapability="urn:wisebed:node:capability:temperature"
actuatornode="urn:wisebed:ctitestbed:0x2b0"
actuatorPayload="7f,69,70,1,ff,"
URL = '://uberdust.cti.gr:80/readings.ws'
WS_URL = 'ws'+URL
http_URL = 'http'+URL
PROTOCOL = []

wsconnection=0
binary1=0

expectedTemperature=0
reportedTemperature=0

reportedThr=25
expectedThr=25

logging.info('Starting application')

class NodeCapabilityConsumerProtocol(WebSocketClientProtocol):
	"""
	Node/Capability consumer protocol class.
	"""

	def onOpen(self):
                #os.system("gntp-send '0.I.9-1' 'onOpen'")
		global wsconnection
		wsconnection=self
		# on connection establish
		logging.info('WebSocket Connection to '+str(WS_URL)+' established.')
	def onMessage(self, wsmessage, binary):
		global binary1
		global reportedTemperature
		binary1=binary
		try :
			envelope = message.Envelope()
			envelope.ParseFromString(wsmessage)
			if envelope.type==1:
				logging.debug(str(envelope.nodeReadings.reading[0].node)+" "+str(envelope.nodeReadings.reading[0].doubleReading))
				binary1=binary
				# on received message
				reportedTemperature=float(envelope.nodeReadings.reading[0].doubleReading)
				logging.info(reportedTemperature)
				self.sendMessage(wsmessage, binary)
		except :
			logging.debug("error in onMessage")
	def onClose(self,wasClean, code, reason):
		logging.info("onClose")
                #os.system("gntp-send '0.I.9-1' 'onClose'")
		reconnect()

def weather_check( threadName, delay):
	global expectedTemperature
	patern="<p class=\"wx-temp\">"
	while 1:		
		html=urllib2.urlopen("http://www.weather.com/weather/today/GRXX0562").read()
		temp_in_fa=int(html[html.find(patern)+len(patern):html.find(patern)+len(patern)+4].replace('<',''))
		expectedTemperature=(temp_in_fa-32)/ (9.0/5.0)
		logging.info("expectedTemperature: "+str(expectedTemperature))
		time.sleep(delay)

def periodic_check( threadName, delay):
	global expectedTemperature
	global reportedTemperature
	global wsconnection
	while 1:
		time.sleep(delay)
		now = datetime.datetime.now()
		logging.info(str(now))
		if now.hour>18:
			continue;
		elif now.hour<7:
			continue;
		else:
			logging.info("checking")

		logging.info("expected: "+str(expectedTemperature) + ",reprted: "+str(reportedTemperature))
		if reportedTemperature > reportedThr :
                        logging.info("turning on")
                        urllib2.urlopen("http://uberdust.cti.gr/rest/sendCommand/destination/"+actuatornode+"/payload/"+actuatorPayload+"1")
		elif expectedTemperature > expectedThr :
			logging.info("turning on")
			urllib2.urlopen("http://uberdust.cti.gr/rest/sendCommand/destination/"+actuatornode+"/payload/"+actuatorPayload+"1")
		else:
			logging.info("turning off")
			urllib2.urlopen("http://uberdust.cti.gr/rest/sendCommand/destination/"+actuatornode+"/payload/"+actuatorPayload+"0")

def ping_task( threadName, delay):
	global wsconnection
	while 1:
		time.sleep(delay)
		logging.debug("ping")
		wsconnection.sendMessage("ping",binary1)

def initfromrest():
	global reportedTemperature
	from string import split
	reportedTemperature=float(split(urllib2.urlopen("http://uberdust.cti.gr/rest/testbed/1/node/"+temperaturenode+"/capability/"+temperaturecapability+"/latestreading").read())[1])
	logging.info("reportedTemperature= "+str(reportedTemperature))
def main(argv=None):
	global lasttime
	lasttime=time.time()
	initfromrest()
	try:
		thread.start_new_thread(weather_check, ("Thread-1", 600, ))
		thread.start_new_thread(periodic_check, ("Thread-2", 60, ))
		thread.start_new_thread(ping_task, ("Thread-3", 10, ))
	except:
		logging.error("Error: unable to create new thread")

	# initialize WebSocketClientFactory object and make connection
	reconnect()

def reconnect():
	while 1:
		try:
			urllib2.urlopen(http_URL)
		except urllib2.HTTPError, e:
			if e.code==406:
				break
		except urllib2.URLError, e:
			logging.info("server unavailable")	
		else:
			break
		time.sleep(10)
	
	# initialize WebSocketClientFactory object and make connection
	PROTOCOL =  [''.join(['SUB@',str(temperaturenode),'@',str(temperaturecapability)])]
	factory = WebSocketClientFactory(WS_URL,None,PROTOCOL)
	factory.protocol = NodeCapabilityConsumerProtocol
	factory.setProtocolOptions(13)
	connectWS(factory)
	if (reactor.running):
		logging.info("running")
	else:
		reactor.run()	

if __name__ == '__main__':
	sys.exit(main())
