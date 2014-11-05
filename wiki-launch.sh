#!/bin/bash

# get absolute path to this script
THISPATH=$(pwd)/

# define the command to launch gollum
get_launch_command(){
    echo "gollum --collapse-tree --live-preview --gollum-path \"$THISPATH/wiki\" --css --js --ref vaadin --config wiki-config.rb"
}

# check whether the path is correct (it contains this script)
if [[ -e $THISPATH/$0 ]]; then
    # if the path is correct, launch:
    # echo $(get_launch_command)
    # echo "OK"
    eval $(get_launch_command)
else
    echo "Error: script could not correctly determine its path. Maybe bash is not properly supported on your platform? Either change the script to run on your system or invoke the launch command manually:"
    THISPATH="WIKI-REPO-PATH"
    echo "     $(get_launch_command)"
fi