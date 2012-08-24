#!/bin/bash

echo "[ANT]"
echo ""
ant jar

echo "[MOVE JAR]"
echo ""
chmod +x CySBGN-v1.1.jar
mv CySBGN-v1.1.jar /Applications/Cytoscape_v2.8.3/plugins/
