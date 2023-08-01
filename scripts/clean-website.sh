#!/usr/bin/env bash

red=$(tput setaf 1)
green=$(tput setaf 2)
yellow=$(tput setaf 3)
blue=$(tput setaf 4)
cyan=$(tput setaf 6)

clean_hawapi=false
clean_downloads=false
clean_static=false

# Check location

if ! [ -d "src/" ]; then
    cd ..
    echo
    echo "${yellow}Unknown location! Moving to: $PWD"

    if ! [ -d "src/" ]; then
        echo
        echo "${red}Unknown location! Exiting"
        exit 1
    fi
fi

# Check args

for arg in "$@"
do
    case $arg in
       "-H"|"--clean-hawapi")
          clean_hawapi=true
          ;;
       "-D"|"--clean-downloads")
          clean_downloads=true
          ;;
       "-S"|"--clean-static")
          clean_static=true
          ;;
       "-A"|"--clean-all")
          clean_hawapi=true
          clean_downloads=true
          clean_static=true
          ;;
        "--help")
          echo "${blue}Usage: ${green}$0 [option...]" >&2
          echo
          echo "   ${cyan}-H, --clean-hawapi           ${green}Remove '.hawapi/' directory"
          echo "   ${cyan}-D, --clean-downloads        ${green}Remove '.downloads/' directory"
          echo "   ${cyan}-S, --clean-static           ${green}Remove 'resources/static/' directory"
          echo "   ${cyan}-A, --clean-all              ${green}Remove all directories related with website build"
          echo
          exit 0
          ;;
    esac
done

echo
echo "${blue}Script: ${green}$0"
echo

## Return 'warn' if no arg found
if ! $clean_hawapi && ! $clean_hawapi && ! $clean_hawapi; then
    echo "${cyan}[$0] ${yellow}Nothing to clean! Use --help to see all options"
fi

if $clean_hawapi; then
    echo "${cyan}[$0] ${green}Removing '.hawapi/' folder..."
    rm -rf .hawapi/
fi

if $clean_downloads; then
    echo "${cyan}[$0] ${green}Removing '.downloads/' folder..."
    rm -rf .downloads/
fi

if $clean_static; then
    echo "${cyan}[$0] ${green}Removing 'resources/static/' folder..."
    rm -rf src/main/resources/static/
fi