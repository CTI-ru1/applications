# -*- coding: utf-8 -*-
# <Copyright and license information goes here.>
from PyQt4.QtCore import Qt
from PyKDE4.plasma import Plasma
from PyKDE4 import plasmascript
import urllib2, urllib 
from time import ctime
from os import chdir,path

server=""
node=""
capabilities=""

class HelloPython(plasmascript.Applet):
	
	def __init__(self,parent,args=None):
		plasmascript.Applet.__init__(self,parent)
		
	def init(self):
		self.setHasConfigurationInterface(False)
		self.resize(200, 150)
		self.setAspectRatioMode(Plasma.Square)

	def paintInterface(self, painter, option, rect):
		global server
		global node
		global capabilities
		readProperties()
		painter.save()
		painter.setPen(Qt.black)		
		temp=urllib.urlopen(server+"rest/testbed/1/node/"+node+"/capability/urn:wisebed:node:capability:temperature/latestreading")
		read=temp.read()
		print read
		temperature=read.split("\t")[1]
		time=read.split("\t")[0]
		temp=urllib.urlopen(server+"rest/testbed/1/node/"+node+"/capability/urn:wisebed:node:capability:light/latestreading")
		light=temp.read().split("\t")[1]
		temp=urllib.urlopen(server+"rest/testbed/1/node/"+node+"/capability/urn:wisebed:node:capability:pir/latestreading")
		pir=temp.read().split("\t")[1]
		painter.drawText(rect, Qt.AlignVCenter | Qt.AlignHCenter, "Hello Uberdust!"+"\nTemp "+temperature+"\nLight "+light+"\nPir "+pir +"\n"+ctime(float(time)/1000) )		
		painter.restore()


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
		from string import replace
		server=replace(lines[0],'\n','')
		node=replace(lines[1],'\n','')
		capabilities=lines[2].split(",")

