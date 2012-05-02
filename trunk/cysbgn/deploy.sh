#!/bin/bash

echo "[ANT]"
echo ""
ant jar

echo "[MOVE JAR]"
echo ""
chmod +x sbgnplugin.jar
mv sbgnplugin.jar /Applications/Cytoscape_v2.8.2/plugins/

echo "[RUN CYTOSCAPE]"
echo ""
java -Xmx1024m -jar /Applications/Cytoscape_v2.8.2/cytoscape.jar -p /Applications/Cytoscape_v2.8.2/plugins/sbgnplugin.jar
