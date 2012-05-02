import xmlrpclib
import os
import sys
import re

sourceFolder = sys.argv[1]
targetFolder = sys.argv[2]

sourceFolderList = os.listdir(sourceFolder)

server = xmlrpclib.ServerProxy("http://localhost:9000")

for fileName in sourceFolderList:
	if re.match(".*\.sbgn$",fileName) :
		print fileName
		server.Cytoscape.executeCommand(
				"network",
				"import",
				{
					"file" : sourceFolder+fileName,
					"createview" : True,
				}
		)

		server.Cytoscape.executeCommand(
				"network view",
				"fit",
				{
				}
		)

		server.Cytoscape.executeCommand(
				"network view",
				"export",
				{
					"file" : targetFolder+fileName+".png",
					"network" : "current",
					"type" : "png",
					"zoom" : "2.0"
				}
		)


