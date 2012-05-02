#!/usr/bin/python

"""
Rendering test-suite for LibSBGN.

Compares various renderers and generates a html table where
all diagrams can be compared side-by-side. That way
deficiencies in renderers can be spotted (and hopefully fixed)
easily.
"""

from subprocess import check_call, call;

import glob;
import os.path;
import os;
import sys
import shutil
import tempfile;
import datetime;
import xmlrpclib;
import time;
import signal;
import socket;


def print_header(ostream):
	ostream.write ("""<html>
<head>
<title></title>
</head>
<body>
""")

def print_footer(ostream):
	ostream.write ("""<p align="right"><small>LibSBGN rendering comparison created by Martijn van Iersel</br>
Generated: """ + str(datetime.date.today()) + """</small></p>
	</body>
</html>""")	

class RenderExtension:
	header = "<a href=\"http://sbmllayout.sf.net\">SBMLLayout@sf.net</a>"
	
	def first(self):
	    pass
	    
	def prepare(self, testfiles):
		pass
	
	def get_outbase (self, infile):
		base = os.path.basename(infile);
		return base.replace (".sbgn", "-rendext.png")
		
	def render(self, infile, outdir):
		print (infile);
		base = os.path.basename(infile);
		url = "http://sysbioapps.dyndns.org/RenderComparison/Home/UploadFile"
		outbase = self.get_outbase(infile)
		outfile = outdir + "/" + outbase
		cmd = [ "curl", "--max-time", "120", "--retry", "3", "-F", 'file=@' + infile, url, "-o", outfile ]
		#~ cmd = [ "wget", url, "-O", outfile, "-t", "3", "-T", "120", "--post-file=" + infile ]
		print cmd
		retcode = call(cmd)
		if (retcode != 0):
			sys.stderr.write ("<span color=\"red\">error</style>")
		return
	
	def done(self):
		pass

	def last(self):
		pass

class Reference:
	header = "Reference"
	
	def first(self):
	    pass

	def prepare(self, testfiles):
		pass

	def get_outbase (self, infile):
		base = os.path.basename(infile);
		return base.replace (".sbgn", ".png")
		
	def render(self, infile, outdir):
		reference = self.get_outbase(infile)
		shutil.copy (infile.replace (".sbgn", ".png"), outdir)
	
	def done(self):
		pass

	def last(self):
		pass

class PathVisio:
	header = "<a href=\"http://www.pathvisio.org\">PathVisio</a>"
	
	def first(self):
	    pass

	def prepare(self, testfiles):
		pass

	def get_outbase (self, infile):
		base = os.path.basename(infile);
		return base.replace (".sbgn", "-pathvisio.png")
		
	def render(self, infile, outdir):
		base = os.path.basename(infile);
		outbase = self.get_outbase(infile)
		outfile = outdir + "/" + outbase
		cmd = [ "java", "-jar", "/home/martijn/buildsystem/libsbgn/work/PV-SBGN-PLUGIN/dist/SBGN.jar", infile, outfile ]
		print cmd;
		retcode = call(cmd)
		if (retcode != 0):
			sys.stderr.write ("<span color=\"red\">error</style>")
		return

	def done(self):
		pass

	def last(self):
		pass


class Cytoscape:
	header = "<a href=\"http://www.cytoscape.org\">Cytoscape</a>"
	server = xmlrpclib.ServerProxy("http://localhost:9000");
	
	def first(self):
		child_pid = os.fork();
		if not child_pid :
			try:
				cmd = ["java", "-Xmx2048m", "-jar", "/Applications/Cytoscape_v2.8.2/cytoscape.jar", "-p", 
				"/Applications/Cytoscape_v2.8.2/plugins/"]
				call(cmd)
			except:
				pass
		else:
			message = ""
			times = 0
			while message != "It works!" :
				time.sleep(2)
				times += 1
				try:
					message = self.server.Cytoscape.test()
				except socket.error:
					pass
				if (times > 30):
					raise Error("Time out while attempting to connect to cytoscape")
			print message
			time.sleep(8)

	def prepare(self, testfiles):
	    pass

	def get_outbase (self, infile):
		base = os.path.basename(infile);
		return base.replace (".sbgn", "-cytoscape.png")
		
	def render(self, infile, outdir):
		base = os.path.basename(infile);
		outbase = self.get_outbase(infile)
		outfile = outdir + "/" + outbase
	
		self.server.Cytoscape.executeCommand(
				"network",
				"import",
				{
					"file" : infile,
					"createview" : True,
				}
		)

		self.server.Cytoscape.executeCommand(
				"network view",
				"fit",
				{
				}
		)

		self.server.Cytoscape.executeCommand(
				"network view",
				"export",
				{
					"file" : outfile,
					"network" : "current",
					"type" : "png",
					"zoom" : "2.0"
				}
		)	

	def done(self):
		pass
		
	def last(self):
		time.sleep(2)
		try:
			self.server.Cytoscape.executeCommand(
					"quit",
					"",
					{
					}
			)
		except:
			pass


