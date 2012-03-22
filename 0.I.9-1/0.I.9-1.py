#!/usr/bin/python

"""node-capability-ws-consumer.py

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
	
WS_URL = 'ws://uberdust.cti.gr:80/readings.ws'
PROTOCOL = []
lasttime = 0
wsconnection=0
binary1=0

class NodeCapabilityConsumerProtocol(WebSocketClientProtocol):
	"""
	Node/Capability consumer protocol class.
	"""

	def onOpen(self):
		global wsconnection
		wsconnection=self
		# on connection establish
		print 'WebSocket Connection to ',WS_URL,' established.'		
	def onMessage(self, wsmessage, binary):
		global lasttime
		global binary1		
		envelope = message.Envelope()
		envelope.ParseFromString(wsmessage)
		if envelope.type==1:
			print envelope.nodeReadings.reading[0].timestamp
			#print readings[0].node
			binary1=binary
			# on received message
			nowtime=envelope.nodeReadings.reading[0].timestamp
			diff=nowtime-lasttime
			if diff > 20 :
				os.system("wget http://uberdust.cti.gr/rest/sendCommand/destination/urn:wisebed:ctitestbed:0x4ec/payload/1,1,1 -O /dev/null")
				#os.system("notify-send --expire-time=1 turning on")
				#print "turning on after",diff
				lasttime=nowtime
				self.sendMessage(wsmessage, binary)
		#if diff > 30 :
		#print 'Message received [',wsmessage,'] '		
		#lasttime=millis
	def onClose(self,wasClean, code, reason):
		# on close connection
		if(reactor.running):
			print 'Closing connection to ',WS_URL,' ',code,' ',reason
			reactor.stop()

def periodic_check( threadName, delay):
	global lasttime
	global wsconnection
	while 1:
		time.sleep(delay)
                # on received message
                nowtime=time.time()
                diff=nowtime-lasttime
		wsconnection.sendMessage("ping",binary1)
		if diff >  90:
                        os.system("wget http://uberdust.cti.gr/rest/sendCommand/destination/urn:wisebed:ctitestbed:0x4ec/payload/1,1,0 -O /dev/null")
                        #os.system("notify-send --expire-time=1 turning off")
                        print "turning off after",diff


		

def main(argv=None):
	"""Main routine of script"""
	if argv is None:
		argv = sys.argv
	try:

		# parse options and args
		node="urn:wisebed:ctitestbed:0x9979"
		capability="urn:wisebed:node:capability:pir"
		opts, args = getopt.getopt(argv[1:], "", ["help","node=","capability="])
		print "Node/Capability WebSocket consumer."
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


		try:
			thread.start_new_thread(periodic_check, ("Thread-1", 90, ))
		except:
			print "Error: unable to create new thread"

		# initialize WebSocketClientFactory object and make connection
		PROTOCOL =  [''.join(['SUB@',str(node),'@',str(capability)])]
		factory = WebSocketClientFactory(WS_URL,None,PROTOCOL)
		factory.protocol = NodeCapabilityConsumerProtocol
		factory.setProtocolOptions(13)
		connectWS(factory)
		reactor.run()		
	except getopt.error, msg:
		print >>sys.stderr, msg
		print >>sys.stderr, "for help use -h or --help"
		return -1

if __name__ == '__main__':
	sys.exit(main())
