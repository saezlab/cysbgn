#!/bin/bash

echo "[ANT]"
echo ""
ant jar

echo "[MOVE JAR]"
echo ""
chmod +x sbgnplugin.jar
mv sbgnplugin.jar /Applications/Cytoscape_v2.8.2/plugins/