class Sbgned:
	sbgnedTempDir = "";

	def first(self):
		pass
	
	def prepare(self, testfiles):
		self.sbgnedTempDir = tempfile.mkdtemp()
		
		for file in testfiles:
			shutil.copy (file, self.sbgnedTempDir)
		
		cmd = ["java", "-Xmx1024m", "-jar", 
			"/home/martijn/etc/vanted-with-sbgn-ed/vanted_with_sbgn-ed.jar", self.sbgnedTempDir]
		print cmd
		retcode = call(cmd)
		if (retcode != 0):
			print "ERROR"

	header = "<a href=\"http://vanted.ipk-gatersleben.de\">SBGN-ED</a>"

	def get_outbase (self, infile):
		base = os.path.basename(infile);
		return base.replace (".sbgn", "-sbgned.png")

	def render(self, infile, outdir):
		base = os.path.basename(infile);
		tmpbase = base.replace (".sbgn", ".png")
		tmpfile = self.sbgnedTempDir + "/" + tmpbase
		outbase = self.get_outbase (infile)
		outfile = outdir + "/" + outbase
		try:
			shutil.copy (tmpfile, outfile)
		except IOError:
			sys.stderr.write ("ERROR")
		
	def done(self):
		shutil.rmtree(self.sbgnedTempDir)

	def last(self):
		pass
		
def html_report(outdir, renderers, locations):
	tmpfile = outdir + "/index.html.tmp"
	freport = open(tmpfile, 'w')

	print_header(freport)
	
	freport.write ('<h2>Table of contents</h2>\n<ul>\n')
	for loc in locations:
		freport.write ('<li><a href="#' + loc + '">' + loc + '</a></li>\n')

	freport.write ('</ul>\n')

	for loc in locations:
		testfiles = glob.glob (indir + "/" + loc + "/*.sbgn");

		freport.write ('<a name="' + loc + '"><h2>' + loc + "</h2>\n")
		freport.write ("<table>\n<tr>")

		for renderer in renderers:
			freport.write ("<td>")
			freport.write (renderer.header)
			freport.write ("</td>")

		freport.write ("</tr>")

		for infile in testfiles:
			base = os.path.basename(infile);
			freport.write ("<tr bgcolor=\"cyan\">" + 
				"<td colspan=\"4\"><a name=\"" + base + "\">" + base + "</a></td></tr>" +
				"<tr>\n")
			
			for renderer in renderers:
				freport.write ("<td>")
				freport.write ("<img src=\"" + os.path.join (loc, renderer.get_outbase(infile)) + "\" width=\"400\">")
				freport.write ("</td>\n")
			
			freport.write ("</tr>\n")

		freport.write ("</table>")

	print_footer(freport)

	freport.close;
	shutil.move (tmpfile, outdir + "/index.html")


def update_renderer(renderer, locations):
	renderer.first()
	
	for loc in locations:
		mydir = indir + "/" + loc + "/*.sbgn"
		testfiles = glob.glob (mydir);
		renderer.prepare(testfiles)

		for infile in testfiles:
			localoutdir = os.path.join(outdir, loc)
			if not os.path.exists(localoutdir):
				os.makedirs(localoutdir)
			renderer.render(infile, localoutdir)
	
		renderer.done()

	renderer.last()
	

indir = sys.argv[1]
outdir = sys.argv[2]

if not os.path.isdir(outdir):
	os.makedirs (outdir);

# temporary file will be renamed after write has finished.

#locations = ["trunk/test-files/ER", "trunk/test-files/PD", "trunk/test-files/AF", "tags/milestone1/test-files"]
locations = ["ER", "PD", "AF"]

ref = Reference()
pv = PathVisio()
re = RenderExtension()
sed = Sbgned()
cy = Cytoscape()

render_map = {
	'ref': ref,
	'pv': pv,
	're': re,
	'sed' : sed,
	'cy' : cy
}
all_renderers = [ ref, pv, re, sed, cy ];

if (len(sys.argv) < 3):
	#active_renderers = [ 'ref', 'pv', 're', 'sed', 'cy' ];
	active_renderers = [ 'ref', 'cy' ];
else:
	active_renderers = sys.argv[3:]

for i in active_renderers:
	update_renderer(render_map[i], locations)

html_report(outdir, [ ref, cy ], locations)
