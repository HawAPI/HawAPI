#!/usr/bin/env bash

red=$(tput setaf 1)
green=$(tput setaf 2)
yellow=$(tput setaf 3)
cyan=$(tput setaf 6)

# Check location

if ! [ -d "src/" ]; then
    cd ..
    echo ''
    echo "${yellow}Unknown location! Moving to: $PWD"

    if ! [ -d "src/" ]; then
        echo ''
        echo "${red}Unknown location! Exiting"
        exit 1
    fi
fi

echo "${cyan}[$0] ${green}Removing '.hawapi/' folder..."
rm -rf ../../.hawapi/

echo "${cyan}[$0] ${green}Removing '.downloads/' folder..."
rm -rf ../../.downloads/

echo "${cyan}[$0] ${green}Removing 'resources/static/' folder..."
rm -rf ../../src/main/resources/static/