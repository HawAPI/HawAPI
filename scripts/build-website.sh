#!/usr/bin/env bash

org_name="HawAPI"
website_repository="website"
docs_repository="docs"
website_zip="https://github.com/${org_name}/${website_repository}/archive/refs/heads/main.zip"
docs_zip="https://github.com/${org_name}/${docs_repository}/archive/refs/heads/main.zip"

red=$(tput setaf 1)
green=$(tput setaf 2)
yellow=$(tput setaf 3)
blue=$(tput setaf 4)
cyan=$(tput setaf 6)

clean_before=false

# Check args

for arg in "$@"
do
    if [[ $arg == "--clean-before" ]]
    then
        clean_before=true
    fi

    if [[ $arg == "--help" ]]
    then
        echo "${blue}Usage: ${green}$0 [option...]" >&2
        echo
        echo "   ${cyan}-B, --clean-before           ${green}Remove '.hawapi/' directory before building the website"
        echo "   ${cyan}-H, --clean-hawapi           ${green}Remove '.hawapi/' directory"
        echo "   ${cyan}-D, --clean-downloads        ${green}Remove '.downloads/' directory"
        echo "   ${cyan}-S, --clean-static           ${green}Remove 'resources/static/' directory"
        echo "   ${cyan}-A, --clean-all              ${green}Remove all directories related with website build"
        echo
        exit 0
    fi
done

# Clean

## Remove '.hawapi/' before building website
if $clean_before; then
    echo "${cyan}[$0] ${green}Removing '.hawapi/' folder..."
    echo "$PWD"
    rm -rf .hawapi/
fi

# Check location

if ! [ -d "src/" ]; then
    echo "${yellow}Unknown location! Cancelling"
    exit 1
fi

# Start script

echo
echo "${blue}Script: ${green}$0"
echo
echo "${cyan}[$0] ${green}See all requisites: https://github.com/HawAPI/HawAPI/blob/main/GETTING-STARTED.md#prerequisites"
echo "${cyan}[$0] ${green}Checking prerequisites for building website..."
echo

# Check all requisites

if ! type yarn; then
    ## Yarn is required. Ask to install (LOCALLY)
    echo "${cyan}[$0] ${red}<Yarn> command not found!"
    echo "${cyan}[$0] ${green}Install yarn to build the website"
    exit 1
fi

echo
echo "${cyan}[$0] ${green}All requisites found!"

## Check if '.hawapi/website' already exist. If not, create it.
if ! [ -d ".hawapi/website" ]; then
    echo "${cyan}[$0] ${green}Directory '.hawapi/website' not found!"

    ## Check if '.downloads/' already exist. If not, download the project from git repository.
    if ! [ -d ".downloads/" ]; then
        echo "${cyan}[$0] ${green}Directory '.downloads/' not found!"
        mkdir -p .downloads/
        wget ${website_zip} -q --show-progress -P .downloads/
        echo "${cyan}[$0] Downloading '${org_name}/${website_repository}' from Github..."
        wget ${docs_zip} -q --show-progress -P .downloads/
        echo "${cyan}[$0] Downloading '${org_name}/${docs_repository}' from Github..."
    fi

    echo "${cyan}[$0] ${green}Extracting files into '.hawapi/website'..."
    mkdir -p .hawapi/website
    unzip -q .downloads/${website_repository}-main.zip -d .hawapi/website/
    unzip -q .downloads/${docs_repository}-main.zip -d .hawapi/docs/
    mv .hawapi/website/${website_repository}-main/* .hawapi/website/
    mv .hawapi/docs/${docs_repository}-main/* .hawapi/website/docs/
    rm -rf .hawapi/website/${website_repository}-main/
    rm -rf .hawapi/docs/
fi

echo "${cyan}[$0] ${green}Building the website..."
cd .hawapi/website/ || exit 1

## Yarn install into website and docs
cd ./docs && yarn install
yarn install

## Build website and docs
echo
yarn build:all
echo

# Finalization

## Spring root
cd ../../

if [ -d "./src/main/resources/static/" ]; then
    echo "${cyan}[$0] ${green}Found files inside 'resources/static/'! Deleting all..."
    rm -rf ./src/main/resources/static/*
fi

if ! [ -d "./src/main/resources/static/" ]; then
    mkdir -p ./src/main/resources/static/
fi

echo "${cyan}[$0] ${green}Moving files from '.hawapi/website/build/' to 'resources/static/'"
mv .hawapi/website/build/* ./src/main/resources/static/

# Clean

./scripts/clean-website.sh "$@"