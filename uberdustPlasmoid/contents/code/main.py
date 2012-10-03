# -*- coding: utf-8 -*-
# <Copyright and license information goes here.>
from PyQt4.QtCore import *
from PyQt4.QtGui import *
from PyKDE4.plasma import Plasma
from PyKDE4 import plasmascript
import urllib2, urllib 
from time import ctime,time,sleep
from os import chdir,path
from string import replace
from time import time
from threading import Timer

server=""
node=""
capabilities=""
prevtime=0
text=""
label=""


class HelloPython(plasmascript.Applet):
	
	def __init__(self,parent,args=None):
		plasmascript.Applet.__init__(self,parent)

		
	def init(self):
		global label
		global meter
		global node
		global WS_URL
		self.setHasConfigurationInterface(False)
		self.setAspectRatioMode(Plasma.Square)
		self.layout = QGraphicsLinearLayout(Qt.Vertical, self.applet)
		readProperties()
		mtitle = Plasma.Label(self.applet)
		self.layout.addItem(mtitle)
		label = Plasma.Label(self.applet)
		meter = Plasma.Slider(self.applet)
		meter.setRange(0,1000)
		meter.setValue(100)
		meter.setOrientation(1)
		self.layout.addItem(meter)
		mtitle.setText("Desired Luminosity")
		label.setText("message")
		self.layout.addItem(label)
		self.resize(400, 400)
		self.startTimer(1000)

		
	def timerEvent(self, event):
		global server
		global node
		global capabilities
		global text
		global prevtime
		global label
		global meter
		global text
		nowtime=time()
		readProperties()
		text = node+"\n"
		pir=urllib.urlopen("http://"+server+"rest/testbed/1/node/"+node+"/capability/urn:wisebed:node:capability:pir/latestreading")
		read=pir.read()
		print read
		pirValue=read.split("\t")[1]
		pirTime=read.split("\t")[0]
		text=text+"urn:wisebed:node:capability:pir "+pirValue+" @"+ pirTime+"\n"

		light=urllib.urlopen("http://"+server+"rest/testbed/1/node/"+node+"/capability/urn:wisebed:node:capability:light/latestreading")
		lightRead=light.read()
		print lightRead
		lightValue=lightRead.split("\t")[1]
		lightTime=lightRead.split("\t")[0]
		text=text+"urn:wisebed:node:capability:light "+lightValue+" @"+ lightTime+"\n"
		
		if meter.value()>float(lightValue) :
			text=text+"need to turn on \n"
		else :
			text=text+"need to turn off \n" 
		prevtime=nowtime

	def paintInterface(self, painter, option, rect):
		global text
		label.setText(text+"\n")
		
def periodic_check():
	print "running"

def print_time(): 
	print "From print_time"
	time()
	
def CreateApplet(parent):		
	return HelloPython(parent)
	
def readProperties():
		global server
		global node
		global capabilities
		homedir = path.expanduser('~')
		f = open(homedir+'/.uberdust', 'r')
		lines=f.readlines()
		print lines
		server=replace(lines[0],'\n','')
		node=replace(lines[1],'\n','')
		capabilities=lines[2].split(",")

