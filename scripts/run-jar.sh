#!/usr/bin/env bash

red=$(tput setaf 1)
green=$(tput setaf 2)
yellow=$(tput setaf 3)
blue=$(tput setaf 4)
cyan=$(tput setaf 6)

skip_tests=false
clean_before=true

function ask_for_build {
    echo "${cyan}[$0] ${green}Build spring application? (Y/n)"
    read -n1 -s -r build_response

    if ! echo "$build_response" | grep '^[Yy]\?$'; then
        exit 1
    fi

    echo "${cyan}[$0] ${green}Building..."

    if $skip_tests; then
        ./scripts/build-website.sh --clean-before
        ./mvnw package -Dmaven.test.skip=true
    else
        make build
    fi

    echo "${cyan}[$0] ${green}Done!"
}

# Check args

for arg in "$@"
do
    case $arg in
       "-S"|"--skip-tests")
          skip_tests=true
          ;;
       "-N"|"--no-clean")
          clean_before=false
          ;;
        "--help")
          echo "${blue}Usage: ${green}$0 [option...]" >&2
          echo
          echo "   ${cyan}-S, --skip-tests         ${green}Skip tests when building the application"
          echo "   ${cyan}-N, --no-clean           ${green}Don't remove 'target/' directory before building"
          echo
          exit 0
          ;;
    esac
done

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

echo
echo "${blue}Script: ${green}$0"
echo

# Setup script

## Check all requisites
if ! type java; then
    echo "${cyan}[$0] ${red}<java> command not found!"
    exit 1
fi

## Ref: https://stackoverflow.com/a/68071437
JAVA_MAJOR_VERSION=$(java -version 2>&1 | grep -oP 'version "?(1\.)?\K\d+' || true)
if [[ $JAVA_MAJOR_VERSION -lt 8 ]]; then
    echo "${cyan}[$0] ${red}Java 8 (1.8) or higher is required!"
    exit 1
else
    echo "${cyan}[$0] ${green}Java version: $JAVA_MAJOR_VERSION"
fi

if $clean_before; then
  echo "${cyan}[$0] ${green}Running 'make clean'!"
  make clean
fi

## Check if the application is ready to run.
if ! [ -d "target/" ]; then
    echo "${cyan}[$0] ${yellow}Directory 'target/' not found!"
    ask_for_build
fi

# Check if the jar file exist.
if [ 0 == "$(find target/ -name 'hawapi*.jar' | wc -l)" ] ; then
    echo "${cyan}[$0] ${yellow}Jar file (target/hawapi*.jar) not found!"
    ask_for_build
fi

# Run script.

echo "${cyan}[$0] ${green}Running application (jar)...!"
java -jar -Dspring.profiles.active=prod target/hawapi-*.jar