#!/bin/bash

echo "[ANT]"
echo ""
ant jar

echo "[MOVE JAR]"
echo ""
chmod +x CySBGN-v1.2.jar
mv CySBGN-v1.2.jar /Applications/Cytoscape_v2.8.3/plugins/
