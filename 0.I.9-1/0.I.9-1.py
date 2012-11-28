#!/usr/bin/python

"""0.I.9-1.py
Message-based WebSockets client that consumes node/capability updates from 
Uberdust server on http://uberdust.cti.gr.
"""

import sys
import getopt
from twisted.internet import reactor
from autobahn.websocket import WebSocketClientFactory, \
                               WebSocketClientProtocol, \
                               connectWS
import os
import time
import thread
import message
import urllib2
import logging
import inspect, os
fname=inspect.getfile(inspect.currentframe()).split('/')[-1].split('.py')[0]

logging.basicConfig(format='%(asctime)-15s %(levelname)s %(message)s',filename=fname+'.log',level=logging.INFO)

#FORMAT = '%(asctime)-15s %(clientip)s %(user)-8s %(message)s'
#logging.basicConfig(format=FORMAT)
#logger = logging.getLogger('0.I.9-1.py')

node="urn:wisebed:ctitestbed:gold"
capability="urn:wisebed:ctitestbed:node:capability:pir"
URL = '://uberdust.cti.gr:80/readings.ws'
WS_URL = 'ws'+URL
http_URL = 'http'+URL
PROTOCOL = []
lasttime = 0
wsconnection=0
binary1=0
screenState=True

logging.info('Starting application')

class NodeCapabilityConsumerProtocol(WebSocketClientProtocol):
	"""
	Node/Capability consumer protocol class.
	"""

	def onOpen(self):		
		global wsconnection
		wsconnection=self
		# on connection establish
		logging.info('WebSocket Connection to '+str(WS_URL)+' established.')	
	def onMessage(self, wsmessage, binary):		
		logging.debug("screen is locked!")		
	def onClose(self,wasClean, code, reason):
		logging.info("onClose")
		#reconnect()

def ping_task( threadName, delay):	
	global wsconnection
	while 1:
		time.sleep(delay)
		wsconnection.sendMessage("ping",binary1)	

def main(argv=None):
	global lasttime
	lasttime=time.time()
	"""Main routine of script"""
	if argv is None:
		argv = sys.argv
	try:

		# parse options and args
		opts, args = getopt.getopt(argv[1:], "", ["help","node=","capability="])
		logging.info("Node/Capability WebSocket consumer.")
		for k,v in opts:
			if k == "--help":
				print "A simple python script for consuming readings for a specific Node/Capability pair.\nHit CTRL-C to stop script at any time.\nMust provide all of the parameters listed bellow :"
				print "\t --node={node's URN}, define the  node."
				print "\t --capability={zone's ID}, define the node's zone."
				return 0
			elif k == "--node":
				node = v
			elif k == "--capability":
				capability = v


		#try:
		#thread.start_new_thread(periodic_check, ("Thread-1", 60, ))
		#thread.start_new_thread(screen_check, ("Thread-2", 60, ))
		#thread.start_new_thread(ping_task, ("Thread-3", 30, ))
		#except:
		#	logging.error("Error: unable to create new thread")

		# initialize WebSocketClientFactory object and make connection
		reconnect()
	except getopt.error, msg:
		print >>sys.stderr, msg
		print >>sys.stderr, "for help use -h or --help"
		return -1

def reconnect():
	while 1:
		try:
			urllib2.urlopen(http_URL)
		except urllib2.HTTPError, e:
			if e.code==406:
				break
		except urllib2.URLError, e:
			logging.info("server unavailable")
			#os.system("gntp-send '0.I.9-1' 'server unavailable'")
		else:
			break
		time.sleep(10)

	#os.system("gntp-send '0.I.9-1' 'connecting'")
	# initialize WebSocketClientFactory object and make connection
	PROTOCOL =  [''.join(['SUB@',str(node),'@',str(capability)])]
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
